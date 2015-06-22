package com.cloudwatt.apis.bss.spec.accountapi;

import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.google.common.base.Optional;

/**
 * Entry point for all operations about an account
 * 
 * @author pierre souchay
 *
 */
public interface AccountApi {

    /**
     * Get Account info details if available
     * 
     * @return the {@link AccountDetailApi}, if available
     */
    public Optional<AccountDetailApi> getAccountDetails();

    /**
     * If available, let you browse the users having roles on this account
     * 
     * @return the list of high level roles on the account
     */
    public Optional<AccountRolesListApi> getRolesListApi();

    /**
     * Retrieve the list of owned tenants for the account, ie: the tenants the account is paying for
     * 
     * @return {@link Optional#absent()} if API is not available for the logged user, the API otherwise
     */
    public Optional<OwnedTenantsListApi> getOwnedTenantsApi();

    /**
     * Retrieve the AccountInvoicesApi if available
     * 
     * @return {@link Optional#absent()} if not available, the Api otherwise
     */
    public Optional<AccountInvoicesApi> getInvoicesApi();

    /**
     * Get the Consumption API if available
     * 
     * @return {@link Optional#absent()} if not available, the API otherwise
     */
    public Optional<ConsumptionApi> getConsumptionApi(OwnedTenant tenant);

}
