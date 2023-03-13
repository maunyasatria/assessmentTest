package org.mandiri.gateway.Controller;

import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.jboss.resteasy.reactive.RestForm;
import org.mandiri.gateway.Exception.ThrowableException;
import org.mandiri.gateway.Service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.ValidationException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;


@Path("/mandiri")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Controller {

    @Path("/qr")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecuritySchemes(value = {
            @SecurityScheme(securitySchemeName = "accessToken", type = SecuritySchemeType.HTTP, scheme = "bearer", apiKeyName = "Authorization: Bearer", bearerFormat = "jwt")})
    public class DokuPaymentController {
        private static final Logger LOG = LoggerFactory.getLogger(DokuPaymentController.class);

        @Inject
        Services services;

        @Path("/v1/enrollment")
        @POST
        @Operation(summary = "generate QR", description = "generating QR string")
        public Response generateQr(JsonObject payload) throws ThrowableException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
            JsonObject resp = services.generateQR(payload);
            return Response.ok(resp).build();
        }

        @Path("/v1/check-status")
        @POST
        @Operation(summary = "Check Status Payment QR", description = "Check Status Payment QR")
        public Response checkStatusQR(@Context UriInfo context, JsonObject payload) throws ThrowableException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, ValidationException {
            JsonObject resp = services.checkStatusQR(context, payload);
            return Response.ok(resp).build();
        }

        @Path("/v1/payment-notify")
        @POST
        @Operation(summary = "Get Payment Notification QR", description = "Get Payment Notification QR")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public Response paymentNotify(@Context UriInfo context, @RestForm("CUSTOMERPAN") String CUSTOMERPAN,
                                      @RestForm("TRANSACTIONID") String TRANSACTIONID,
                                      @RestForm("TXNDATE") String TXNDATE,
                                      @RestForm("ISSUERNAME") String ISSUERNAME,
                                      @RestForm("AMOUNT") String AMOUNT,
                                      @RestForm("TXNSTATUS") String TXNSTATUS,
                                      @RestForm("INVOICE") String INVOICE) throws ValidationException, ThrowableException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, ParseException {
            LOG.info("======================== payment notify==========================");

            JsonObject payload = new JsonObject();
            payload.put("TRANSACTIONID", TRANSACTIONID);
            payload.put("TXNDATE", TXNDATE);
            payload.put("ISSUERNAME", ISSUERNAME);
            payload.put("AMOUNT", AMOUNT);
            payload.put("TXNSTATUS", TXNSTATUS);
            payload.put("INVOICE", INVOICE);

            LOG.info("PAYLOAD  Dari depan berupa String text {}", payload);

            JsonObject resp = services.doNotifyPayment(context, payload);

            return Response.ok(resp).build();
        }

    }
}