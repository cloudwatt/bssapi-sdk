package com.cloudwatt.apis.bss.spec.domain.consumption;

import com.google.common.base.Optional;

public interface HourlyEventBase extends HourlyEvent {

    /**
     * Cast the Event as given subclass
     * 
     * @param clazz interface to get
     * @return {@link Optional#absent()} if cast is not possible
     */
    <T extends HourlyEvent> Optional<T> castAs(Class<T> clazz);

}
