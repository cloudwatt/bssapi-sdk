package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEventBase;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

public interface ConsumptionApi {

    /**
     * The Builder you can tune
     * 
     * @author Pierre Souchay
     *
     */
    public static interface ConsumptionApiBuilder {

        /**
         * Get the consumption
         * 
         * @return all the nodes of Consumption
         */
        public Iterable<? extends HourlyEventBase> get() throws IOException, TooManyRequestsException;

    }

    /**
     * Get a builder
     * 
     * @return the Builder
     */
    public ConsumptionApiBuilder get();
}
