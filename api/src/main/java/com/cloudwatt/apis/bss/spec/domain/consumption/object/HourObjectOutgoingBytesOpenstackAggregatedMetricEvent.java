package com.cloudwatt.apis.bss.spec.domain.consumption.object;

import com.cloudwatt.apis.bss.spec.domain.consumption.SizeInBytes;
import com.cloudwatt.apis.bss.spec.domain.consumption.TypedHourlyEvent;

/**
 * The size of Swift for a given hour
 * 
 * @author pierre souchay
 *
 */
public interface HourObjectOutgoingBytesOpenstackAggregatedMetricEvent extends TypedHourlyEvent, SizeInBytes {

    /**
     * Get the number of requests
     * 
     * @return the number of request
     */
    public long getRequestsCounter();
}
