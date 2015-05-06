/**
 * $URL$
 *
 * $LastChangedBy$ - $LastChangedDate$
 */
package com.cloudwatt.apis.bss.impl.contactapi;

import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;

/**
 * The Object to create / update a contact
 * 
 * @author Pierre Souchay <pierre@souchay.net>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {

    public final static String PROPERTY_DO_NOT_CALL = "do_not_call"; //$NON-NLS-1$

    public final static String PROPERTY_EMAILS = "emails"; //$NON-NLS-1$

    public final static String PROPERTY_FIRST_NAME = "first_name"; //$NON-NLS-1$

    public final static String PROPERTY_LAST_NAME = "last_name"; //$NON-NLS-1$

    public final static String PROPERTY_LOGIN_EMAIL = "loginEmail"; //$NON-NLS-1$

    public final static String PROPERTY_OPTION_NEWSLETTER = "optin_newsletter"; //$NON-NLS-1$

    public final static String PROPERTY_PHONE_WORK = "phone_work"; //$NON-NLS-1$

    public final static String PROPERTY_PRIMARY_ADDRESS_PREFIX = "primary_address_"; //$NON-NLS-1$

    public final static String PROPERTY_TITLE = "title"; //$NON-NLS-1$

    private static final Optional<Boolean> convertAnyToBoolean(JsonNode node) {
        if (node == null || node.isNull())
            return Optional.absent();
        return Optional.of(0 != node.asInt());
    }

    @JsonUnwrapped(prefix = PROPERTY_PRIMARY_ADDRESS_PREFIX)
    private final CRMAddress contactAddress;

    @JsonProperty(PROPERTY_DO_NOT_CALL)
    private final boolean doNotCall;

    @JsonProperty(PROPERTY_LOGIN_EMAIL)
    private final String loginEmail;

    @JsonProperty(PROPERTY_FIRST_NAME)
    private final String firstName;

    @JsonProperty(PROPERTY_LAST_NAME)
    private final String lastName;

    @JsonProperty(PROPERTY_OPTION_NEWSLETTER)
    private final boolean optionNewsletter;

    @JsonProperty(PROPERTY_TITLE)
    private final String title;

    @JsonProperty(PROPERTY_PHONE_WORK)
    private final String workPhone;

    /**
     * Constructor
     * 
     * @param doNotCall
     * @param loginEmail
     * @param firstName
     * @param lastName
     * @param title
     */
    @JsonCreator
    public Contact(
            @JsonProperty(required = false, value = PROPERTY_DO_NOT_CALL) JsonNode doNotCall,
            @JsonProperty(required = true, value = PROPERTY_LOGIN_EMAIL) String loginEmail,
            @JsonProperty(required = true, value = PROPERTY_FIRST_NAME) String firstName,
            @JsonProperty(required = true, value = PROPERTY_LAST_NAME) String lastName,
            @JsonProperty(required = false, value = PROPERTY_TITLE) String title,
            @JsonProperty(required = true, value = PROPERTY_PHONE_WORK) String workPhone,
            @JsonProperty(required = false, value = PROPERTY_OPTION_NEWSLETTER) JsonNode optionNewsletter,
            @JsonProperty(required = true, value = PROPERTY_PRIMARY_ADDRESS_PREFIX + CRMAddress.PROPERTY_ADDRESS) String address,
            @JsonProperty(required = true, value = PROPERTY_PRIMARY_ADDRESS_PREFIX + CRMAddress.PROPERTY_POSTALCODE) String postCode,
            @JsonProperty(required = true, value = PROPERTY_PRIMARY_ADDRESS_PREFIX + CRMAddress.PROPERTY_CITY) String city,
            @JsonProperty(required = true, value = PROPERTY_PRIMARY_ADDRESS_PREFIX + CRMAddress.PROPERTY_COUNTRY) String isoCountryCode,
            @JsonProperty(value = PROPERTY_PRIMARY_ADDRESS_PREFIX + CRMAddress.PROPERTY_STATE, required = false) String state) {
        // if (loginEmail == null || firstName == null || lastName == null) {
        // throw new IllegalArgumentException("loginEmail, firstName, lastName must not be null");
        // }
        this.contactAddress = new CRMAddress(address, postCode, city, isoCountryCode, state);
        this.doNotCall = convertAnyToBoolean(doNotCall).or(true);
        this.loginEmail = loginEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.workPhone = workPhone;
        this.optionNewsletter = convertAnyToBoolean(optionNewsletter).or(true);
    }

    /**
     * get the contactAddress
     * 
     * @return the contactAddress
     */
    @JsonUnwrapped(prefix = PROPERTY_PRIMARY_ADDRESS_PREFIX)
    public CRMAddress getContactAddress() {
        return contactAddress;
    }

    /**
     * get the email
     *
     * @return the email
     */
    @JsonProperty(PROPERTY_EMAILS)
    public Iterable<String> getEmails() {
        return Collections.singleton(loginEmail);
    }

    /**
     * get the firstName
     * 
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get a formatted name with name, firstName
     * 
     * @return a formated name
     */
    @JsonIgnore
    public String getFormattedName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLastName());
        sb.append(", ").append(getFirstName()); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * get the lastName
     * 
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * get the loginEmail
     *
     * @return the loginEmail
     */
    public String getLoginEmail() {
        return loginEmail;
    }

    /**
     * get the title
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * get the workPhone
     * 
     * @return the workPhone
     */
    public String getWorkPhone() {
        return workPhone;
    }

    /**
     * get the doNotCall
     * 
     * @return the doNotCall
     */
    public boolean isDoNotCall() {
        return doNotCall;
    }

    /**
     * get the optionNewsletter
     * 
     * @return the optionNewsletter
     */

    public boolean isOptionNewsletter() {
        return optionNewsletter;
    }

}
