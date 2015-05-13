package com.cloudwatt.apis.bss.impl.accountapi;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Payment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
        public OwnedTenantImpl(@JsonProperty(value = "date_entered", required = true) Date date_entered,
                @JsonProperty(value = "customerId", required = true) String customerId,
                @JsonProperty(value = "type", required = false) String type,
                @JsonProperty(value = "tenantId", required = true) String tenantId) {
            super();
            this.date_entered = date_entered;
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
        public InvoiceImpl(@JsonProperty(value = "id", required = false) int id,
                @JsonProperty(value = "create_date", required = false) Date create_date,
                @JsonProperty(value = "due_date", required = false) Date due_date,
                @JsonProperty(value = "total", required = false) double total,
                @JsonProperty(value = "balance", required = false) double balance,
                @JsonProperty(value = "invoicesURI") Map<String, URI> invoicesURI,
                @JsonProperty(value = "payments") Iterable<PaymentImpl> payments) {
            super();
            this.id = id;
            this.create_date = create_date;
            this.due_date = due_date;
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

}
