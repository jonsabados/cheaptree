package com.jshnd.cheaptree.cors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jshnd.cheaptree.gateway.GatewayBasedRequest;
import com.jshnd.cheaptree.gateway.GatewayLambdaUtil;
import com.jshnd.cheaptree.gateway.GatewaySafeResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class CorsLambda implements RequestStreamHandler {
    private final GatewayLambdaUtil gatewayUtil = new GatewayLambdaUtil(new ObjectMapper());

    @Override
    public void handleRequest(final InputStream in, final OutputStream out, final Context ctx) throws IOException {
        final GatewayBasedRequest request = gatewayUtil.readInput(in);
        final Map<String, String> headers = gatewayUtil.baseHeaders(request);
        gatewayUtil.writeResponse(out, new GatewaySafeResponse(GatewayLambdaUtil.HTTP_STATUS_NO_CONTENT, headers, ""));
    }
}
