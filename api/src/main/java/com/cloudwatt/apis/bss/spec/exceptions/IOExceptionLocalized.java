/**
 * 
 */
package com.cloudwatt.apis.bss.spec.exceptions;

import java.io.IOException;
import java.util.Locale;

/**
 * @author pierre souchay
 *
 */
public class IOExceptionLocalized extends IOException implements LocalizedException {

    /**
     * 
     */
    private static final long serialVersionUID = -9077684731602031574L;

    /**
     * Constructor
     * 
     * @param key
     * @param params
     */
    public IOExceptionLocalized(String key, Object... params) {
        super(Messages.getString(Locale.ENGLISH, key, params));
        this.key = key;
        this.params = params;
    }

    private final String key;

    private final Object params[];

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
        return Messages.getString(locale, key, params);
    }

}
