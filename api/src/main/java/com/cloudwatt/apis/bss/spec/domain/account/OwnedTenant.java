package com.cloudwatt.apis.bss.spec.domain.account;

import java.util.Date;

/**
 * A tenant owned by an account
 * 
 * @author pierre souchay
 *
 */
public interface OwnedTenant {

    /**
     * The creation time of tenant
     * 
     * @return the UTC creation time
     */
    public Date getCreationTime();

    /**
     * The customer Id owning the tenant
     * 
     * @return the account id
     */
    public String getCustomerId();

    /**
     * The kind of tenant, aka compute or storage
     * 
     * @return the tenant type
     */
    public String getTenantType();

    /**
     * The tenant Id
     * 
     * @return the id of tenant in Openstack
     */
    public String getTenantId();
}
