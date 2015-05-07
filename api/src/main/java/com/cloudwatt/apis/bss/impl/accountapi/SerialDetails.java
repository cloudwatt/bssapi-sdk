package com.cloudwatt.apis.bss.impl.accountapi;

import java.util.Date;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableList;

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

}
