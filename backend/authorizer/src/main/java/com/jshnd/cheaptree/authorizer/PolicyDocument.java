package com.jshnd.cheaptree.authorizer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PolicyDocument {

    @JsonProperty("Version")
    final String version = "2012-10-17";

    @JsonProperty("Statement")
    final List<PolicyStatement> statement;

    public PolicyDocument(List<PolicyStatement> statement) {
        this.statement = statement;
    }

    public String getVersion() {
        return version;
    }

    public List<PolicyStatement> getStatement() {
        return statement;
    }

}
