package com.cloudwatt.apis.bss.spec.domain.consumption.lbaas;

import com.cloudwatt.apis.bss.spec.domain.consumption.RegionalResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.ResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.TypedHourlyEvent;

import java.util.UUID;

/**
 * Created by werner on 05/01/16.
 */
public interface HourVipOpenstackAggregatedMetricEvent extends TypedHourlyEvent, ResourceEvent, RegionalResourceEvent {

    /**
     * Get the list of member attached to the pool
     *
     * @return the list of member ID
     */
    public Iterable<UUID> getMemberIds();

    /**
     * Get the number of member attached to the pool
     *
     * @return the number of member
     */
    public int getNumberOfMember();

    /**
     * Get the pool ID attached to the vip
     *
     * @return the pool ID attached to the vip
     */
    public UUID getPoolId();
}
