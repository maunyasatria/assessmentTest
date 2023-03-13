package org.mandiri.gateway.Service;

import io.vertx.core.json.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.mandiri.gateway.Constant.ConstantVariable;
import org.mandiri.gateway.ErrorList.ErrorListConstant;
import org.mandiri.gateway.Exception.ThrowableException;
import org.mandiri.gateway.Kafka.KafkaAdminClient;
import org.mandiri.gateway.Util.GlobalVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.ValidationException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class Services {
    private static final Logger LOG = LoggerFactory.getLogger(Services.class);

    @ConfigProperty(name = "url.get.token")
    private String urlGetToken;

    @ConfigProperty(name = "url.generate.qr")
    private String urlGetQR;

    @ConfigProperty(name = "url.check.status.qr")
    private String urlCheckStatusQr;

    @ConfigProperty(name = "auth.client.id")
    private String clientId;

    @ConfigProperty(name = "auth.client.secret")
    private String clientSecret;

    @ConfigProperty(name = "auth.shared.key")
    private String sharedKey;

    @ConfigProperty(name = "fee.type")
    private String fType;

    @Inject
    HmacSha1 words;

    @Inject
    @Channel("callback-notification")
    Emitter<String> checkStatsQr;

    public JsonObject doNotifyPayment(UriInfo uriInfo, JsonObject payload) throws ValidationException, ThrowableException,
            SignatureException, NoSuchAlgorithmException, InvalidKeyException, ParseException {
		LocalDateTime reqAt = LocalDateTime.now();

        String kafkaServer = ConfigProvider.getConfig().getValue("kafka.bootstrap.servers", String.class);
        boolean isKafkaActive = new KafkaAdminClient(kafkaServer).verifyConnectionMain();

        JsonObject response = new JsonObject();
        if (isKafkaActive) {
            JsonObject responseKafka = paymentNotify(uriInfo, payload);

            System.out.println(
                    "=================================================== SEND KAFKA START ====================================");
            // If valid (no error thrown), send kafka
            checkStatsQr.send(responseKafka.encode());
            System.out.println(
                    "=================================================== SEND KAFKA END ====================================");

            response.put("status", "0000");
            response.put("message", "QR payment is being processed");
        } else
            response = ErrorListConstant.KAFKA_OFFLINE;

        return response;
    }

    public JsonObject generateQR(JsonObject payload)
            throws ThrowableException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        JsonObject respData = new JsonObject();

        Map<String, Object> fields = new HashMap<>();
        fields.put("clientId", clientId);
        fields.put("accessToken", GlobalVariable.accessToken);
        fields.put("words", words.calculateWords(
                clientId.concat(GlobalVariable.uuid).concat(clientId).concat(sharedKey), clientSecret));
        fields.put("fType", fType);

        LOG.info("request QR parameter\n" + fields.toString());

        HttpResponse<String> response = Unirest.post(urlGetQR).fields(fields).asString();

        if (response.getStatus() == 200) {
            JsonObject dokuResp = new JsonObject(response.getBody());
            if (dokuResp.getString("responseCode").equalsIgnoreCase("0000")) {
                respData.put("qrCode", dokuResp.getString("qrCode"));
                respData.put("detailResp", dokuResp.encode());
            } else
                throw new ThrowableException(
                        ErrorListConstant.ERROR_CALL_SERVICE.put("message", response.getBody()).encode());
        } else
            throw new ThrowableException(ErrorListConstant.ERROR_CALL_SERVICE.put("message", response.getBody()).encode());
        return respData;
    }

    public JsonObject checkStatusQR(UriInfo context, JsonObject payload)
            throws ThrowableException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        JsonObject respData = new JsonObject();

        HttpResponse<String> response = Unirest.post(urlCheckStatusQr).field("clientId", clientId)
                .field("accessToken", GlobalVariable.accessToken).field("dpMallId", clientId)
                .field("responseType", "json").asString();

        if (response.getStatus() == 200) {
            JsonObject dokuResp = new JsonObject(response.getBody());
            if (dokuResp.getString("responseCode").equalsIgnoreCase("0000")) {
                respData.put("status", "0000");
                respData.put("message", dokuResp.getString("responseMessage"));
            } else if (dokuResp.getString("responseCode").equalsIgnoreCase("3006")) {
                respData.put("chargeStatus", ConstantVariable.PAYMENT_STATUS_WAITING);
            } else
                throw new ThrowableException(
                        ErrorListConstant.ERROR_CALL_SERVICE.put("message", response.getBody()).encode());
        } else
            throw new ThrowableException(ErrorListConstant.ERROR_CALL_SERVICE.put("message", response.getBody()).encode());
        return respData;
    }

    public JsonObject paymentNotify(UriInfo context, JsonObject payload) throws ParseException {
        JsonObject respData = new JsonObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        respData.put("amount",Float.parseFloat(payload.getString("AMOUNT")));
        respData.put("chargeStatus", ConstantVariable.PAYMENT_STATUS_SUCCESS);
        respData.put("paymentDateTime", sdf2.format(sdf.parse(payload.getString("TXNDATE"))));
        respData.put("detailResp", payload.encode());
        return respData;
    }
}
