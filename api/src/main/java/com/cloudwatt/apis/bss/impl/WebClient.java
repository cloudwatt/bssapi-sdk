package com.cloudwatt.apis.bss.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;

/**
 * Implementation class to retrieve data from HTTP calls
 * 
 * @author pierre souchay
 *
 */
public class WebClient {

    private final CloseableHttpClient client;

    /**
     * Package protected constructor
     * 
     * @param client
     */
    WebClient(CloseableHttpClient client) {
        this.client = client;
    }

    private final JsonFactory jsonFactory = new JsonFactory();

    /**
     * Get the JSON Factory for Deserialization
     *
     * @return the JSONFactory to use
     */
    private JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    /**
     * Get the default Object Mapper
     *
     * @return a JSON Object Mapper
     */
    private static ObjectMapper getObjectMapper(JsonFactory factory) {
        return new ObjectMapper(factory).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                        .disable(DeserializationFeature.EAGER_DESERIALIZER_FETCH)
                                        .disable(SerializationFeature.EAGER_SERIALIZER_FETCH);
    }

    private static CloseableHttpResponse doRequest(final CloseableHttpClient client, HttpUriRequest request)
            throws ClientProtocolException, IOException {
        return client.execute(request);
    }

    /**
     * Call the API with proper headers and deserialized contents
     * 
     * @param clazz the class to serialize to
     * @param request the request to send
     * @param access the token to use, if not present, won't use X-Auth-Token header
     * @return {@link Optional#absent()} if HTTP 404, the data otherwise within the optional
     * @throws IOException
     * @throws TooManyRequestsException When you are calling too fast, too many times the API or have been blacklisted
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> doRequestAndRetrieveResultAsJSON(Class<T> clazz, HttpUriRequest request,
            Optional<TokenAccess> access) throws IOException, TooManyRequestsException {
        // final long start = System.currentTimeMillis();
        try {
            request.setHeader(Constants.HEADER_NAME_ACCEPT, Constants.HEADER_VALUE_APPLICATION_JSON);
            request.setHeader(Constants.HEADER_NAME_CONTENT_TYPE, Constants.HEADER_VALUE_APPLICATION_JSON);
            if (access.isPresent()) {
                request.setHeader(Constants.HEADER_NAME_X_AUTH_TOKEN, access.get().getToken().getId());
            }
            CloseableHttpResponse response = doRequest(client, request);
            BufferedReader reader = null;
            JsonFactory factory = getJsonFactory();
            JsonParser parser = null;
            try {
                if (response.getStatusLine().getStatusCode() == 404) {
                    // HTTP 404: Not found
                    return Optional.<T> absent();
                }
                if (response.getStatusLine().getStatusCode() == 429) {
                    // TODO: parse blocked until
                    throw new TooManyRequestsException(request, Optional.<Date> absent());
                }

                if (clazz.isAssignableFrom(StatusLine.class)) {
                    return (Optional<T>) Optional.<StatusLine> of(response.getStatusLine());
                }

                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                                                                      Constants.UTF_8));
                } catch (UnsupportedEncodingException e) {

                    throw new RuntimeException(e);
                }

                parser = factory.createParser(reader);
                try {
                    ObjectMapper mapper = getObjectMapper(factory);
                    mapper.canSerialize(clazz);
                    parser.setCodec(mapper);
                    try {
                        T webClientResponse = parser.readValueAs(clazz);
                        return Optional.fromNullable(webClientResponse);
                    } catch (JsonParseException err) {
                        StringBuilder sb = new StringBuilder("Cannot parse JSON result ["). //$NON-NLS-1$
                        append(request.getMethod())
                                                                                          .append(" ") //$NON-NLS-1$
                                                                                          .append(request.getURI()
                                                                                                         .toASCIIString())
                                                                                          .append("][).append(") //$NON-NLS-1$
                                                                                          .append(response.getStatusLine()
                                                                                                          .getStatusCode())
                                                                                          .append("][") //$NON-NLS-1$
                                                                                          .append(response.getStatusLine()
                                                                                                          .getReasonPhrase())
                                                                                          .append("] ") //$NON-NLS-1$
                                                                                          .append(err.getClass()
                                                                                                     .getSimpleName())
                                                                                          .append("=") //$NON-NLS-1$
                                                                                          .append(err.getMessage());
                        final String msg = sb.toString();
                        throw new JsonParseException(msg, err.getLocation());
                    }
                } finally {
                    parser.close();
                    reader.close();
                }
            } finally {
                response.close();
            }
        } finally {
            // FIXME:log ?
        }
    }

}
