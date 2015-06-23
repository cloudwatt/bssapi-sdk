package com.cloudwatt.apis.bss.spec.domain.consumption.instances;

import com.cloudwatt.apis.bss.spec.domain.consumption.ResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.SizeInBytes;
import com.cloudwatt.apis.bss.spec.domain.consumption.TypedHourlyEvent;

public interface HourComputeOutgoingBytesOpenstackAggregatedMetricEvent extends TypedHourlyEvent, ResourceEvent,
        SizeInBytes {

}
