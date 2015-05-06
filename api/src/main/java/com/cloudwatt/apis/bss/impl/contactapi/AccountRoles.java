/**
 * @author pierre souchay
 */
package com.cloudwatt.apis.bss.impl.contactapi;

import java.util.Set;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRoles;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Implementation class
 * 
 * @author Pierre Souchay
 * 
 */
public class AccountRoles implements AccountWithRoles {

    /**
     * Constructor
     * 
     * @param account
     * @param roles
     * @param caps
     */
    @JsonCreator
    public AccountRoles(@JsonProperty(value = "account", required = true) String account,
            @JsonProperty(value = "roles", required = true) Set<String> roles,
            @JsonProperty(value = "caps", required = true) Set<String> caps) {
        this.account = account;
        this.roles = roles;
        this.caps = caps;
    }

    private final String account;

    private final Set<String> roles;

    private final Set<String> caps;

    /**
     * get the account
     * 
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * get the roles
     * 
     * @return the roles
     */
    @Override
    public Set<String> getNamedRoles() {
        return roles;
    }

    @Override
    public String getCustomerId() {
        return getAccount();
    }

    @Override
    public Set<String> getCaps() {
        return caps;
    }

}
