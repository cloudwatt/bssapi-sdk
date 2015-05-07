/**
 * 
 */
package com.cloudwatt.apis.bss.spec.exceptions;

import java.util.Date;
import java.util.Locale;
import org.apache.http.client.methods.HttpUriRequest;
import com.google.common.base.Optional;

/**
 * Exception throw when you are calling too fast too many times the APIs
 * 
 * @author pierre souchay
 *
 */
public class TooManyRequestsException extends Exception implements LocalizedException {

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
        if (blockedUntil.isPresent())
            return Messages.getString(locale, "TooManyRequestsException.withDate", method, uri, blockedUntil.get()); //$NON-NLS-1$
        else
            return Messages.getString(Locale.getDefault(), "TooManyRequestsException.nodate", method, uri); //$NON-NLS-1$
    }

    /**
     * 
     */
    private static final long serialVersionUID = 306179527555917364L;

    /**
     * Constructor
     * 
     * @param blockedUntil
     */
    public TooManyRequestsException(final HttpUriRequest request, Optional<Date> blockedUntil) {
        this.blockedUntil = blockedUntil;
        this.method = request.getMethod();
        this.uri = request.getURI().toASCIIString();
    }

    public Optional<Date> getBlockedUntil() {
        return blockedUntil;
    }

    private final String method;

    private final String uri;

    private final Optional<Date> blockedUntil;

}
