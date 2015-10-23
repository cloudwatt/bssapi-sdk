package com.cloudwatt.apis.bss.impl.accountapi;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Payment;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.OpenstackRole;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.OpenstackUserWithRoles;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEventBase;
import com.cloudwatt.apis.bss.spec.domain.consumption.block.HourBlockSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.block.HourSnapshotSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourComputeOutgoingBytesOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourInstanceOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.instances.HourMaxFloatingIpsOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.object.HourObjectOutgoingBytesOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.object.HourObjectSizeOpenstackAggregatedMetricEvent;
import com.cloudwatt.apis.bss.spec.utils.CommonFormats;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public static class CollectionOfStrings {

        private final Iterable<String> data;

        @JsonCreator
        public CollectionOfStrings(Iterable<String> innerData) {
            this.data = innerData;
        }

        /**
         * Get the collection of String
         * 
         * @return the Strings
         */
        public Iterable<String> getData() {
            return data;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class HouryEventImpl implements HourlyEventBase, HourBlockSizeOpenstackAggregatedMetricEvent,
            HourComputeOutgoingBytesOpenstackAggregatedMetricEvent, HourSnapshotSizeOpenstackAggregatedMetricEvent,
            HourObjectSizeOpenstackAggregatedMetricEvent, HourInstanceOpenstackAggregatedMetricEvent,
            HourMaxFloatingIpsOpenstackAggregatedMetricEvent, HourObjectOutgoingBytesOpenstackAggregatedMetricEvent

    {

        @Override
        public EventType getEventType() {
            try {
                return EventType.valueOf(getType());
            } catch (IllegalArgumentException | NullPointerException err) {
                return EventType.Unknown;
            }
        }

        @JsonCreator
        @SuppressWarnings("nls")
        protected HouryEventImpl(Map<String, Object> fields) {
            this.fields = fields;
            this.projectId = getString("projectId");
            this.computeDate = new Date((Long) fields.remove("computeDate"));
            this.type = String.valueOf(fields.remove("type"));
            fields.remove("version");
            fields.remove("origin");
            fields.remove("tags");
            fields.remove("year");
            fields.remove("month");
            fields.remove("day");
            fields.remove("hour");
            fields.remove("checksum");
            fields.remove("guid");
            fields.remove("timestamp");
        }

        protected long getLong(String name) {
            return (Long) fields.get(name);
        }

        protected Date getDate(String name) {
            return new Date(getLong(name));
        }

        protected String getString(String name) {
            return (String) fields.get(name);
        }

        private Map<String, Object> fields;

        public Map<String, Object> getFields() {
            return fields;
        }

        public String getType() {
            return type;
        }

        @Override
        public String getProjectId() {
            return projectId;
        }

        @Override
        public Date getUtcComputeDate() {
            return computeDate;
        }

        private final String type, projectId;

        private final Date computeDate;

        protected StringBuilder createStringBuilderPrefix() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(CommonFormats.buildIso8601Format().format(getUtcComputeDate()))
              .append(' ')
              .append(String.valueOf(getEventType()))
              .append(' ');
            return sb;
        }

        @Override
        public String toString() {
            StringBuilder sb = createStringBuilderPrefix();
            sb.append(getFields());
            return sb.toString();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends HourlyEvent> Optional<T> castAs(Class<T> clazz) {
            if (!getEventType().getClazz().isAssignableFrom(clazz)) {
                return Optional.absent();
            }
            if (clazz.isAssignableFrom(getClass())) {
                return Optional.of((T) this);
            } else {
                return Optional.absent();
            }
        }

        @Override
        public UUID getResourceId() {
            return UUID.fromString(getString("resourceId")); //$NON-NLS-1$
        }

        @Override
        public long getSizeInBytes() {
            return getSize();
        }

        public long getSize() {
            return getLong("size"); //$NON-NLS-1$
        }

        @Override
        public UUID getVolumeType() {
            return UUID.fromString(getString("volumeType")); //$NON-NLS-1$
        }

        @Override
        public long getRequestsCounter() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Iterable<String> getIps() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getCounter() {
            return (int) getLong("counter"); //$NON-NLS-1$
        }

        @Override
        public UUID getInstanceId() {
            return getResourceId();
        }

        @Override
        public String getInstanceType() {
            return getString("instanceType"); //$NON-NLS-1$
        }

        @Override
        public Optional<String> getImageOrigin() {
            return Optional.of(getString("imageOrigin")); //$NON-NLS-1$
        }

        @Override
        public Optional<String> getImageOs() {
            return Optional.of(getString("imageOs"));//$NON-NLS-1$
        }

        @Override
        public long getDurationLifeTimeInMs() {
            return getLong("durationLifetime"); //$NON-NLS-1$

        }

        @Override
        public long getDurationDownTimeInMs() {
            return getLong("durationDowntime");//$NON-NLS-1$
        }

        @Override
        public long getUpTimeInMs() {
            return getLong("durationUptime");//$NON-NLS-1$
        }

        @Override
        public long getSleepTimeInMs() {
            return getLong("durationSleeptime");//$NON-NLS-1$
        }

        @Override
        public long getPauseTimeInMs() {
            return getLong("durationPausetime");//$NON-NLS-1$
        }

        @Override
        public long getRescueTimeInMs() {
            return getLong("durationRescuetime");//$NON-NLS-1$
        }

        @Override
        public long getShelveTimeInMs() {
            return getLong("durationShelvetime");//$NON-NLS-1$
        }

        @Override
        public String getImageId() {
            return getString("imageId"); //$NON-NLS-1$
        }

        @Override
        public String getImageName() {
            return getString("imageName"); //$NON-NLS-1$
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListOfEventsImpl {

        @JsonCreator
        public ListOfEventsImpl(@JsonProperty("events") Iterable<HouryEventImpl> events) {
            this.events = events;
        }

        private final Iterable<? extends HourlyEventBase> events;

        public Iterable<? extends HourlyEventBase> getEvents() {
            return events;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenstackRoleImpl implements OpenstackRole {

        private final String id;

        private final String name;

        @JsonCreator
        public OpenstackRoleImpl(@JsonProperty(value = "id", required = true) String id,
                @JsonProperty(value = "name", required = false) String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name == null ? id : name;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenstackUserWithRolesImpl implements OpenstackUserWithRoles {

        private final String name;

        private final String email;

        private final String id;

        private final Collection<OpenstackRole> roles;

        @JsonCreator
        public OpenstackUserWithRolesImpl(@JsonProperty(value = "name") String name,
                @JsonProperty(value = "email") String email, @JsonProperty(value = "id") String id,
                @JsonProperty(value = "roles") Collection<OpenstackRoleImpl> roles) {
            this.name = name;
            this.email = email;
            this.id = id;

            final ImmutableList.Builder<OpenstackRole> builder = ImmutableList.<OpenstackRole> builder();
            for (OpenstackRole r : roles) {
                builder.add(r);
            }
            this.roles = builder.build();
        }

        @Override
        public String getUserName() {
            return name;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getOpenstackId() {
            return id;
        }

        @Override
        public Collection<OpenstackRole> getRoles() {
            return roles;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CollectionOfOpenstackUserWithRolesImpl {

        private final Collection<OpenstackUserWithRoles> users;

        public Collection<OpenstackUserWithRoles> getUsers() {
            return users;
        }

        @JsonCreator
        public CollectionOfOpenstackUserWithRolesImpl(
                @JsonProperty(value = "users", required = true) List<OpenstackUserWithRolesImpl> users) {
            final ImmutableList.Builder<OpenstackUserWithRoles> builder = ImmutableList.<OpenstackUserWithRoles> builder();
            for (OpenstackUserWithRolesImpl r : users) {
                builder.add(r);
            }
            this.users = builder.build();
        }

    }

}
