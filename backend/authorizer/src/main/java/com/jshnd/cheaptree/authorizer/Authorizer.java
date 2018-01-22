package com.jshnd.cheaptree.authorizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

// all the bits of the authorizer lambda, but testable.
public class Authorizer {

    private static final Logger LOG = LogManager.getLogger(Authorizer.class);

    private static final String API_ACTION = "execute-api:Invoke";

    private static final String EFFECT_ALLOW = "Allow";

    private static final String EFFECT_DENY = "Deny";

    private static final String EXECUTE_API_ARN_FORMAT = "arn:aws:execute-api:%s:%s:%s/%s/%s/%s";

    private final GoogleTokenValidator tokenValidator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Authorizer(final GoogleTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    public void authorize(final InputStream in, final OutputStream out) throws IOException {
        LOG.trace("Beginning authorization");
        final TokenAuthorizerContext tokenCtx = objectMapper.readValue(in, TokenAuthorizerContext.class);
        LOG.trace("Read token context");

        final String methodArn = tokenCtx.getMethodArn();
        final String[] arnPartials = methodArn.split(":");
        final String region = arnPartials[3];
        final String awsAccountId = arnPartials[4];
        final String[] apiGatewayArnPartials = arnPartials[5].split("/");
        final String restApiId = apiGatewayArnPartials[0];
        final String stage = apiGatewayArnPartials[1];

        final String token = tokenCtx.getAuthorizationToken();
        objectMapper.writeValue(out, getPolicyForToken(region, awsAccountId, restApiId, stage, token));
        out.close();
    }

    private AuthPolicy getPolicyForToken(final String region,
                                         final String account,
                                         final String apiId,
                                         final String stage,
                                         final String token) {
        try {
            LOG.debug("Validating token");
            final TokenValidationResult result = tokenValidator.validateToken(token);
            if (result.isValid()) {
                LOG.info("Authorization pass for principle " + result.getPrinciple());
                return allowPolicy(result.getPrinciple(), region, account, apiId, stage);
            } else {
                LOG.info("Authorization failure");
                return denyPolicy(region, account, apiId, stage);
            }
        } catch (Exception e) {
            LOG.warn("Caught exception during authorization", e);
            return denyPolicy(region, account, apiId, stage);
        }
    }

    private AuthPolicy allowPolicy(final String principle,
                                   final String region,
                                   final String account,
                                   final String apiId,
                                   final String stage) {
        final PolicyStatement denyStatement = new PolicyStatement(
                EFFECT_DENY,
                API_ACTION,
                new ArrayList<>(),
                new HashMap<>()
        );
        final PolicyStatement allowStatement = new PolicyStatement(
                EFFECT_ALLOW,
                API_ACTION,
                Collections.singletonList(
                        String.format(EXECUTE_API_ARN_FORMAT, region, account, apiId, stage, "*", "*")
                ),
                new HashMap<>()
        );
        return new AuthPolicy(principle, new PolicyDocument(Arrays.asList(allowStatement, denyStatement)));
    }

    private AuthPolicy denyPolicy(final String region, final String account, final String apiId, final String stage) {
        final PolicyStatement allowStatement = new PolicyStatement(
                EFFECT_ALLOW,
                API_ACTION,
                new ArrayList<>(),
                new HashMap<>()
        );
        final PolicyStatement denyStatement = new PolicyStatement(
                EFFECT_DENY,
                API_ACTION,
                Collections.singletonList(
                        String.format(EXECUTE_API_ARN_FORMAT, region, account, apiId, stage, "*", "*")
                ),
                new HashMap<>()
        );
        return new AuthPolicy(null, new PolicyDocument(Arrays.asList(allowStatement, denyStatement)));
    }

}
