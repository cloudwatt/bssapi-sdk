package com.cloudwatt.apis.bss.impl.contactapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class BSSRolesInformation {

    private final String customer_id;

    private final String usage_type;

    private final String user_id;

    private Optional<String> user_email;

    /**
     * Constructor
     * 
     * @param customer_id the Customer ID
     * @param role the role as a String (CRM Role)
     * @param user_id the User ID (IAM User ID)
     */
    public BSSRolesInformation(String customer_id, String role, String user_id) {
        super();
        this.customer_id = customer_id;
        this.usage_type = role;
        this.user_id = user_id;
    }

    public Optional<String> getUser_email() {
        return user_email;
    }

    public void setUser_email(Optional<String> user_email) {
        this.user_email = user_email;
    }

    public String getCustomer_Id() {
        return customer_id;
    }

    public String getUsage_type() {
        return usage_type;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getUser_id() {
        return user_id;
    }

}
