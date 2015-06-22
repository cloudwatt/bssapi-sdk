package com.cloudwatt.apis.bss.impl.accountapi;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Payment;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.block.HourSnapshotSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.block.HourlyBlockSizeEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourComputeOutgoingBytesOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourlyInstanceEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourlyMaxFloatingIpsEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.object.HourObjectSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.object.HourlySwiftOutgoingBytes;
import com.cloudwatt.apis.bss.spec.utils.CommonFormats;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

class SerialDetails {

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class IdentityToAccountRoleImpl implements IdentityToAccountRole {

        private final String customer_id;

        private final String usage_type;

        private final String user_id;

        private final String user_email;

        @Override
        public String getCustomerId() {
            return customer_id;
        }

        @Override
        public String getUsageType() {
            return usage_type;
        }

        @Override
        public String getUserName() {
            return user_id;
        }

        @Override
        public String getUserEmail() {
            return user_email;
        }

        @JsonCreator
        public IdentityToAccountRoleImpl(@JsonProperty(value = "customer_id", required = true) String customer_id,
                @JsonProperty(value = "usage_type", required = true) String usage_type,
                @JsonProperty(value = "user_id", required = true) String user_id,
                @JsonProperty(value = "user_email", required = false) String user_email) {
            super();
            this.customer_id = customer_id;
            this.usage_type = usage_type;
            this.user_id = user_id;
            this.user_email = user_email;
        }

    }

    public static class CollectionOfRolesList {

        @JsonCreator
        public CollectionOfRolesList(@JsonUnwrapped Iterable<IdentityToAccountRoleImpl> roles) {
            super();
            ImmutableList.Builder<IdentityToAccountRole> rolesBuilder = new ImmutableList.Builder<IdentityToAccountRole>();
            for (IdentityToAccountRoleImpl r : roles) {
                rolesBuilder.add(r);
            }
            this.roles = rolesBuilder.build();
        }

        private final Iterable<IdentityToAccountRole> roles;

        public Iterable<IdentityToAccountRole> getRoles() {
            return roles;
        }
    }

    private final static SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", //$NON-NLS-1$
                                                                                 Locale.ENGLISH);
    static {
        ISO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
    }

    private static final Date parseIsoDate(String date) {
        try {
            return ((DateFormat) (ISO_DATE_FORMAT.clone())).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OwnedTenantImpl implements OwnedTenant {

        private final Date date_entered;

        private final String customerId;

        private final String type;

        private final String tenantId;

        @Override
        public Date getCreationTime() {
            return date_entered;
        }

        @Override
        public String getCustomerId() {
            return customerId;
        }

        @Override
        public String getTenantType() {
            return type;
        }

        @Override
        public String getTenantId() {
            return tenantId;
        }

        @JsonCreator
        public OwnedTenantImpl(@JsonProperty(value = "date_entered", required = true) String date_entered,
                @JsonProperty(value = "customerId", required = true) String customerId,
                @JsonProperty(value = "type", required = false) String type,
                @JsonProperty(value = "tenantId", required = true) String tenantId) {
            super();
            this.date_entered = parseIsoDate(date_entered);
            this.customerId = customerId;
            this.type = type;
            this.tenantId = tenantId;
        }
    }

    public static class CollectionOfOwnedTenants {

        @JsonCreator
        public CollectionOfOwnedTenants(@JsonUnwrapped Iterable<OwnedTenantImpl> tenants) {
            super();
            ImmutableList.Builder<OwnedTenant> rolesBuilder = new ImmutableList.Builder<OwnedTenant>();
            for (OwnedTenantImpl r : tenants) {
                rolesBuilder.add(r);
            }
            this.tenants = rolesBuilder.build();
        }

        private final Iterable<OwnedTenant> tenants;

        public Iterable<OwnedTenant> getTenants() {
            return tenants;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentImpl implements Payment {

        private final int id;

        private final double amount;

        private final Date create_date;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public double getAmountInEuros() {
            return amount;
        }

        @Override
        public Date getCreateDate() {
            return create_date;
        }

        @JsonCreator
        public PaymentImpl(@JsonProperty(value = "id", required = false) int id,
                @JsonProperty(value = "amount", required = false) double amount,
                @JsonProperty(value = "create_date", required = false) Date create_date) {
            super();
            this.id = id;
            this.amount = amount;
            this.create_date = create_date;
        }

    }

    public static class InvoiceImpl implements Invoice {

        @JsonCreator
        public InvoiceImpl(@JsonProperty(value = "id", required = true) int id,
                @JsonProperty(value = "createDate", required = false) Date createDate,
                @JsonProperty(value = "dueDate", required = false) Date dueDate,
                @JsonProperty(value = "total", required = true) double total,
                @JsonProperty(value = "balance", required = false) double balance,
                @JsonProperty(value = "invoicesURI") Map<String, URI> invoicesURI,
                @JsonProperty(value = "payments") Iterable<PaymentImpl> payments) {
            super();
            this.id = id;
            this.create_date = createDate;
            this.due_date = dueDate;
            this.total = total;
            this.balance = balance;
            this.invoicesURI = ImmutableMap.<String, URI> copyOf(invoicesURI);
            {
                ImmutableList.Builder<Payment> builder = new ImmutableList.Builder<Payment>();
                for (PaymentImpl e : payments) {
                    builder.add(e);
                }
                this.payments = builder.build();
            }
        }

        private final int id;

        private final Date create_date;

        private final Date due_date;

        private final double total;

        private final double balance;

        private final Map<String, URI> invoicesURI;

        private final Iterable<Payment> payments;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public Date getCreateDate() {
            return create_date;
        }

        @Override
        public Date getDueDate() {
            return due_date;
        }

        @Override
        public double getTotalInEuros() {
            return total;
        }

        @Override
        public double getBalance() {
            return balance;
        }

        @Override
        public Map<String, URI> getInvoicesURI() {
            return invoicesURI;
        }

        @Override
        public Iterable<Payment> getPayments() {
            return payments;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListOfInvoicesImpl {

        @JsonCreator
        public ListOfInvoicesImpl(@JsonProperty("invoices") Iterable<InvoiceImpl> invoices) {
            super();
            ImmutableList.Builder<Invoice> builder = new ImmutableList.Builder<Invoice>();
            for (InvoiceImpl e : invoices) {
                builder.add(e);
            }
            this.invoices = builder.build();
        }

        public Iterable<Invoice> getInvoices() {
            return invoices;
        }

        private final Iterable<Invoice> invoices;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = HouryEventDefaultImpl.class)
    @JsonSubTypes({
                   @Type(value = HourlyEventInstanceImpl.class, name = "HourInstanceOpenstackAggregatedMetricEvent"),
                   @Type(value = HourlyMaxFloatingIpImpl.class, name = "HourMaxFloatingIpsOpenstackAggregatedMetricEvent"),
                   @Type(value = HourlyBlockSizeImpl.class, name = "HourBlockSizeOpenstackAggregatedMetricEvent"),
                   @Type(value = HourlySwiftSizeImpl.class, name = "HourObjectSizeOpenstackAggregatedMetricEvent"),
                   @Type(value = HourSnapshotSizeOpenstackAggregatedMetricEventImpl.class, name = "HourSnapshotSizeOpenstackAggregatedMetricEvent"),
                   @Type(value = HourComputeOutgoingBytesOpenstackAggregatedMetricEventImpl.class, name = "HourComputeOutgoingBytesOpenstackAggregatedMetricEvent"),
                   @Type(value = HourlySwiftOutgoingBytesImpl.class, name = "HourObjectOutgoingBytesOpenstackAggregatedMetricEvent") })
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class HouryEventImpl implements HourlyEvent {

        @Override
        public EventType getEventType() {
            return null;
        }

        @JsonCreator
        protected HouryEventImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId) {

            this.type = type;
            this.computeDate = computeDate;
            this.utcYear = utcYear;
            this.utcMonth = utcMonth;
            this.utcDay = utcDay;
            this.utcHour = utcHour;
            this.projectId = projectId;
        }

        public String getType() {
            return type;
        }

        @Override
        public short getUtcYear() {
            return utcYear;
        }

        @Override
        public short getUtcMonth() {
            return utcMonth;
        }

        @Override
        public short getUtcDay() {
            return utcDay;
        }

        @Override
        public short getUtcHour() {
            return utcHour;
        }

        @Override
        public String getProjectId() {
            return projectId;
        }

        @Override
        public Date getUtcComputeDate() {
            return computeDate;
        }

        protected StringBuilder createStringBuilderPrefix() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(CommonFormats.buildIso8601Format().format(getUtcComputeDate()))
              .append(' ')
              .append(String.valueOf(getEventType()))
              .append(' ');
            return sb;
        }

        private final String type;

        private final Date computeDate;

        private final short utcYear;

        private final short utcMonth;

        private final short utcDay;

        private final short utcHour;

        private final String projectId;

    }

    static class HouryEventDefaultImpl extends HouryEventImpl {

        @JsonCreator
        public HouryEventDefaultImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().toString();
        }

        @Override
        public EventType getEventType() {
            return EventType.Unknown;
        }
    }

    static class HourlyMaxFloatingIpImpl extends HouryEventImpl implements HourlyMaxFloatingIpsEvent {

        @JsonCreator
        public HourlyMaxFloatingIpImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "ips", required = true) Iterable<String> ips,
                @JsonProperty(value = "counter", required = true) int counter) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.ips = ips;
            this.counter = counter;
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().append(String.valueOf(getIps())).toString();
        }

        private final Iterable<String> ips;

        private final int counter;

        @Override
        public Iterable<String> getIps() {
            return ips;
        }

        @Override
        public int getCounter() {
            return counter;
        }

        @Override
        public EventType getEventType() {
            return EventType.HourMaxFloatingIpsOpenstackAggregatedMetricEvent;
        }
    }

    static class HourlySwiftOutgoingBytesImpl extends HouryEventImpl implements HourlySwiftOutgoingBytes {

        @JsonCreator
        public HourlySwiftOutgoingBytesImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "size", required = true) long size,
                @JsonProperty(value = "counter", required = true) long counter) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.size = size;
            this.counter = counter;
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().append(getRequestsCounter())
                                              .append(' ')
                                              .append(getSizeInBytes())
                                              .toString();
        }

        private final long counter;

        private final long size;

        @Override
        public long getRequestsCounter() {
            return counter;
        }

        @Override
        public long getSizeInBytes() {
            return size;
        }

        @Override
        public EventType getEventType() {
            return EventType.HourObjectOutgoingBytesOpenstackAggregatedMetricEvent;
        }

    }

    static class HourComputeOutgoingBytesOpenstackAggregatedMetricEventImpl extends HouryEventImpl implements
            HourComputeOutgoingBytesOpenstackAggregatedMetricEvent {

        @JsonCreator
        public HourComputeOutgoingBytesOpenstackAggregatedMetricEventImpl(
                @JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "cumulativeSize", required = true) long cumulativeSizeInBytes,
                @JsonProperty(value = "resourceId", required = true) UUID resourceId) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.cumulativeSizeInBytes = cumulativeSizeInBytes;
            this.resourceId = resourceId;
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().append(getResourceId()).append(' ').append(getSizeInBytes()).toString();
        }

        private final long cumulativeSizeInBytes;

        @Override
        public UUID getResourceId() {
            return resourceId;
        }

        private final UUID resourceId;

        @Override
        public EventType getEventType() {
            return EventType.HourComputeOutgoingBytesOpenstackAggregatedMetricEvent;
        }

        @Override
        public long getSizeInBytes() {
            return cumulativeSizeInBytes;
        }

    }

    static class HourlyBlockSizeImpl extends HouryEventImpl implements HourlyBlockSizeEvent {

        @JsonCreator
        public HourlyBlockSizeImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "size", required = true) long size,
                @JsonProperty(value = "resourceId", required = true) UUID resourceId,
                @JsonProperty(value = "volumeId", required = true) UUID volumeType) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.size = size;
            this.resourceId = resourceId;
            this.volumeType = volumeType;
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().append(getResourceId()).append(' ').append(getSizeInBytes()).toString();
        }

        private final long size;

        @Override
        public UUID getVolumeType() {
            return volumeType;
        }

        @Override
        public UUID getResourceId() {
            return resourceId;
        }

        @Override
        public long getSizeInBytes() {
            return size;
        }

        private final UUID volumeType;

        private final UUID resourceId;

        @Override
        public EventType getEventType() {
            return EventType.HourBlockSizeOpenstackAggregatedMetricEvent;
        }

    }

    static class HourSnapshotSizeOpenstackAggregatedMetricEventImpl extends HouryEventImpl implements
            HourSnapshotSizeOpenstackAggregatedMetricEvent {

        @JsonCreator
        public HourSnapshotSizeOpenstackAggregatedMetricEventImpl(
                @JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "size", required = true) long size,
                @JsonProperty(value = "resourceId", required = true) UUID resourceId) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.size = size;
            this.resourceId = resourceId;
        }

        private final UUID resourceId;

        @Override
        public UUID getResourceId() {
            return resourceId;
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().append(getResourceId()).append(' ').append(getSizeInBytes()).toString();
        }

        private final long size;

        @Override
        public long getSizeInBytes() {
            return size;
        }

        @Override
        public EventType getEventType() {
            return EventType.HourSnapshotSizeOpenstackAggregatedMetricEvent;
        }

    }

    static class HourlySwiftSizeImpl extends HouryEventImpl implements HourObjectSizeOpenstackAggregatedMetricEvent {

        @JsonCreator
        public HourlySwiftSizeImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "size", required = true) long size) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.size = size;
        }

        @Override
        public String toString() {
            return createStringBuilderPrefix().append(getSizeInBytes()).toString();
        }

        private final long size;

        @Override
        public long getSizeInBytes() {
            return size;
        }

        @Override
        public EventType getEventType() {
            return EventType.HourObjectSizeOpenstackAggregatedMetricEvent;
        }

    }

    static class HourlyEventInstanceImpl extends HouryEventImpl implements HourlyInstanceEvent {

        @JsonCreator
        public HourlyEventInstanceImpl(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "computeDate", required = true) Date computeDate,
                @JsonProperty(value = "year", required = true) short utcYear,
                @JsonProperty(value = "month", required = true) short utcMonth,
                @JsonProperty(value = "day", required = true) short utcDay,
                @JsonProperty(value = "hour", required = true) short utcHour,
                @JsonProperty(value = "projectId", required = true) String projectId,
                @JsonProperty(value = "instanceType", required = true) String instanceType,
                @JsonProperty(value = "imageOrigin", required = false) String imageOrigin,
                @JsonProperty(value = "imageOs", required = false) String imageOs,
                @JsonProperty(value = "imageName", required = false) String imageName,
                @JsonProperty(value = "durationLifetime", required = true) long durationLifetime,
                @JsonProperty(value = "durationDowntime", required = true) long durationDowntime,
                @JsonProperty(value = "durationUptime", required = true) long durationUptime,
                @JsonProperty(value = "durationSleeptime", required = true) long durationSleeptime,
                @JsonProperty(value = "durationPausetime", required = true) long durationPausetime,
                @JsonProperty(value = "durationRescuetime", required = true) long durationRescuetime,
                @JsonProperty(value = "durationShelvetime", required = true) long durationShelvetime,
                @JsonProperty(value = "imageId", required = false) String imageId,
                @JsonProperty(value = "instanceId", required = true) UUID instanceId) {
            super(type, computeDate, utcYear, utcMonth, utcDay, utcHour, projectId);
            this.instanceType = instanceType;
            this.imageOrigin = Optional.fromNullable(imageOrigin);
            this.imageOs = Optional.fromNullable(imageOs);
            this.imageName = imageName;
            this.durationLifetime = durationLifetime;
            this.durationDowntime = durationDowntime;
            this.durationUptime = durationUptime;
            this.durationSleeptime = durationSleeptime;
            this.durationPausetime = durationPausetime;
            this.durationRescuetime = durationRescuetime;
            this.durationShelvetime = durationShelvetime;
            this.imageId = imageId;
            this.instanceId = instanceId;
        }

        private final UUID instanceId;

        @Override
        public UUID getInstanceId() {
            return instanceId;
        }

        /**
         * Same as {@link #getInstanceId()}
         */
        @Override
        public UUID getResourceId() {
            return instanceId;
        }

        @Override
        public String toString() {
            StringBuilder sb = createStringBuilderPrefix();
            sb.append(getInstanceId())
              .append(' ')
              .append(getDurationLifeTimeInMs())
              .append(' ')
              .append(getInstanceType());
            return sb.toString();
        }

        @Override
        public String getInstanceType() {
            return instanceType;
        }

        @Override
        public Optional<String> getImageOrigin() {
            return imageOrigin;
        }

        @Override
        public Optional<String> getImageOs() {
            return imageOs;
        }

        @Override
        public long getDurationLifeTimeInMs() {
            return durationLifetime;
        }

        @Override
        public long getDurationDownTimeInMs() {
            return durationDowntime;
        }

        @Override
        public long getUpTimeInMs() {
            return durationUptime;
        }

        @Override
        public long getSleepTimeInMs() {
            return durationSleeptime;
        }

        @Override
        public long getPauseTimeInMs() {
            return durationPausetime;
        }

        @Override
        public long getRescueTimeInMs() {
            return durationRescuetime;
        }

        @Override
        public long getShelveTimeInMs() {
            return durationShelvetime;
        }

        @Override
        public String getImageId() {
            return imageId;
        }

        @Override
        public String getImageName() {
            return imageName;
        }

        private final String instanceType;

        private final Optional<String> imageOrigin;

        private final Optional<String> imageOs;

        private final String imageName;

        private final long durationLifetime;

        private final long durationDowntime;

        private final long durationUptime;

        private final long durationSleeptime;

        private final long durationPausetime;

        private final long durationRescuetime;

        private final long durationShelvetime;

        private final String imageId;

        @Override
        public EventType getEventType() {
            return EventType.HourInstanceOpenstackAggregatedMetricEvent;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListOfEventsImpl {

        @JsonCreator
        public ListOfEventsImpl(@JsonProperty("events") Iterable<? extends HouryEventImpl> events) {
            this.events = events;
        }

        private final Iterable<? extends HourlyEvent> events;

        public Iterable<? extends HourlyEvent> getEvents() {
            return events;
        }
    }

}
