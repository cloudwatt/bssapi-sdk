/**
 * 
 */
package com.cloudwatt.apis.bss.impl.domain.keystone;

import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;

/**
 * Representation of return from keystone call to list tenants
 * 
 * @author pierre souchay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantsResult {

    @JsonCreator
    public TenantsResult(@JsonProperty(value = "tenants", required = true) Iterable<Tenant> tenants,
            @JsonProperty(value = "tenants_links", required = false) Iterable<JsonNode> tenants_links) {
        ImmutableSet.Builder<TenantIFace> tenantsBuilder = new ImmutableSet.Builder<TenantIFace>();
        for (Tenant t : tenants){
            tenantsBuilder.add(t);
        }
        this.tenants = tenantsBuilder.build();
        this.tenants_links = tenants_links;
    }

    private final Iterable<TenantIFace> tenants;

    private final Iterable<?> tenants_links;

    /**
     * Get the tenants the user has access to
     * 
     * @return the list of tenants
     */
    public Iterable<TenantIFace> getTenants() {
        return tenants;
    }

    /**
     * Get the tenants links (unused)
     * 
     * @return the list of tenants
     */
    public Iterable<?> getTenants_links() {
        return tenants_links;
    }

}
