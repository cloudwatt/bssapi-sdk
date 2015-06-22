package com.cloudwatt.apis.bss.spec.domain.consumption;

import java.util.UUID;

public interface ResourceTypedEvent extends TypedHourlyEvent {

    /**
     * Get the resource Id
     * 
     * @return the resourceId
     */
    public UUID getResourceId();

}
