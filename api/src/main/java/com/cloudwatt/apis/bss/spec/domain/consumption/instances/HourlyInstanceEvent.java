package com.cloudwatt.apis.bss.spec.domain.consumption.instances;

import java.util.UUID;
import com.cloudwatt.apis.bss.spec.domain.consumption.ResourceTypedEvent;
import com.google.common.base.Optional;

public interface HourlyInstanceEvent extends ResourceTypedEvent {

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
