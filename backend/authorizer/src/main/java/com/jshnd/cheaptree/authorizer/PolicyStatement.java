package com.jshnd.cheaptree.authorizer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class PolicyStatement {

    @JsonProperty("Effect")
    final String effect;

    @JsonProperty("Action")
    final String action;

    @JsonProperty("Resource")
    final List<String> resource;

    @JsonProperty("Condition")
    final Map<String, Map<String,Object>> condition;

    public PolicyStatement(final String effect,
                           final String action,
                           final List<String> resource,
                           final Map<String, Map<String, Object>> condition) {
        this.effect = effect;
        this.action = action;
        this.resource = resource;
        this.condition = condition;
    }

    public String getEffect() {
        return effect;
    }

    public String getAction() {
        return action;
    }

    public List<String> getResource() {
        return resource;
    }

    public Map<String, Map<String, Object>> getCondition() {
        return condition;
    }
}
