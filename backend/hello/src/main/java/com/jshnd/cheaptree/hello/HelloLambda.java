package com.jshnd.cheaptree.hello;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jshnd.cheaptree.gateway.GatewayBasedRequest;
import com.jshnd.cheaptree.gateway.GatewayLambdaUtil;
import com.jshnd.cheaptree.gateway.GatewaySafeResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HelloLambda implements RequestStreamHandler {

    private final GatewayLambdaUtil gatewayUtil = new GatewayLambdaUtil(new ObjectMapper());

    public void handleRequest(final InputStream in, final OutputStream out, final Context ctx) throws IOException {
        final GatewayBasedRequest request = gatewayUtil.readInput(in);
        final HelloIn requestBody = gatewayUtil.readBody(request, HelloIn.class);
        final GatewaySafeResponse response = gatewayUtil.createResponseWithBody(
                GatewayLambdaUtil.HTTP_STATUS_OK,
                gatewayUtil.baseHeaders(request),
                new HelloOut(requestBody)
        );
        gatewayUtil.writeResponse(out, response);
    }
}