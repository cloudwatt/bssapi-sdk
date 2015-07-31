package com.cloudwatt.apis.bss.spec.domain;

/**
 * Object that describes capabilities
 * 
 * @author pierre souchay
 *
 */
public interface BSSCap {

    /**
     * Well known capabilities
     * 
     * @author pierre souchay
     *
     */
    public static enum KNOWN_CAPS {
        /**
         * Show detailed information about an account
         */
        ACCOUNT_SHOW,
        /**
         * List the invoices for Given Account
         */
        BILLING_INVOICES,
        /**
         * Show RAW Consumption
         */
        CONSUMPTION,
        /**
         * Show all the Users and their roles having access to the account
         */
        ACCOUNT_ROLES_LIST,
        /**
         * Edit all the Users and their roles having access to the account
         */
        ACCOUNT_ROLES_EDIT,
        /**
         * List the tenants owned by account
         */
        TENANTS_LIST;
    };

    /**
     * Get the name of cap
     * 
     * @return the cap name
     */
    String getName();

    /**
     * Get the description of capability
     * 
     * @return the description
     */
    String getDescription();
}
