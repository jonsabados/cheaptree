package com.jshnd.cheaptree.authorizer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenAuthorizerContext {

    private final String type;
    private final String authorizationToken;
    private final String methodArn;

    @JsonCreator
    public TokenAuthorizerContext(@JsonProperty("type") final String type,
                                  @JsonProperty("authorizationToken") final String authorizationToken,
                                  @JsonProperty("methodArn") final String methodArn) {
        this.type = type;
        this.authorizationToken = authorizationToken;
        this.methodArn = methodArn;
    }

    public String getType() {
        return type;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getMethodArn() {
        return methodArn;
    }
}
