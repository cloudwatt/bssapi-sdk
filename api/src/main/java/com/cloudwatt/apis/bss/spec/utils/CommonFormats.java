package com.cloudwatt.apis.bss.spec.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Common utilities and formats
 * 
 * @author pierre souchay
 *
 */
public class CommonFormats {

    /**
     * Get a ISO Time format
     * 
     * @return the ISO Time format
     */
    public final static DateFormat buildIso8601Format() {
        final DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US); //$NON-NLS-1$
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
        return iso8601Format;
    }

}
