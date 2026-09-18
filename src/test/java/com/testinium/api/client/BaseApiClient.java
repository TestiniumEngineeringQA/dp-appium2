package com.testinium.api.client;

import com.testinium.api.config.ApiConfig;
import com.testinium.api.support.ApiSession;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;


public abstract class BaseApiClient {

    protected RequestSpecification baseSpec() {
        RequestSpecBuilder b = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter());

        String token = ApiSession.getBearerToken();
        if (token != null && !token.isBlank()) {
            b.addHeader("Authorization", "Bearer " + token.trim());
        }

        RequestSpecification spec = b.build();
        return spec;
    }
}
