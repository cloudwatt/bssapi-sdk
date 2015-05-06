package com.cloudwatt.apis.bss.spec.exceptions;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

    private static final String BUNDLE_NAME = "com.cloudwatt.apis.bss.spec.exceptions.messages"; //$NON-NLS-1$

    private Messages() {
    }

    /**
     * Get a translated String
     * 
     * @param locale the locale to use
     * @param key the key
     * @return the translated key
     */
    public static String getString(Locale locale, String key) {
        try {
            return ResourceBundle.getBundle(BUNDLE_NAME, locale).getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Get a translation with parameters
     * 
     * @param locale the locale to use
     * @param key the key used for translation
     * @param params the parameters
     * @return the translated String
     */
    public static String getString(Locale locale, String key, Object... params) {
        return MessageFormat.format(getString(locale, key), params);
    }
}
