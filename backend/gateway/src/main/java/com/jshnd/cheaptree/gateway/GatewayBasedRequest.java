package com.jshnd.cheaptree.gateway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayBasedRequest {

    private final Map<String, String> queryStringParameters;

    private final Map<String, String> pathParameters;

    private final Map<String, String> headers;

    private final String body;

    @JsonCreator
    public GatewayBasedRequest(@JsonProperty("queryStringParameters") final Map<String, String> queryStringParameters,
                               @JsonProperty("pathParameters") final Map<String, String> pathParameters,
                               @JsonProperty("headers") final Map<String, String> headers,
                               @JsonProperty("body") final String body) {
        this.queryStringParameters = queryStringParameters;
        this.pathParameters = pathParameters;
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> getQueryStringParameters() {
        return queryStringParameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}
