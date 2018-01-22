package com.jshnd.cheaptree.authorizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizerTest {

    @Mock
    private GoogleTokenValidator mockTokenValidator;

    @InjectMocks
    private Authorizer testObj;

    @Test
    public void authorize_invalidToken_writesExpectedPolicyDocument() throws IOException, GeneralSecurityException {
        final ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = getClass().getResourceAsStream("/sampleInputStream.json")) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            when(mockTokenValidator.validateToken("123")).thenReturn(new TokenValidationResult(false, null));
            testObj.authorize(in, out);

            final JsonNode expected = mapper.readTree(getClass().getResourceAsStream("/expectedDenyAll.json"));
            final JsonNode got = mapper.readTree(out.toByteArray());

            assertThat(got, equalTo(expected));
        }
    }

    @Test
    public void authorize_validToken_writesExpectedPolicyDocument() throws IOException, GeneralSecurityException {
        final ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = getClass().getResourceAsStream("/sampleInputStream.json")) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            when(mockTokenValidator.validateToken("123")).thenReturn(new TokenValidationResult(true, "Bob"));
            testObj.authorize(in, out);

            final JsonNode expected = mapper.readTree(getClass().getResourceAsStream("/expectedAllowAll.json"));
            final JsonNode got = mapper.readTree(out.toByteArray());

            assertThat(got, equalTo(expected));
        }
    }

}