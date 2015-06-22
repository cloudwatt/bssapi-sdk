package com.cloudwatt.apis.bss.spec.domain.consumption;

import java.util.Date;

public interface HourlyEvent {

    public enum EventType {
        /**
         * Bytes outgoing from a VM (network)
         */
        HourComputeOutgoingBytesOpenstackAggregatedMetricEvent("vm-outgoing"), //$NON-NLS-1$
        /**
         * Snapshot size
         */
        HourSnapshotSizeOpenstackAggregatedMetricEvent("bl-snapshot"), //$NON-NLS-1$

        /**
         * Block Size (Cinder)
         */
        HourBlockSizeOpenstackAggregatedMetricEvent("bl-block-sz"), //$NON-NLS-1$
        /**
         * Object Size (Swift)
         */
        HourObjectSizeOpenstackAggregatedMetricEvent("sw-objectsz"), //$NON-NLS-1$
        /**
         * VM Instances (Nova)
         */
        HourInstanceOpenstackAggregatedMetricEvent("vm-instance"), //$NON-NLS-1$
        /**
         * Number of floating IPs allocated
         */
        HourMaxFloatingIpsOpenstackAggregatedMetricEvent("ip-floating"), //$NON-NLS-1$
        /**
         * Network Swift outgoing bytes
         */
        HourObjectOutgoingBytesOpenstackAggregatedMetricEvent("sw-outgoing"), //$NON-NLS-1$
        /**
         * Unknown kind of metrics
         */
        Unknown("xx-unknown-"); //$NON-NLS-1$

        public String shortName() {
            return sName;
        }

        @Override
        public String toString() {
            return shortName();
        }

        private String sName;

        private EventType(String sName) {
            this.sName = sName;
        }
    }

    /**
     * Get the UTC Year of event
     * 
     * @return the year, ie: 2015
     */
    public short getUtcYear();

    /**
     * Get the month of event
     * 
     * @return the month of event, 1 for January, 12 for december
     */
    public short getUtcMonth();

    /**
     * Get the day of month of event, 1 for the first day of month, 31 at the maximum
     * 
     * @return the day
     */
    public short getUtcDay();

    /**
     * Get the hour of the day, 0 for midnight, 23 for the last hour of the day
     * 
     * @return
     */
    public short getUtcHour();

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
