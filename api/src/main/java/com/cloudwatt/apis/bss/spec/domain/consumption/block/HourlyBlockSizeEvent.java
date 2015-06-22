package com.cloudwatt.apis.bss.spec.domain.consumption.block;

import java.util.UUID;
import com.cloudwatt.apis.bss.spec.domain.consumption.ResourceTypedEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.SizeInBytes;

public interface HourlyBlockSizeEvent extends ResourceTypedEvent, SizeInBytes {

    public UUID getVolumeType();

}
