package com.cloudwatt.apis.bss.spec.domain;

import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;

/**
 * This interface let you call some public APIs scoped on a {@link AccountWithRoles}
 * 
 * @author pierre souchay
 *
 */
public interface AccountWithRolesWithOperations extends AccountWithRoles {

    /**
     * Get the API to retrieve and modify informations about account
     * 
     * @return the API to perform the operations on given account
     */
    public AccountApi getApi();

}
