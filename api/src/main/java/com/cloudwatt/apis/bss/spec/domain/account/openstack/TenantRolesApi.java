/**
 * 
 */
package com.cloudwatt.apis.bss.spec.domain.account.openstack;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * @author pierre souchay
 * @since 0.2.5
 */
public interface TenantRolesApi {

    /**
     * Get the Users for this tenant
     * 
     * @return a collection of users having roles on the tenant
     */
    Iterable<OpenstackUserWithRoles> getUsers() throws IOException, TooManyRequestsException;

}
