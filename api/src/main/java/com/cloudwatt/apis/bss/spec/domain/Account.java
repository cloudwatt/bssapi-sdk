/**
 * 
 */
package com.cloudwatt.apis.bss.spec.domain;

/**
 * The account is the concept that represent a not physical entity responsible for paying the cloud resources
 * 
 * @author pierre souchay
 *
 */
public interface Account {

    /**
     * Get the customerId of given account
     * 
     * @return the CustomerId, a string that will be displayed in invoices
     */
    public String getCustomerId();
}
