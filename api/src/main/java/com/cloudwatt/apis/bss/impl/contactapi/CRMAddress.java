package com.cloudwatt.apis.bss.impl.contactapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class CRMAddress {

    /**
     * JSON Property name
     */
    public final static String PROPERTY_ADDRESS = "street";//$NON-NLS-1$

    /**
     * JSON Property name
     */
    public final static String PROPERTY_CITY = "city";//$NON-NLS-1$

    /**
     * JSON Property name
     */
    public final static String PROPERTY_COUNTRY = "country";//$NON-NLS-1$

    /**
     * JSON Property name
     */
    public final static String PROPERTY_POSTALCODE = "postalcode"; //$NON-NLS-1$

    /**
     * JSON Property name
     */
    public final static String PROPERTY_STATE = "state";//$NON-NLS-1$

    @JsonProperty(PROPERTY_ADDRESS)
    private final String address;

    @JsonProperty(PROPERTY_CITY)
    private final String city;

    @JsonProperty(PROPERTY_COUNTRY)
    private final String isoCountryCode;

    @JsonProperty(PROPERTY_POSTALCODE)
    private final String postCode;

    @JsonProperty(PROPERTY_STATE)
    private String state;

    /**
     * @param address
     * @param city
     * @param isoCountryCode
     */
    @JsonCreator
    public CRMAddress(@JsonProperty(PROPERTY_ADDRESS) String address,
            @JsonProperty(PROPERTY_POSTALCODE) String postCode, @JsonProperty(PROPERTY_CITY) String city,
            @JsonProperty(PROPERTY_COUNTRY) String isoCountryCode,
            @JsonProperty(value = PROPERTY_STATE, required = false) String state) {
        super();
        this.address = address;
        this.city = city;
        this.postCode = postCode;
        this.isoCountryCode = isoCountryCode;
        this.state = state;
    }

    /**
     * get the address
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * get the city
     * 
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * get the isoCountryCode
     * 
     * @return the isoCountryCode
     */
    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    /**
     * get the postCode
     * 
     * @return the postCode
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * get the state
     * 
     * @return the state
     */
    public String getState() {
        return state;
    }

}
