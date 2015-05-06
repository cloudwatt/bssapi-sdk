package com.cloudwatt.apis.bss.impl;

import java.nio.charset.Charset;

@SuppressWarnings("nls")
public class Constants {

    public final static String UTF_8_NAME = "UTF-8";

    public final static Charset UTF_8 = Charset.forName(UTF_8_NAME);

    /**
     * Content-Type Header
     */
    final static String HEADER_NAME_CONTENT_TYPE = "Content-Type";

    /**
     * Accept Header
     */
    final static String HEADER_NAME_ACCEPT = "Accept";

    /**
     * Acccept-Language Header
     */
    public final static String HEADER_NAME_LANGUAGE = "Accept-Language";

    /**
     * X-Auth-Token header
     */
    final static String HEADER_NAME_X_AUTH_TOKEN = "X-Auth-Token";

    /**
     * Content-Type Header
     */
    final static String HEADER_VALUE_APPLICATION_JSON = "application/json";

}
