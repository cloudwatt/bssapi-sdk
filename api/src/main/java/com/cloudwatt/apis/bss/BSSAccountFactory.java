package com.cloudwatt.apis.bss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import com.cloudwatt.apis.bss.impl.ApiContext;
import com.cloudwatt.apis.bss.impl.BSSHandlerImpl;
import com.cloudwatt.apis.bss.impl.Constants;
import com.cloudwatt.apis.bss.impl.TokenResult;
import com.cloudwatt.apis.bss.impl.TokenResult.AuthPayload;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.impl.WebClient;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.exceptions.HttpUnexpectedError;
import com.cloudwatt.apis.bss.spec.exceptions.IOExceptionLocalized;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.cloudwatt.apis.bss.spec.exceptions.WrongCredentialsException;
import com.google.common.base.Optional;

/**
 * Main Entry point.
 * 
 * Use the {@link Builder} to setup the parameters for connecting to the API, you can then call {@link Builder#build()}
 * to get the handle and start working with the API.
 * 
 * <p>
 * The typical use is the following:<br>
 * </p>
 * <p>
 * <code>
 *   BSSApiHandle handle = (new BSSAccountFactory.Builder(email, password).build()).getHandle();<br>
 *   BSSApiHandle mainApi = factory.getHandle();<br>
 *   <br>
 *   // Work with accounts<br>
 *   for (AccountWithRolesWithOperations account : mainApi.getAccounts()) {<br>
 *     &nbsp;&nbsp;// Do something with account<br>
 *     &nbsp;&nbsp;...<br>
 *   }<br>
 *   <br>
 *   // Work with common API<br>
 *   CommonApi commonApi = mainApi.getCommmonApi();<br>
 *   // Get the public API version on the server<br>
 *   commonApi.getVersion()<br>
 *   ...
 *   
 * </code>
 * </p>
 * 
 * @author pierre souchay
 *
 */
public class BSSAccountFactory {

    /**
     * Get the client API version
     * 
     * @return the client API version
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

    private static TokenResult refreshToken(WebClient client, String url, AuthPayload authPayload) throws IOException,
            IOExceptionLocalized, TooManyRequestsException {
        Optional<TokenResult> access = TokenResult.getToken(client, url, authPayload);
        if (!access.isPresent()) {
            throw new IOExceptionLocalized("IOExceptionLocalized.couldNotGetTokenFrom404", url); //$NON-NLS-1$
        } else {
            return access.get();
        }
    }

    private static final class ApiContextImpl implements ApiContext {

        /**
         * package protected constructor, please use the Builder instead
         * 
         * @param keystonePublicEndpoint
         * @param client
         * @param publicApiBase
         * @param tokenAccess
         * @param email used to refresh tokens
         * @param password used to refresh tokens
         */
        ApiContextImpl(String keystonePublicEndpoint, WebClient client, String publicApiBase, TokenAccess tokenAccess,
                String email, String password) {
            this.client = client;
            this.connectionEmail = email;
            this.publicApiBase = publicApiBase;
            this.tokenAccess = tokenAccess;
            this.keystonePublicEndpoint = keystonePublicEndpoint;
            this.password = password;
        }

        private final String connectionEmail;

        private final String keystonePublicEndpoint;

        private final WebClient client;

        private final String password;

        private final String publicApiBase;

        private volatile TokenAccess tokenAccess;

        @Override
        public WebClient getWebClient() {
            return client;
        }

        @Override
        public String buildPublicApiUrl(String relativePath, Map<String, String> queryParameters) {
            return createRequest(publicApiBase, relativePath, queryParameters);
        }

        @Override
        public TokenAccess getTokenAccess() throws IOException, TooManyRequestsException, IOExceptionLocalized {
            if ((new Date(System.currentTimeMillis() + 60000)).after(tokenAccess.getToken().getExpires())) {
                // OK, we will try to refresh the token
                final TokenResult access = refreshToken(client,
                                                        buildTokensUrl(keystonePublicEndpoint),
                                                        TokenResult.buildCrendentialsPayload(connectionEmail, password));
                this.tokenAccess = access.getAccess();
            }

            return tokenAccess;
        }

        @Override
        public String buildKeystoneUrl(String relativePath) {
            return createRequest(keystonePublicEndpoint, relativePath, Collections.<String, String> emptyMap());
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

    /**
     * Use the builder to connect to the API.
     * 
     * Only email and password are mandatory, use the methods of the builder only if you have some specific needs
     * 
     * @author pierre souchay
     *
     */
    public static class Builder {

        private String keystonePublicEndpoint = "https://identity.fr1.cloudwatt.com/v2.0"; //$NON-NLS-1$

        private final String email;

        private final String password;

        private String userAgent = System.getProperty("cwapi-ua", //$NON-NLS-1$
                                                      "horse-client/" + getClientApiVersion()); //$NON-NLS-1$

        private String bssApiForceURL = "https://bssapi.fr1.cloudwatt.com"; //$NON-NLS-1$

        /**
         * Builder Constructor
         * 
         * @param email email to use to connect to BSS APIs
         * @param password the password to use to connect to BSS APIs
         * @throws IllegalArgumentException if password or email are null or empty
         */
        public Builder(String email, String password) {
            if (email == null || email.trim().isEmpty())
                throw new IllegalArgumentException("email cannot be null or empty"); //$NON-NLS-1$
            if (password == null || password.trim().isEmpty())
                throw new IllegalArgumentException("password cannot be null or empty"); //$NON-NLS-1$
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
         * Set an HTTP Proxy, use this method to configure a proxy if you need a proxy to connect to Cloudwatt Public
         * APIs
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
         * @throws WrongCredentialsException bad credentials
         * @throws HttpUnexpectedError unexpected HTTP Error
         * @throws TooManyRequestsException If you try to call us too many times, too fast
         */
        public BSSAccountFactory build() throws IOException, TooManyRequestsException, WrongCredentialsException,
                HttpUnexpectedError {
            final String url = buildTokensUrl(keystonePublicEndpoint);
            webClientBuilder.setDefaultHeaders(Collections.singleton(new BasicHeader("User-Agent", userAgent))); //$NON-NLS-1$
            final WebClient client = new WebClient(webClientBuilder.build());
            final TokenResult access = refreshToken(client, url, TokenResult.buildCrendentialsPayload(email, password));

            return new BSSAccountFactory(client,
                                         keystonePublicEndpoint,
                                         bssApiForceURL,
                                         email,
                                         password,
                                         access.getAccess());
        }
    }

    private final ApiContext context;

    private BSSAccountFactory(final WebClient client, final String keystonePublicEndpoint, final String bssApi,
            final String email, final String password, final TokenAccess access) {
        this.context = new ApiContextImpl(keystonePublicEndpoint, client, bssApi, access, email, password);
    }

    /**
     * Get the BSS API Handle, keep this object in your application to work with the API
     * 
     * @return the BSS API Handler, ready to use
     * @throws IOException, TooManyRequestsException
     */
    public BSSApiHandle getHandle() throws IOException, TooManyRequestsException {
        return new BSSHandlerImpl(context);
    }

}
