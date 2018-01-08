package com.jshnd.cheaptree.hello;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HelloLambda implements RequestStreamHandler {
    private static final Logger logger = LogManager.getLogger(HelloLambda.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public void handleRequest(final InputStream ioIn, final OutputStream ioOut, final Context ctx) throws IOException {
        try {
            final GatewayBasedRequest request = mapper.readValue(ioIn, GatewayBasedRequest.class);
            final HelloIn in = mapper.readValue(request.getBody(), HelloIn.class);
            final String responseBody = mapper.writeValueAsString(new HelloOut(in));
            final GatewaySafeResponse out = new GatewaySafeResponse(200, new HashMap<>(), responseBody);

            mapper.writeValue(ioOut, out);
            ioOut.close();
        } catch (Exception e) {
            logger.error("Exception during execution", e);
        }
    }
}