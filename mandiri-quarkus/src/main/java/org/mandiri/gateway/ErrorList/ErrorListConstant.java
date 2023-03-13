package org.mandiri.gateway.ErrorList;

import io.vertx.core.json.JsonObject;

public class ErrorListConstant {
    public static final JsonObject ERROR_CALL_SERVICE = gen("ERROR_CALL_SERVICE", " service return error");
    public static final JsonObject MISSING_REQUIRED_FIELD = gen("MISSING_REQUIRED_FIELD", "Missing field(s)");

    public static final JsonObject SERVER_ERROR = gen("INTERNAL SERVER ERROR", "Internal Server Error");

    public static final JsonObject KAFKA_OFFLINE = gen("KAFKA_OFFLINE", "Kafka server is offline");

    public static final JsonObject INVALID_WORDS = gen("INVALID_WORDS", "words invalid");

    public static final JsonObject ERROR_INVALID_RANGE = gen("ERROR_INVALID_RANGE", "Data range is invalid");

    public static final JsonObject WRONG_VALUE_FIELD = gen("WRONG_VALUE_FIELD", "Nilai yang anda masukkan tidak cocok\nInput not match the criterias");


    private static JsonObject gen(String errCode, String message) {
        return new JsonObject().put("error_code", errCode).put("message", message);
    }
}
