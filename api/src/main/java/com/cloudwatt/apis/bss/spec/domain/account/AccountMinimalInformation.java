package com.cloudwatt.apis.bss.spec.domain.account;

import com.google.common.base.Optional;

/**
 * Get minimal information about an account (information given even when you don't have any capability)
 * 
 * @author pierre souchay
 *
 */
public interface AccountMinimalInformation {

    /**
     * The email for a billable account
     * 
     * @return the email (not null or empty)
     */
    public String getEmail();

    /**
     * The name of account
     * 
     * @return the name of account
     */
    public String getName();

    /**
     * Get the corporate name if set
     * 
     * @return the corporate name
     */
    public Optional<String> getCorporateName();
}
