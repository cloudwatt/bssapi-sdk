package com.cloudwatt.apis.bss.spec.domain.account.openstack;

/**
 * 
 * @author pierre souchay
 * @since 0.2.5
 *
 */
public interface OpenstackRole {

    /**
     * Get the Openstack ID of this role
     * 
     * @return the Openstack ID f Role
     */
    public String getId();

    /**
     * Get the Symbolic name of Role
     * 
     * @return the name of Openstack Role
     */
    public String getName();

}
