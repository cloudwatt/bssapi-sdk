package com.cloudwatt.apis.bss.spec.domain.keystone;

/**
 * Interface implemented by tenants result
 * 
 * @author pierre souchay
 *
 */
public interface TenantIFace {

    /**
     * Return the ID of tenant
     * 
     * @return a not null ID
     */
    public String getId();

    /**
     * Get the name of tenant
     * 
     * @return a not null name
     */
    public String getName();

    /**
     * Get the description of tenat
     * 
     * @return a not null description of tenant
     */
    public String getDescription();

    /**
     * Tells whether you can use the tenant
     * 
     * @return true if you can use it
     */
    public boolean isEnabled();

}
