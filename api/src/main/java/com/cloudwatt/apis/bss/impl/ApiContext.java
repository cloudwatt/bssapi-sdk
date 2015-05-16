package com.cloudwatt.apis.bss.impl;

import java.io.IOException;
import java.util.Map;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.spec.exceptions.IOExceptionLocalized;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * Internal Use only
 * 
 * @author pierre souchay
 *
 */
public interface ApiContext {

    public String buildKeystoneUrl(String relativePath);

    public WebClient getWebClient();

    public String buildPublicApiUrl(String path, Map<String, String> queryParameters);

    public TokenAccess getTokenAccess() throws IOException, TooManyRequestsException, IOExceptionLocalized;
}
