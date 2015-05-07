package com.cloudwatt.apis.bss.spec.domain.account;

import java.util.Date;

public interface OwnedTenant {

    public Date getCreationTime();

    public String getCustomerId();

    public String getTenantType();

    public String getTenantId();
}
