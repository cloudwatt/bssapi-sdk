package com.cloudwatt.apis.bss.spec.domain;

import java.util.Set;

/**
 * Roles are granted between Accounts and Identities and give capabilities to use in order to perform some operation or
 * get some informations
 * 
 * @author pierre souchay
 *
 */
public interface BSSRole {

    /**
     * Get the relation between the identity and the role
     * 
     * @return the identity
     */
    public Identity getIdentity();

    /**
     * Get the account
     * 
     * @return
     */
    public Account getAccount();

    /**
     * The list of named roles.
     * 
     * Here only for display, always use capabilities to know what you can do on an account
     * 
     * @return a not null immutable set of the roles between the account and the identity
     */
    public Set<NamedRole> getNamedRoles();

    /**
     * The capabilities list all features enabled on an Account for an Identity. It is the sum of all the capabilities
     * represented by all named roles
     * 
     * @return a not null set of capabilities
     */
    public Set<BSSCap> getCapabilities();
}
