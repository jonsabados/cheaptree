package com.jshnd.cheaptree.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayLambdaUtil {

    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_NO_CONTENT = 204;

    private static final Logger LOG = LogManager.getLogger(GatewayLambdaUtil.class);

    private final List<String> allowedDomains;

    private final ObjectMapper mapper;

    public GatewayLambdaUtil(final ObjectMapper mapper) {
        allowedDomains = Arrays.asList(System.getenv("ALLOWED_CORS_DOMAINS").split(","));
        this.mapper = mapper;
    }

    /**
     * Marshals API-Gateway lambda-proxied requests into a usable object. Also does some sanity like making case
     * on header names consistent (they will all be converted to lower case).
     *
     * @param in input stream provided to the lambda
     * @return Information passed by the API gateway
     * @throws IOException if anything goes wrong reading the input
     */
    public GatewayBasedRequest readInput(final InputStream in) throws IOException {
        final GatewayBasedRequest withMixedHeaderCase = mapper.readValue(in, GatewayBasedRequest.class);
        final Map<String, String> fixedHeaders = new HashMap<>();
        for (Map.Entry<String, String> en : withMixedHeaderCase.getHeaders().entrySet()) {
            fixedHeaders.put(en.getKey().toLowerCase(), en.getValue());
        }
        return new GatewayBasedRequest(withMixedHeaderCase.getQueryStringParameters(),
                withMixedHeaderCase.getPathParameters(),
                fixedHeaders,
                withMixedHeaderCase.getBody());
    }

    public <T> T readBody(final GatewayBasedRequest request, final Class<T> bodyType) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Reading body: " + request.getBody());
        }
        return mapper.readValue(request.getBody(), bodyType);
    }

    public void writeResponse(final OutputStream out, final GatewaySafeResponse response) throws IOException {
        try {
            if (LOG.isTraceEnabled()) {
                final String responseBody = mapper.writeValueAsString(response);
                LOG.trace("Writing response: " + responseBody);
            }
            mapper.writeValue(out, response);
        } finally {
            out.close();
        }
    }

    public GatewaySafeResponse createResponseWithBody(final int status,
                                                      final Map<String, String> headers,
                                                      final Object body) throws IOException {
        return new GatewaySafeResponse(status, headers, mapper.writeValueAsString(body));
    }

    public Map<String, String> baseHeaders(final GatewayBasedRequest request) {
        final Map<String, String> headers = new HashMap<>();
        if (request.getHeaders() != null) {
            final String origin = request.getHeaders().get("origin");
            if (origin != null && allowedDomains.stream().anyMatch(d -> d.equals(origin))) {
                headers.put("Access-Control-Allow-Origin", origin);
                headers.put("Access-Control-Allow-Headers", "Cookie,Content-Type");
                headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,HEAD");
                headers.put("Access-Control-Allow-Credentials", "true");
            }
        }
        return headers;
    }
}
