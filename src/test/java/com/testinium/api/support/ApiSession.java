package com.testinium.api.support;

import java.util.HashMap;
import java.util.Map;


public final class ApiSession {

    private static final ThreadLocal<Map<String, String>> VARS = ThreadLocal.withInitial(HashMap::new);

    private ApiSession() {
    }

    public static void put(String key, String value) {
        if (value != null) {
            VARS.get().put(key, value);
        }
    }

    public static String get(String key) {
        return VARS.get().get(key);
    }

    public static void setBearerToken(String token) {
        put("bearerToken", token);
    }

    public static String getBearerToken() {
        return get("bearerToken");
    }

    public static void clear() {
        VARS.remove();
    }
}
