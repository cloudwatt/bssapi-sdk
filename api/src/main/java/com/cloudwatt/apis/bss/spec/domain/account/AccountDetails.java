package com.cloudwatt.apis.bss.spec.domain.account;

import com.cloudwatt.apis.bss.spec.domain.Account;

/**
 * Account details may be available to users having cap ACCOUNT_SHOW
 * 
 * @author pierre souchay
 *
 */
public interface AccountDetails extends Account, AccountMinimalInformation {

    public String getBillingAddress();

    public String getBillingCity();

    public String getBillingAddressPostCode();

    public String getBillingCountry();

}
