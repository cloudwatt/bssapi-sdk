/**
 * 
 */
package com.cloudwatt.apis.bss.spec.commonapi;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * Global information about public API
 * 
 * @author pierre souchay
 */
public interface CommonApi {

    /**
     * Get the version of API
     * 
     * @return the version of API
     * @throws IOException, TooManyRequestsException
     */
    public String getVersion() throws IOException, TooManyRequestsException;

    /**
     * Get all the capabilities description in specified locale.
     * 
     * @param locale the capabilities description locale. If not supported, data will be returned in English
     * @return a Map of capabilities name and descriptions
     * @throws IOException, TooManyRequestsException
     */
    public Map<String, String> getAllCapsAndDescriptions(Locale locale) throws IOException, TooManyRequestsException;

    /**
     * Get all possible account types in caller locale
     * 
     * @param locale the capabilities description locale. If not supported, data will be returned in English
     * @return a Map of account types and their description
     * @throws IOException, TooManyRequestsException
     * 
     */
    public Map<String, String> getAllAccountTypes(Locale locale) throws IOException, TooManyRequestsException;

    /**
     * Get the list of valid country codes
     * 
     * @return a set of Country code
     * @throws IOException, TooManyRequestsException if web API cannot be reached
     */
    public Set<String> getCountryCodes() throws IOException, TooManyRequestsException;

}
