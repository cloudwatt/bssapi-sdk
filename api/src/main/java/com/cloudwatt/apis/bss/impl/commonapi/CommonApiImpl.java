/**
 * 
 */
package com.cloudwatt.apis.bss.impl.commonapi;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.http.client.methods.HttpGet;
import com.cloudwatt.apis.bss.impl.ApiContext;
import com.cloudwatt.apis.bss.impl.Constants;
import com.cloudwatt.apis.bss.impl.JSONUtilities;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;

/**
 * @author pierre souchay
 *
 */
public class CommonApiImpl implements CommonApi {

    /**
     * Constructor
     * 
     * @param context
     */
    public CommonApiImpl(ApiContext context) {
        this.context = context;
    }

    private final ApiContext context;

    /**
     * @see com.cloudwatt.apis.bss.spec.commonapi.CommonApi#getVersion()
     */
    @Override
    public String getVersion() throws IOException, TooManyRequestsException {
        return context.getWebClient()
                      .doRequestAndRetrieveResultAsJSON(JsonNode.class,
                                                        new HttpGet(context.buildPublicApiUrl("ping", //$NON-NLS-1$
                                                                                              Collections.<String, String> emptyMap())),
                                                        Optional.<TokenAccess> of(context.getTokenAccess()))
                      .get()
                      .path("id") //$NON-NLS-1$
                      .asText();
    }

    @Override
    public Map<String, String> getAllCapsAndDescriptions(Locale locale) throws IOException, TooManyRequestsException {
        final HttpGet get = new HttpGet(context.buildPublicApiUrl("bss/1/caps", //$NON-NLS-1$
                                                                  Collections.<String, String> emptyMap()));
        get.addHeader(Constants.HEADER_NAME_LANGUAGE, locale.toLanguageTag());
        JsonNode node = context.getWebClient()
                               .doRequestAndRetrieveResultAsJSON(JsonNode.class,
                                                                 get,
                                                                 Optional.<TokenAccess> of(context.getTokenAccess()))
                               .get();
        return JSONUtilities.jsonNodeToMap(node);
    }

    @Override
    public Map<String, String> getAllAccountTypes(Locale locale) throws IOException, TooManyRequestsException {
        final HttpGet get = new HttpGet(context.buildPublicApiUrl("bss/1/accountTypes", //$NON-NLS-1$
                                                                  Collections.<String, String> emptyMap()));
        get.addHeader(Constants.HEADER_NAME_LANGUAGE, locale.toLanguageTag());
        JsonNode node = context.getWebClient()
                               .doRequestAndRetrieveResultAsJSON(JsonNode.class,
                                                                 get,
                                                                 Optional.<TokenAccess> of(context.getTokenAccess()))
                               .get();
        return JSONUtilities.jsonNodeToMap(node);
    }

    private final Set<String> countryCodes = new TreeSet<String>();

    private static class CountryCodesList {

        private final Set<String> countryCodes;

        @JsonCreator
        public CountryCodesList(@JsonProperty("country_codes") Collection<String> countryCodes) {
            this.countryCodes = new TreeSet<String>(countryCodes);
        }

        public Set<String> getCountryCodes() {
            return countryCodes;
        }
    }

    @Override
    public Set<String> getCountryCodes() throws IOException, TooManyRequestsException {
        if (!countryCodes.isEmpty()) {
            return Collections.unmodifiableSet(countryCodes);
        }
        final HttpGet get = new HttpGet(context.buildPublicApiUrl("bss/1/countryCode", //$NON-NLS-1$
                                                                  Collections.<String, String> emptyMap()));
        CountryCodesList node = context.getWebClient()
                                       .doRequestAndRetrieveResultAsJSON(CountryCodesList.class,
                                                                         get,
                                                                         Optional.<TokenAccess> of(context.getTokenAccess()))
                                       .get();
        countryCodes.addAll(node.getCountryCodes());
        return Collections.unmodifiableSet(countryCodes);
    }
}
