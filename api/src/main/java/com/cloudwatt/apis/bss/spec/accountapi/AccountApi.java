package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
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
    public Optional<AccountDetailApi> getAccountDetails() throws IOException;

}
