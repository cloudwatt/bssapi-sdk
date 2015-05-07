package com.cloudwatt.apis.bss.impl.domain.keystone;

import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Tenant representation
 * 
 * @author pierre souchay
 */
public class Tenant implements TenantIFace {

    private final String id;

    private final String name;

    private final String description;

    private final boolean enabled;

    @JsonCreator
    public Tenant(@JsonProperty(value = "id", required = false) String id,
            @JsonProperty(value = "name", required = false) String name,
            @JsonProperty(value = "description", required = false) String description,
            @JsonProperty(value = "enabled", required = false) Boolean enabled) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled == null || enabled.booleanValue();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
