package com.cloudwatt.apis.bss.spec.domain.account;

import com.cloudwatt.apis.bss.spec.accountapi.ConsumptionApi;
import com.google.common.base.Optional;

public interface OwnedTenantWithApi extends OwnedTenant {

    /**
     * Get the consumption API if available
     * 
     * @return the Consumption Api If available, {@link Optional#absent()} otherwise
     */
    public Optional<ConsumptionApi> getConsumptionApi();

}
