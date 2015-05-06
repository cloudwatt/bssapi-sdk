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
        ACCOUNT_SHOW();
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
