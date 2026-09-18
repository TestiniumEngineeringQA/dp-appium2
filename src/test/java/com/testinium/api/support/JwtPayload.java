package com.testinium.api.support;

import io.restassured.path.json.JsonPath;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


public final class JwtPayload {

    private JwtPayload() {
    }

    public static String userIdFromAccessToken(String jwt) {
        if (jwt == null || jwt.isBlank()) {
            throw new IllegalArgumentException("accessToken boş");
        }
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Geçersiz JWT formatı");
        }
        byte[] decoded = base64UrlDecode(parts[1]);
        String payloadJson = new String(decoded, StandardCharsets.UTF_8);
        String userId = JsonPath.from(payloadJson).getString("userId");
        if (userId == null || userId.isBlank()) {
            throw new AssertionError("JWT payload'da userId yok: " + payloadJson);
        }
        return userId;
    }

    private static byte[] base64UrlDecode(String segment) {
        String padded = segment;
        int mod = segment.length() % 4;
        if (mod == 2) {
            padded = segment + "==";
        } else if (mod == 3) {
            padded = segment + "=";
        }
        return Base64.getUrlDecoder().decode(padded);
    }
}
