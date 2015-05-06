/**
 * 
 */
package com.cloudwatt.apis.bss.spec.domain;

import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.google.common.base.Optional;

/**
 * @author pierre
 *
 */
public interface BSSApiHandler {

    /**
     * Get the identity you are connected with
     * 
     * @return the identity you are connected with
     */
    public Identity getIdentity();

    /**
     * List all the accounts the identity has
     * 
     * @return the list of account you can work with
     */
    public Iterable<AccountWithRolesWithOperations> getAccounts();

    /**
     * Get common API (Common function, version, public informations...)
     * 
     * @return the CommonApi
     */
    public CommonApi getCommmonApi();

    /**
     * Get the account API
     * 
     * @param customerId the customerId
     * @return the API if available
     */
    public Optional<AccountApi> getAccountApi(String customerId);
}
