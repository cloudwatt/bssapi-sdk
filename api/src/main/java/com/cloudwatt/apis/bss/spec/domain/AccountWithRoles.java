package com.cloudwatt.apis.bss.spec.domain;

import java.util.Set;
import com.cloudwatt.apis.bss.spec.domain.account.AccountMinimalInformation;
import com.google.common.base.Optional;

/**
 * Account that exposes named roles (don't use it for other purposes than display) and Capabilities that define what
 * calls can be performed on the account.
 * 
 * @author pierre souchay
 *
 */
public interface AccountWithRoles extends Account {

    /**
     * Get the named roles the user has
     * 
     * @return a set of high level roles (read-only)
     */
    public Set<String> getNamedRoles();

    /**
     * Get the capabilities the caller has on the account. Capabilities define what APIs you can call on a specific
     * account.
     * 
     * @return a collection of String representing the capabilities (read-only)
     */
    public Set<String> getCaps();

    /**
     * Get minimal information about account. Will be filled most of the time except in case of some maintenance
     * operations
     * 
     * @return some basic information about accounts
     */
    public Optional<AccountMinimalInformation> getAccountMinimalInformation();
}
