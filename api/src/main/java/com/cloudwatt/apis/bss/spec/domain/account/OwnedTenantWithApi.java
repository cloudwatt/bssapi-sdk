package com.cloudwatt.apis.bss.spec.domain.account;

import com.cloudwatt.apis.bss.spec.accountapi.ConsumptionApi;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.TenantRolesApi;
import com.google.common.base.Optional;

public interface OwnedTenantWithApi extends OwnedTenant {

    /**
     * Get the consumption API if available
     * 
     * @return the Consumption Api If available, {@link Optional#absent()} otherwise
     */
    public Optional<ConsumptionApi> getConsumptionApi();

    /**
     * Get the Users on tenant and their roles
     * 
     * @return the {@link TenantRolesApi} if available, {@link Optional#absent()} otherwise
     */
    public Optional<TenantRolesApi> getOpenstackRolesApi();

}
