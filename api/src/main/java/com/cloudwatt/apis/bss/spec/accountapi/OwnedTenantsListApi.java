package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenantWithApi;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * List the tenants that are owned by an account
 * 
 * @author pierre souchay
 *
 */
public interface OwnedTenantsListApi {

    /**
     * List all the tenants owned by account
     * 
     * @return a not null list of tenants
     */
    public Iterable<OwnedTenantWithApi> get() throws IOException, TooManyRequestsException;

}
