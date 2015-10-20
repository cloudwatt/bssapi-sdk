package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;

/**
 * API to get the list of Users having roles on this account
 * 
 * @author pierre souchay
 *
 */
public interface AccountRolesListApi {

    /**
     * Get the list of roles
     * 
     * @return a collection of roles
     */
    Iterable<IdentityToAccountRole> get() throws IOException, TooManyRequestsException;

    /**
     * Add / remove roles
     * 
     * @return if you are allowed to, return the roles
     */
    Optional<RolesEditApi> getEditRolesApi();

}
