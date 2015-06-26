/**
 * 
 */
package com.cloudwatt.apis.bss.impl.accountapi;

import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

/**
 * @author pierre
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetailsImpl implements AccountDetails {

    private final String customerId;

    private final String phone_office;

    private final String email;

    private final String siret_siren;

    private final String corporateName;

    public String getPhoneOffice() {
        return phone_office;
    }

    @JsonCreator
    public AccountDetailsImpl(@JsonProperty(value = "customer_id", required = false) String customerId,
            @JsonProperty(value = "phone_office", required = false) String phone_office,
            @JsonProperty(value = "email", required = false) String email,
            @JsonProperty(value = "siret_siren", required = false) String siret_siren,
            @JsonProperty(value = "name", required = false) String name,
            @JsonProperty(value = "corporate_name", required = false) String corporateName,
            @JsonProperty(value = "billing_address_street", required = false) String billing_address_street,
            @JsonProperty(value = "billing_address_postalcode", required = false) String billing_address_postalcode,
            @JsonProperty(value = "billing_address_city", required = false) String billing_address_city,
            @JsonProperty(value = "billing_address_country", required = false) String billing_address_country) {
        super();
        this.customerId = customerId;
        this.phone_office = phone_office;
        this.email = email;
        this.siret_siren = siret_siren;
        this.corporateName = corporateName;
        this.name = name;
        this.billing_address_street = billing_address_street;
        this.billing_address_postalcode = billing_address_postalcode;
        this.billing_address_city = billing_address_city;
        this.billing_address_country = billing_address_country;
    }

    public String getSiret() {
        return siret_siren;
    }

    private final String name;

    private final String billing_address_street;

    private final String billing_address_postalcode;

    private final String billing_address_city;

    private final String billing_address_country;

    /**
     * @see com.cloudwatt.apis.bss.spec.domain.Account#getCustomerId()
     */
    @Override
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @see com.cloudwatt.apis.bss.spec.domain.account.AccountDetails#getEmail()
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * @see com.cloudwatt.apis.bss.spec.domain.account.AccountDetails#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @see com.cloudwatt.apis.bss.spec.domain.account.AccountDetails#getBillingAddress()
     */
    @Override
    public String getBillingAddress() {
        return billing_address_street;
    }

    @Override
    public String getBillingAddressPostCode() {
        return billing_address_postalcode;
    }

    @Override
    public String getBillingCity() {
        return billing_address_city;
    }

    @Override
    public String getBillingCountry() {
        return billing_address_country;
    }

    @Override
    public Optional<String> getCorporateName() {
        return Optional.fromNullable(corporateName);
    }

}
