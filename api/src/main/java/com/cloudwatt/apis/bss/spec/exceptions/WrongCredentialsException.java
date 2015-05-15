/**
 * 
 */
package com.cloudwatt.apis.bss.spec.exceptions;

/**
 * @author pierre
 *
 */
public class WrongCredentialsException extends IOExceptionLocalized {

    /**
     * 
     */
    private static final long serialVersionUID = 8601470273327581820L;

    /**
     * Wrong Credentials Exception
     */
    public WrongCredentialsException(Throwable cause) {
        super("WrongCredentialsException", cause.getMessage()); //$NON-NLS-1$
    }
}
