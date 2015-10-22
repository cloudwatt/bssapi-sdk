package com.cloudwatt.apis.bss.spec.exceptions;

import java.util.Locale;

/**
 * Interface implemented by exceptions that are i18n aware
 * 
 * @author pierre souchay
 *
 */
public interface LocalizedException {

    /**
     * Get the message in English
     * 
     * @return the message in English
     */
    public String getMessage();

    /**
     * Get the message in JVM's locale (usually the locale of user running the JVM)
     * 
     * @return the Message in User's locale
     */
    public String getLocalizedMessage();

    /**
     * Get the translated message for the language you want
     * 
     * @param locale the locale to use
     * @return the message in given locale
     */
    public String getMessage(Locale locale);

}
