package com.cloudwatt.apis.bss.impl;

import java.io.IOException;
import java.util.Map;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;

public interface ApiContext {

    public WebClient getWebClient();

    public String buildPublicApiUrl(String path, Map<String, String> queryParameters);

    public TokenAccess getTokenAccess() throws IOException;

}
