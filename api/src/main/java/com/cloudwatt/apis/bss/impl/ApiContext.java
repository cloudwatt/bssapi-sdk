package com.cloudwatt.apis.bss.impl;

import java.io.IOException;
import java.util.Map;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;

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

    public TokenAccess getTokenAccess() throws IOException;

}
