/**
 * 
 */
package com.cloudwatt.apis.bss.spec.exceptions;

import java.net.URI;

/**
 * @author pierre
 *
 */
public class WrongCredentialsException extends HttpUnexpectedError {

    /**
     * 
     */
    private static final long serialVersionUID = 8601470273327581820L;

    /**
     * Wrong Credentials Exception
     */
    public WrongCredentialsException(URI uri, int httpCode, String message) {
        super("WrongCredentialsException", uri, httpCode, message); //$NON-NLS-1$
    }
}
