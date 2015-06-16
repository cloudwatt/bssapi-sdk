package com.cloudwatt.apis.bss.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.spec.exceptions.GenericHorseException;
import com.cloudwatt.apis.bss.spec.exceptions.HttpUnexpectedError;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    public WebClient(CloseableHttpClient client) {
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

    private <T> Optional<T> tryReadJson(final Class<T> clazz, final HttpUriRequest request,
            final CloseableHttpResponse response) throws JsonParseException, IOException {
        BufferedReader reader = null;
        JsonFactory factory = getJsonFactory();
        JsonParser parser = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Constants.UTF_8));
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
    }

    /**
     * The ISO date format
     */
    public final static String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"; //$NON-NLS-1$

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
            Optional<TokenAccess> access) throws IOException, TooManyRequestsException, GenericHorseException,
            HttpUnexpectedError {
        // final long start = System.currentTimeMillis();
        try {
            request.setHeader(Constants.HEADER_NAME_ACCEPT, Constants.HEADER_VALUE_APPLICATION_JSON);
            request.setHeader(Constants.HEADER_NAME_CONTENT_TYPE, Constants.HEADER_VALUE_APPLICATION_JSON);
            if (access.isPresent()) {
                request.setHeader(Constants.HEADER_NAME_X_AUTH_TOKEN, access.get().getToken().getId());
            }
            CloseableHttpResponse response = doRequest(client, request);

            try {
                final int httpCode = response.getStatusLine().getStatusCode();
                if (httpCode == 404) {
                    // HTTP 404: Not found
                    return Optional.<T> absent();
                } else if (httpCode == 429) {
                    Optional<GenericHorseException.HorseErrorDescription> value = Optional.absent();
                    try {
                        value = tryReadJson(GenericHorseException.HorseErrorDescription.class, request, response);
                    } catch (IOException e) {
                    }
                    Optional<Date> blockedUntil = Optional.absent();
                    if (value.isPresent()) {
                        try {
                            Date dx = (new SimpleDateFormat(ISO_DATE_FORMAT)).parse(value.get()
                                                                                         .getData()
                                                                                         .get("blockedUntil")); //$NON-NLS-1$
                            if (dx.after(new Date(System.currentTimeMillis() - 3600000))) {
                                blockedUntil = Optional.<Date> of(dx);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    throw new TooManyRequestsException(request, blockedUntil);
                } else if (httpCode > 399) {
                    // We try to read Error code !
                    try {
                        Optional<GenericHorseException.HorseErrorDescription> value = tryReadJson(GenericHorseException.HorseErrorDescription.class,
                                                                                                  request,
                                                                                                  response);
                        if (value.isPresent()) {
                            throw new GenericHorseException(request, value.get());
                        }
                    } catch (JsonParseException ignored) {
                        // We go to next Exception thrown
                    } catch (JsonMappingException ignored) {
                        // Same, we are unexpected
                    }
                    throw new HttpUnexpectedError(request.getURI(), httpCode, response.getStatusLine()
                                                                                      .getReasonPhrase());

                }

                if (clazz.isAssignableFrom(StatusLine.class)) {
                    return (Optional<T>) Optional.<StatusLine> of(response.getStatusLine());
                }

                return tryReadJson(clazz, request, response);

            } finally {
                response.close();
            }
        } finally {
            // FIXME:log ?
        }
    }
}
