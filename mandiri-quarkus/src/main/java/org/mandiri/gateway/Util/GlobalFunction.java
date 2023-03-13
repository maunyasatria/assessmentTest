package org.mandiri.gateway.Util;


import io.vertx.core.json.JsonObject;

import java.sql.Timestamp;
import java.util.List;

public class GlobalFunction {
    private GlobalFunction() {
        // do nothing
    }

    public static boolean isNullOrBlank(JsonObject json, List<String> list, Object type)
            throws ClassCastException {
        for (String string2 : list) {
            if (!json.containsKey(string2))
                return true;

            if (type == String.class) {
                if (json.getString(string2) == null || json.getString(string2).isBlank())
                    return true;
            } else if (type == Float.class) {
                if (json.getFloat(string2) == null)
                    return true;
            } else if (type == Boolean.class) {
                if (json.getBoolean(string2) == null)
                    return true;
            } else if (type == JsonObject.class) {
                if (json.getJsonObject(string2).isEmpty())
                    return true;
            }
        }
        return false;
    }

    public static boolean isNullOrBlankOne(String value) {
        if(value == null || value.isBlank()) return true;
        return false;
    }

    public static boolean isNullOrBlankOne( Float value) {
        if(value == null) return true;
        return false;
    }

    public static boolean isPresent(JsonObject payload, String key) {
        if(payload.containsKey(key) && !isNullOrBlankOne(payload.getString(key))) return true;
        return false;
    }

    public static Timestamp defaultTime() {
        return Timestamp.valueOf("1900-01-01 00:00:00");
    }
}
