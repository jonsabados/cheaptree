package com.jshnd.cheaptree.hello;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jshnd.cheaptree.gateway.GatewayBasedRequest;
import com.jshnd.cheaptree.gateway.GatwayLambdaUtil;
import com.jshnd.cheaptree.gateway.GatewaySafeResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HelloLambda implements RequestStreamHandler {
    private static final Logger LOG = LogManager.getLogger(HelloLambda.class);

    private final GatwayLambdaUtil gatewayUtil = new GatwayLambdaUtil(new ObjectMapper());

    public void handleRequest(final InputStream in, final OutputStream out, final Context ctx) throws IOException {
        final GatewayBasedRequest request = gatewayUtil.readInput(in);
        final HelloIn requestBody = gatewayUtil.readBody(request, HelloIn.class);
        final GatewaySafeResponse response = gatewayUtil.createResponseWithBody(200, gatewayUtil.baseHeaders(request), new HelloOut(requestBody));
        gatewayUtil.writeResponse(out, response);
    }
}