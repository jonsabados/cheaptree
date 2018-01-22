package com.jshnd.cheaptree.authorizer;

public class TokenValidationResult {

    private final boolean valid;

    private final String principle;

    public TokenValidationResult(final boolean valid, final String principle) {
        this.valid = valid;
        this.principle = principle;
    }

    public boolean isValid() {
        return valid;
    }

    public String getPrinciple() {
        return principle;
    }

}
