package com.cloudwatt.apis.bss.spec.domain.consumption.block;

import java.util.UUID;

import com.cloudwatt.apis.bss.spec.domain.consumption.RegionalResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.ResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.SizeInBytes;
import com.cloudwatt.apis.bss.spec.domain.consumption.TypedHourlyEvent;

public interface HourBlockSizeOpenstackAggregatedMetricEvent extends TypedHourlyEvent, ResourceEvent, SizeInBytes, RegionalResourceEvent {

    public UUID getVolumeType();

}
