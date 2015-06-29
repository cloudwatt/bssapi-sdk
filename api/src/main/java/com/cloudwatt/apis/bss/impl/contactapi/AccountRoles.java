/**
 * @author pierre souchay
 */
package com.cloudwatt.apis.bss.impl.contactapi;

import java.util.Set;
import com.cloudwatt.apis.bss.impl.accountapi.MinimumAccountInformationImpl;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRoles;
import com.cloudwatt.apis.bss.spec.domain.account.AccountMinimalInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

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
    public AccountRoles(
            @JsonProperty(value = "account", required = true) String account,
            @JsonProperty(value = "roles", required = true) Set<String> roles,
            @JsonProperty(value = "caps", required = true) Set<String> caps,
            @JsonProperty(value = "accountInformation", required = false) MinimumAccountInformationImpl accountInformation) {
        this.account = account;
        this.roles = roles;
        this.caps = caps;
        this.accountInformation = accountInformation;
    }

    private final String account;

    private final AccountMinimalInformation accountInformation;

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

    @Override
    public Optional<AccountMinimalInformation> getAccountMinimalInformation() {
        return Optional.fromNullable(accountInformation);
    }
}
