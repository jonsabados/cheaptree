package com.jshnd.cheaptree.cors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jshnd.cheaptree.gateway.GatewayBasedRequest;
import com.jshnd.cheaptree.gateway.GatwayLambdaUtil;
import com.jshnd.cheaptree.gateway.GatewaySafeResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class CorsLambda implements RequestStreamHandler {
    private final GatwayLambdaUtil gatewayUtil = new GatwayLambdaUtil(new ObjectMapper());

    @Override
    public void handleRequest(final InputStream in, final OutputStream out, final Context ctx) throws IOException {
        final GatewayBasedRequest request = gatewayUtil.readInput(in);
        final Map<String, String> headers = gatewayUtil.baseHeaders(request);
        gatewayUtil.writeResponse(out, new GatewaySafeResponse(204, headers, ""));
    }
}
