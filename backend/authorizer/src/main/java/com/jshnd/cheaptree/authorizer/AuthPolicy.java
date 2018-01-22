package com.jshnd.cheaptree.authorizer;

public class AuthPolicy {

    private final String principalId;

    private final PolicyDocument policyDocument;

    public AuthPolicy(final String principalId, final PolicyDocument policyDocument) {
        this.principalId = principalId;
        this.policyDocument = policyDocument;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public PolicyDocument getPolicyDocument() {
        return policyDocument;
    }

}
