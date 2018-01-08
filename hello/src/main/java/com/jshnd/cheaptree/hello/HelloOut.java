package com.jshnd.cheaptree.hello;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloOut {

    private final HelloIn echo;

    public HelloOut(HelloIn echo) {
        this.echo = echo;
    }

    @JsonProperty
    public HelloIn getEcho() {
        return echo;
    }

}
