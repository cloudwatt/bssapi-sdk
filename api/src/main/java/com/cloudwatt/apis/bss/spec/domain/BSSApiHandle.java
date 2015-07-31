/**
 * 
 */
package com.cloudwatt.apis.bss.spec.domain;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.cloudwatt.apis.bss.spec.commonapi.FindUserApi;
import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;

/**
 * The descriptor you can use to work with all the APIs
 * 
 * @author pierre souchay
 *
 */
public interface BSSApiHandle {

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
     * Get the Identity API if available. Identity API is used to find User that may exist or you might want to invite
     * 
     * @return The API to find users (to give them roles to Tenants or Accounts)
     */
    public Optional<FindUserApi> getFindUserApi();

    /**
     * List the tenants I have access to
     * 
     * @return the list of tenants I can work with
     */
    public Iterable<TenantIFace> getTenantsList() throws IOException, TooManyRequestsException;

    /**
     * Get the account API
     * 
     * @param customerId the customerId
     * @return the API if available
     */
    public Optional<AccountApi> getAccountApi(String customerId);
}
