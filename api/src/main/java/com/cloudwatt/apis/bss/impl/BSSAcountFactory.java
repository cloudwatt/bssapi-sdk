package com.cloudwatt.apis.bss.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandler;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;

public class BSSAcountFactory {

    /**
     * Get the client API version
     * 
     * @return
     */
    public final static String getClientApiVersion() {
        return "0.2.0"; //$NON-NLS-1$
    }

    private static String createRequest(String baseUrl, String relativePath, Map<String, ?> parameters) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (!baseUrl.endsWith("/") && !(relativePath.startsWith("/"))) //$NON-NLS-1$ //$NON-NLS-2$
            sb.append('/');
        sb.append(relativePath);
        if (parameters != null) {
            boolean first = true;
            for (Map.Entry<String, ?> en : parameters.entrySet()) {
                if (first) {
                    sb.append('?');
                    first = false;
                } else {
                    sb.append('&');
                }
                try {
                    sb.append(URLEncoder.encode(en.getKey(), Constants.UTF_8_NAME))
                      .append('=')
                      .append(URLEncoder.encode(String.valueOf(en.getValue()), Constants.UTF_8_NAME));
                } catch (UnsupportedEncodingException err) {
                    throw new RuntimeException(err);
                }
            }
        }
        return sb.toString();
    }

    private static final class ApiContextImpl implements ApiContext {

        /**
         * Constructor
         * 
         * @param client
         * @param publicApiBase
         * @param tokenAccess
         */
        ApiContextImpl(String keystonePublicEndpoint, WebClient client, String publicApiBase, TokenAccess tokenAccess,
                String password) {
            this.client = client;
            this.publicApiBase = publicApiBase;
            this.tokenAccess = tokenAccess;
            this.keystonePublicEndpoint = keystonePublicEndpoint;
            this.password = password;
        }

        private final String keystonePublicEndpoint;

        private final WebClient client;

        private final String password;

        private final String publicApiBase;

        private TokenAccess tokenAccess;

        @Override
        public WebClient getWebClient() {
            return client;
        }

        @Override
        public String buildPublicApiUrl(String relativePath, Map<String, String> queryParameters) {
            return createRequest(publicApiBase, relativePath, queryParameters);
        }

        @Override
        public TokenAccess getTokenAccess() throws IOException {
            return tokenAccess;
        }

    }

    private final static String TOKENS_SUFFIX = "tokens"; //$NON-NLS-1$

    private static String buildTokensUrl(String value) {
        if (value.endsWith(TOKENS_SUFFIX)) {
            return value;
        }
        if (!value.endsWith("/")) { //$NON-NLS-1$
            value += "/"; //$NON-NLS-1$
        }
        value += TOKENS_SUFFIX;
        return value;
    }

    public static class Builder {

        private String keystonePublicEndpoint = "https://identity.fr1.cloudwatt.com/v2.0";

        private final String email;

        private final String password;

        private String userAgent = System.getProperty("cwapi-ua", //$NON-NLS-1$
                                                      "horse-client/" + getClientApiVersion()); //$NON-NLS-1$

        private String bssApiForceURL = "http://bssapi-prd1.bou.cloudwatt.net/rest/public/";

        public Builder(String email, String password) {
            this.email = email;
            this.password = password;
        }

        /**
         * Override the User-Agent used by the library
         * 
         * @param userAgent the User Agent to use instead of the default one
         * @return itself
         */
        public Builder overrideUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Don't use default Keystone Enpoint
         * 
         * @param keystonePublicEndpoint the value to use instead of default one
         * @return itself
         */
        public Builder keystonePublicEndpoint(URL keystonePublicEndpoint) {
            this.keystonePublicEndpoint = keystonePublicEndpoint.toExternalForm();
            return this;
        }

        /**
         * Override BSS Endpoint
         * 
         * @param endpoint the endpoint to use
         * @return itself
         */
        public Builder overrideBSSAPIEndpoint(URL endpoint) {
            this.bssApiForceURL = endpoint.toExternalForm();
            return this;
        }

        /**
         * Set an HTTP Proxy
         * 
         * @param proxy the proxy to use
         * @return ifself
         */
        public Builder setHttpProxy(HttpHost proxy) {
            webClientBuilder.setProxy(proxy);
            return this;
        }

        private final HttpClientBuilder webClientBuilder = HttpClientBuilder.create();

        /**
         * Build the API, try to connect !
         * 
         * @return an object you can play with !
         * @throws IOException if connection cannot be made to APIs
         * @throws TooManyRequestsException If you try to call us too many times, too fast
         */
        public BSSAcountFactory build() throws IOException, TooManyRequestsException {
            final String url = buildTokensUrl(keystonePublicEndpoint);
            webClientBuilder.setDefaultHeaders(Collections.singleton(new BasicHeader("User-Agent", userAgent))); //$NON-NLS-1$
            final WebClient client = new WebClient(webClientBuilder.build());
            final Optional<TokenResult> access = TokenResult.getToken(client,
                                                                      url,
                                                                      TokenResult.buildCrendentialsPayload(email,
                                                                                                           password));

            if (!access.isPresent()) {
                throw new IOException("Could no get token from URL: '" + url + "', got a HTTP 404 code");
            }

            return new BSSAcountFactory(client,
                                        keystonePublicEndpoint,
                                        bssApiForceURL,
                                        email,
                                        password,
                                        access.get().getAccess());
        }
    }

    private final ApiContext context;

    private BSSAcountFactory(final WebClient client, final String keystonePublicEndpoint, final String bssApi,
            final String email, final String password, final TokenAccess access) {
        this.context = new ApiContextImpl(keystonePublicEndpoint, client, bssApi, access, password);
    }

    /**
     * Get the BSS API Handler
     * 
     * @return the BSS API Handler, ready to use
     * @throws IOException, TooManyRequestsException
     */
    public BSSApiHandler getHandler() throws IOException, TooManyRequestsException {
        return new BSSHandlerImpl(context);
    }

}
