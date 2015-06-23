package com.cloudwatt.apis.bss.spec.domain.consumption;

import java.util.Date;
import com.cloudwatt.apis.bss.spec.domain.consumption.block.HourBlockSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.block.HourSnapshotSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourComputeOutgoingBytesOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourInstanceOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourMaxFloatingIpsOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.object.HourObjectOutgoingBytesOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.object.HourObjectSizeOpenstackAggregatedMetricEvent;

public interface HourlyEvent {

    public enum EventType {
        /**
         * Bytes outgoing from a VM (network)
         */
        HourComputeOutgoingBytesOpenstackAggregatedMetricEvent(
                HourComputeOutgoingBytesOpenstackAggregatedMetricEvent.class, "vm-outgoing"), //$NON-NLS-1$
        /**
         * Snapshot size
         */
        HourSnapshotSizeOpenstackAggregatedMetricEvent(HourSnapshotSizeOpenstackAggregatedMetricEvent.class,
                "bl-snapshot"), //$NON-NLS-1$

        /**
         * Block Size (Cinder)
         */
        HourBlockSizeOpenstackAggregatedMetricEvent(HourBlockSizeOpenstackAggregatedMetricEvent.class, "bl-block-sz"), //$NON-NLS-1$
        /**
         * Object Size (Swift)
         */
        HourObjectSizeOpenstackAggregatedMetricEvent(HourObjectSizeOpenstackAggregatedMetricEvent.class, "sw-objectsz"), //$NON-NLS-1$
        /**
         * VM Instances (Nova)
         */
        HourInstanceOpenstackAggregatedMetricEvent(HourInstanceOpenstackAggregatedMetricEvent.class, "vm-instance"), //$NON-NLS-1$
        /**
         * Number of floating IPs allocated
         */
        HourMaxFloatingIpsOpenstackAggregatedMetricEvent(HourMaxFloatingIpsOpenstackAggregatedMetricEvent.class,
                "ip-floating"), //$NON-NLS-1$
        /**
         * Network Swift outgoing bytes
         */
        HourObjectOutgoingBytesOpenstackAggregatedMetricEvent(
                HourObjectOutgoingBytesOpenstackAggregatedMetricEvent.class, "sw-outgoing"), //$NON-NLS-1$
        /**
         * Unknown kind of metrics
         */
        Unknown(TypedHourlyEvent.class, "xx-unknown-"); //$NON-NLS-1$

        public Class<? extends TypedHourlyEvent> getClazz() {
            return clazz;
        }

        public String shortName() {
            return sName;
        }

        @Override
        public String toString() {
            return shortName();
        }

        private String sName;

        private Class<? extends TypedHourlyEvent> clazz;

        private EventType(Class<? extends TypedHourlyEvent> clazz, String sName) {
            this.sName = sName;
            this.clazz = clazz;
        }
    }

    /**
     * Get the project id aka tenant id
     * 
     * @return the project id
     */
    public String getProjectId();

    /**
     * Get the compute date
     * 
     * @return the date in UTC
     */
    public Date getUtcComputeDate();

    /**
     * Get the event type
     * 
     * @return the Event type, if your version of SDK is too old, it may return {@link EventType#Unknown}
     */
    public EventType getEventType();
}
