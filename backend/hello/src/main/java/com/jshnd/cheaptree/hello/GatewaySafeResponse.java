package com.jshnd.cheaptree.hello;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GatewaySafeResponse {

    private final String statusCode;

    private final Map<String, String> headers;

    private final String body;

    public GatewaySafeResponse(int statusCode, Map<String, String> headers, String body) {
        // based on this: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-create-api-as-simple-proxy-for-lambda.html#api-gateway-proxy-integration-lambda-function-java
        // they want this to be a string. WTF?
        this.statusCode = String.valueOf(statusCode);
        this.headers = headers;
        this.body = body;
    }

    @JsonProperty
    public String getStatusCode() {
        return statusCode;
    }

    @JsonProperty
    public Map<String, String> getHeaders() {
        return headers;
    }

    @JsonProperty
    public String getBody() {
        return body;
    }
}
