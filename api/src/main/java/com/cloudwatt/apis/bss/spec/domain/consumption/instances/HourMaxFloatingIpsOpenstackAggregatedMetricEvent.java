package com.cloudwatt.apis.bss.spec.domain.consumption.instances;

import com.cloudwatt.apis.bss.spec.domain.consumption.TypedHourlyEvent;

/**
 * Consumption of Floating IPs per hour
 * 
 * @author pierre souchay
 *
 */
public interface HourMaxFloatingIpsOpenstackAggregatedMetricEvent extends TypedHourlyEvent {

    /**
     * Get the Ips related to this consumption
     * 
     * @return the list of IPs
     */
    public Iterable<String> getIps();

    /**
     * Get the counter of IPs
     * 
     * @return the number of IPs
     */
    public int getCounter();

}
