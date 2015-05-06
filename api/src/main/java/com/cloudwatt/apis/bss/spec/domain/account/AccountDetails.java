package com.cloudwatt.apis.bss.spec.domain.account;

import com.cloudwatt.apis.bss.spec.domain.Account;

public interface AccountDetails extends Account {

    public String getEmail();

    public String getName();

    public String getBillingAddress();

    public String getBillingCity();

    public String getBillingCountry();

}
