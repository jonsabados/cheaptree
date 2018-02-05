package com.jshnd.cheaptree.authorizer;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleTokenValidator {

    private static final Logger LOG = LogManager.getLogger(GoogleTokenValidator.class);

    private final GoogleIdTokenVerifier tokenValidator;

    public GoogleTokenValidator(GoogleIdTokenVerifier tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    public TokenValidationResult validateToken(final String token) throws GeneralSecurityException, IOException {
        LOG.trace("Entering validateToken");
        GoogleIdToken idToken = tokenValidator.verify(token.replaceFirst("Bearer ", ""));
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
