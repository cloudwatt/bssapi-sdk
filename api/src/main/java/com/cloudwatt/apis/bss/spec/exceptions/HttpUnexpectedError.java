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
     * @param uri The URI
     * @param httpCode the HTTP Code
     * @param httpMessage message
     */
    public HttpUnexpectedError(final URI uri, final int httpCode, final String httpMessage) {
        super("HttpUnexpectedError", uri.toASCIIString(), httpCode, httpMessage); //$NON-NLS-1$
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    private final int httpCode;

}
