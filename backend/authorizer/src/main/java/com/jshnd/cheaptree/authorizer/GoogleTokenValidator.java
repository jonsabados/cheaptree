package com.jshnd.cheaptree.authorizer;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleTokenValidator {

    private static final Logger LOG = LogManager.getLogger(GoogleTokenValidator.class);

    private final HttpTransport transport;

    private final JsonFactory jsonFactory;

    private final String clientId;

    public GoogleTokenValidator(final HttpTransport transport,
                                final JsonFactory jsonFactory,
                                final String clientId) {
        this.transport = transport;
        this.jsonFactory = jsonFactory;
        this.clientId = clientId;
    }

    public TokenValidationResult validateToken(final String token) throws GeneralSecurityException, IOException {
        LOG.trace("Entering validateToken");
        final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

        LOG.trace("Verifier built");
//        final Thread mightHang = Thread.currentThread();
//        new Thread(() -> {
//            try {
//                LOG.debug("Making request to https://www.googleapis.com/oauth2/v3/certs");
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet("https://www.googleapis.com/oauth2/v3/certs");
//
//                HttpResponse response = client.execute(request);
//
//                LOG.debug("Response Code : " + response.getStatusLine().getStatusCode());
//
//                Thread.sleep(500);
//                for (StackTraceElement el : mightHang.getStackTrace()) {
//                    LOG.debug("Stack element: " + el.getFileName() + ", line " + el.getLineNumber());
//                }
//            } catch (Exception e) {
//                // don't care....
//            }
//        }).start();
        GoogleIdToken idToken = verifier.verify(token.replaceFirst("Bearer ", ""));
        LOG.trace("Token verified");
        if (idToken != null) {
            final GoogleIdToken.Payload payload = idToken.getPayload();

            final String userId = payload.getSubject();

            return new TokenValidationResult(true, userId);
        } else {
            return new TokenValidationResult(false, null);
        }
    }

}
