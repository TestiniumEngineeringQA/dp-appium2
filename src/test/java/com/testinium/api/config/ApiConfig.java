package com.testinium.api.config;


public final class ApiConfig {

    private static final String DEFAULT_API_BASE_URL = "https://api.gratis.retter.io/1oakekr4e";

    private ApiConfig() {
    }

    public static String baseUri() {
        String url = firstNonBlank(
                System.getenv("API_BASE_URL"),
                System.getProperty("API_BASE_URL"),
                System.getProperty("api.base.url"));
        if (url == null || url.isBlank()) {
            url = DEFAULT_API_BASE_URL;
        }
        return url.replaceAll("/$", "");
    }

    private static String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String s : candidates) {
            if (s != null && !s.isBlank()) {
                return s.trim();
            }
        }
        return null;
    }
}
