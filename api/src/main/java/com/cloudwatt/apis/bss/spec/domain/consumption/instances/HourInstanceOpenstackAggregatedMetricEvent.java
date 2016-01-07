package com.cloudwatt.apis.bss.spec.domain.consumption.instances;

import java.util.UUID;

import com.cloudwatt.apis.bss.spec.domain.consumption.RegionalResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.ResourceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.TypedHourlyEvent;
import com.google.common.base.Optional;

public interface HourInstanceOpenstackAggregatedMetricEvent extends TypedHourlyEvent, ResourceEvent, RegionalResourceEvent {

    public UUID getInstanceId();

    public String getInstanceType();

    public Optional<String> getImageOrigin();

    public Optional<String> getImageOs();

    public long getDurationLifeTimeInMs();

    public long getDurationDownTimeInMs();

    public long getUpTimeInMs();

    public long getSleepTimeInMs();

    public long getPauseTimeInMs();

    public long getRescueTimeInMs();

    public long getShelveTimeInMs();

    public String getImageId();

    public String getImageName();

}
