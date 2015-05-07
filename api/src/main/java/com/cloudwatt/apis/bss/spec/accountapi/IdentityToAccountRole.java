/**
 * 
 */
package com.cloudwatt.apis.bss.spec.accountapi;

/**
 * @author pierre
 *
 */
public interface IdentityToAccountRole {

    /**
     * Get the Account's customerId
     * 
     * @return a not null customerId
     */
    public String getCustomerId();

    /**
     * Get a symbolic role name
     * 
     * @return the role name
     */
    public String getUsageType();

    /**
     * Get a not null User Name
     * 
     * @return the User Name
     */
    public String getUserName();

    /**
     * Get a not null User Email
     * 
     * @return The User's email
     */
    public String getUserEmail();
}
