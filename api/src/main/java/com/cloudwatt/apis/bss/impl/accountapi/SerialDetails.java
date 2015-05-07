package com.cloudwatt.apis.bss.impl.accountapi;

import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
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

}
