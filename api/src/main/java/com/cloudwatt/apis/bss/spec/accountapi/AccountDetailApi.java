package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * API to get detailed information about an account
 * 
 * @author pierre souchay
 *
 */
public interface AccountDetailApi {

    /**
     * Get the details about the account
     * 
     * @return a not null AccountDetails object where you can get informations about account
     * @throws IOException
     */
    AccountDetails get() throws IOException, TooManyRequestsException;

}
