/**
 * 
 */
package com.cloudwatt.apis.bss.spec.exceptions;

import java.net.URI;

/**
 * @author pierre
 *
 */
public class HttpUnexpectedError extends IOExceptionLocalized {

    /**
     * 
     */
    private static final long serialVersionUID = -8168333843058073389L;

    /**
     * Constructor
     * 
     * @param translation key
     * @param uri The URI
     * @param httpCode the HTTP Code
     * @param httpMessage message
     */
    protected HttpUnexpectedError(String key, final URI uri, final int httpCode, final String httpMessage) {
        super(key, uri.toASCIIString(), httpCode, httpMessage);
        this.httpCode = httpCode;
        this.httpMessage = httpMessage;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    /**
     * Constructor
     * 
     * @param uri The URI
     * @param httpCode the HTTP Code
     * @param httpMessage message
     */
    public HttpUnexpectedError(final URI uri, final int httpCode, final String httpMessage) {
        this("HttpUnexpectedError", uri, httpCode, httpMessage); //$NON-NLS-1$
    }

    public int getHttpCode() {
        return httpCode;
    }

    private final int httpCode;

    private final String httpMessage;

}
