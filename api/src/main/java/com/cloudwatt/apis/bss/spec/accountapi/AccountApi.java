package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
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
    public Optional<AccountDetailApi> getAccountDetails() throws IOException, TooManyRequestsException;

    /**
     * If available, let you browse the users having roles on this account
     * 
     * @return the list of high level roles on the account
     * @throws IOException
     * @throws TooManyRequestsException
     */
    public Optional<AccountRolesListApi> getRolesListApi() throws IOException, TooManyRequestsException;

}
