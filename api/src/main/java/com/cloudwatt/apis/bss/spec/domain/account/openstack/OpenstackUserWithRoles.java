package com.cloudwatt.apis.bss.spec.domain.account.openstack;

import java.util.Collection;

/**
 * A Openstack User and its roles
 * 
 * @author pierre souchay
 * @since 0.2.5
 */
public interface OpenstackUserWithRoles {

    /**
     * The Openstack User name (usually a UUID aka IAM UserID)
     * 
     * @return the username
     */
    public String getUserName();

    /**
     * The Email of the Openstack User
     * 
     * @return the email of the User
     */
    public String getEmail();

    /**
     * Get the technical Openstack User Id
     * 
     * @return the technical OPenstack User-Id
     */
    public String getOpenstackId();

    /**
     * The list of roles of this User for the Tenant
     * 
     * @return the collection of Roles
     */
    public Collection<OpenstackRole> getRoles();

}
