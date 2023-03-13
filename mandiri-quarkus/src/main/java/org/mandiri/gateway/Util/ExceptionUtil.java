package org.mandiri.gateway.Util;

import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

public class ExceptionUtil {
    private ExceptionUtil(){
        //do nothing
    }

    public static Object ExceptionExceptionFormatter(String uri, LocalDateTime reqAt, JsonObject reqBody,
                                                     JsonObject respBody, java.lang.Exception ex, Object expType) {
        return ExceptionExceptionFormatter(uri, reqAt, reqBody, LocalDateTime.now(), respBody, ex, expType);
    }
}
