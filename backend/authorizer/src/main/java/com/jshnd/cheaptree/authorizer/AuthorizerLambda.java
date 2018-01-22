package com.jshnd.cheaptree.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AuthorizerLambda implements RequestStreamHandler {

    private static final Logger LOG = LogManager.getLogger(AuthorizerLambda.class);

    private final Authorizer authorizer;

    public AuthorizerLambda() {
        authorizer = buildAuthorizer();
    }

    @Override
    public void handleRequest(final InputStream in, final OutputStream out, final Context ctx) throws IOException {
        authorizer.authorize(in, out);
    }

    private Authorizer buildAuthorizer() {
        final String clientId = System.getenv("GOOGLE_CLIENT_ID");
        LOG.debug("Initializing with client id " + clientId);
        final HttpTransport transport = new ApacheHttpTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        final GoogleTokenValidator tokenValidator = new GoogleTokenValidator(transport, jsonFactory, clientId);
        return new Authorizer(tokenValidator);
    }

}
