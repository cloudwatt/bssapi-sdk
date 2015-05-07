package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

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

}
