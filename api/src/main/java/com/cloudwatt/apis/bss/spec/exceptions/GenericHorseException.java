package com.cloudwatt.apis.bss.spec.exceptions;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.http.client.methods.HttpUriRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

/**
 * i18n ready exception for all Horse Exceptions
 * 
 * @author pierre souchay
 *
 */
public class GenericHorseException extends IOException implements LocalizedException {

    /**
     * 
     */
    private static final long serialVersionUID = -936753473448880661L;

    /**
     * JSON Payload return by horse API
     * 
     * @author pierre souchay
     *
     */
    public static class HorseErrorDescription {

        private final String id;

        private final String errorTime;

        private final String errorCode;

        private final String description;

        public String getId() {
            return id;
        }

        public String getErrorTime() {
            return errorTime;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getDescription() {
            return description;
        }

        public Map<String, String> getData() {
            return data;
        }

        private final Map<String, String> data;

        @JsonCreator
        public HorseErrorDescription(@JsonProperty(value = "id", required = true) String id,
                @JsonProperty(value = "errorTime", required = true) String errorTime,
                @JsonProperty(value = "errorCode", required = true) String errorCode,
                @JsonProperty(value = "description", required = true) String description,
                @JsonProperty(value = "data", required = false) Map<String, String> data) {
            super();
            this.id = id;
            this.errorTime = errorTime;
            this.errorCode = errorCode;
            this.description = description;
            this.data = ImmutableMap.<String, String> copyOf(data);
        }

    }

    @Override
    public String getMessage() {
        return getMessage(Locale.ENGLISH);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    /**
     * Get the translated message for the language you want
     * 
     * @param locale the locale to use
     * @return the message
     */
    @Override
    public String getMessage(Locale locale) {
        return Messages.getString(locale,
                                  "GenericHorseException.message", method, uri, jsonError.errorCode, jsonError.description, String.valueOf(jsonError.data)); //$NON-NLS-1$
    }

    private final HorseErrorDescription jsonError;

    private final String method;

    private final String uri;

    /**
     * Constructor
     * 
     * @param jsonError
     */
    public GenericHorseException(final HttpUriRequest request, HorseErrorDescription jsonError) {
        this.jsonError = jsonError;
        this.method = request.getMethod();
        this.uri = request.getURI().toASCIIString();
    }

}
