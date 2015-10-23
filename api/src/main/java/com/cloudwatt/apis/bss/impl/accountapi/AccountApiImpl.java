package com.cloudwatt.apis.bss.impl.accountapi;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import com.cloudwatt.apis.bss.impl.ApiContext;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.CollectionOfOpenstackUserWithRolesImpl;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.CollectionOfOwnedTenants;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.CollectionOfRolesList;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.CollectionOfStrings;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.ListOfInvoicesImpl;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountRolesListApi;
import com.cloudwatt.apis.bss.spec.accountapi.ConsumptionApi;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.accountapi.OwnedTenantsListApi;
import com.cloudwatt.apis.bss.spec.accountapi.RolesEditApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRoles;
import com.cloudwatt.apis.bss.spec.domain.BSSCap.KNOWN_CAPS;
import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenantWithApi;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.OpenstackUserWithRoles;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.TenantRolesApi;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEventBase;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.cloudwatt.apis.bss.spec.utils.CommonFormats;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AccountApiImpl implements AccountApi {

    private final AccountWithRoles account;

    private final ApiContext context;

    private <T, T2 extends T> Optional<T> buildApi(KNOWN_CAPS capabilityRequired, T2 api) {
        if (account.getCaps().contains(capabilityRequired.name())) {
            return Optional.<T> of(api);
        } else {
            return Optional.<T> absent();
        }
    }

    @Override
    public Optional<AccountDetailApi> getAccountDetails() {
        return buildApi(KNOWN_CAPS.ACCOUNT_SHOW, new AccountDetailApi() {

            @Override
            public AccountDetails get() throws IOException, TooManyRequestsException {
                return context.getWebClient()
                              .doRequestAndRetrieveResultAsJSON(AccountDetailsImpl.class,
                                                                new HttpGet(context.buildPublicApiUrl(String.format("bss/1/accounts/%s", account.getCustomerId()), //$NON-NLS-1$
                                                                                                      Collections.<String, String> emptyMap())),
                                                                Optional.<TokenAccess> of(context.getTokenAccess()))
                              .get();
            }
        });
    }

    public AccountApiImpl(AccountWithRoles account, ApiContext context) {
        this.account = account;
        this.context = context;
    }

    @Override
    public Optional<AccountRolesListApi> getRolesListApi() {
        return buildApi(KNOWN_CAPS.ACCOUNT_ROLES_LIST, new AccountRolesListApi() {

            @Override
            public Iterable<IdentityToAccountRole> get() throws IOException, TooManyRequestsException {
                return context.getWebClient()
                              .doRequestAndRetrieveResultAsJSON(CollectionOfRolesList.class,
                                                                new HttpGet(context.buildPublicApiUrl(String.format("bss/1/accounts/%s/roles", account.getCustomerId()), //$NON-NLS-1$
                                                                                                      Collections.<String, String> emptyMap())),
                                                                Optional.<TokenAccess> of(context.getTokenAccess()))
                              .get()
                              .getRoles();
            }

            @Override
            public Optional<RolesEditApi> getEditRolesApi() {
                return buildApi(KNOWN_CAPS.ACCOUNT_ROLES_LIST, new RolesEditApi() {

                    @Override
                    public void removeRole(IdentityToAccountRole roleToRemove) throws IOException,
                            TooManyRequestsException {
                        if (roleToRemove == null) {
                            throw new IllegalArgumentException("RolesEditApi.removeRole(): roleToRemove cannot be null"); //$NON-NLS-1$
                        }
                        HttpDelete delete = new HttpDelete(context.buildPublicApiUrl(String.format("bss/1/accounts/%s/roles/%s/%s", account.getCustomerId(), roleToRemove.getUserName(), roleToRemove.getUsageType()), //$NON-NLS-1$
                                                                                     Collections.<String, String> emptyMap()));
                        Optional<JsonNode> ret = context.getWebClient()
                                                        .doRequestAndRetrieveResultAsJSON(JsonNode.class,
                                                                                          delete,
                                                                                          Optional.<TokenAccess> of(context.getTokenAccess()));
                        assert (ret != null);
                    }

                    @Override
                    public void addRoleToIdentity(Identity identity, String roleToAdd) throws IOException,
                            TooManyRequestsException, NoSuchRoleException {
                        if (identity == null) {
                            throw new IllegalArgumentException("RolesEditApi.addRoleToIdentity(): identity MUST NOT BE NULL"); //$NON-NLS-1$
                        }
                        if (roleToAdd == null || roleToAdd.isEmpty()) {
                            throw new IllegalArgumentException("RolesEditApi.addRoleToIdentity(): roleToAdd MUST NOT BE NULL OR Empty"); //$NON-NLS-1$
                        }
                        // https://bssapi.fr1.cloudwatt.com/bss/accounts/1/0750102462/addRoleToIdentity
                        HttpPost post = new HttpPost(context.buildPublicApiUrl(String.format("bss/accounts/1/%s/addRoleToIdentity", account.getCustomerId()), //$NON-NLS-1$
                                                                               Collections.<String, String> emptyMap()));
                        ObjectMapper mapper = new ObjectMapper();
                        String data = mapper.writeValueAsString(ImmutableMap.<String, String> of("usage_type", roleToAdd, "user_id", identity.getName())); //$NON-NLS-1$//$NON-NLS-2$
                        post.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));
                        Optional<JsonNode> ret = context.getWebClient()
                                                        .doRequestAndRetrieveResultAsJSON(JsonNode.class,
                                                                                          post,
                                                                                          Optional.<TokenAccess> of(context.getTokenAccess()));
                        assert (ret != null);
                    }

                    @Override
                    public Iterable<String> listAllowedRolesForAccount() throws IOException, TooManyRequestsException {
                        return context.getWebClient()
                                      .doRequestAndRetrieveResultAsJSON(CollectionOfStrings.class,
                                                                        new HttpGet(context.buildPublicApiUrl(String.format("bss/1/accounts/%s/allowedroles", account.getCustomerId()), //$NON-NLS-1$
                                                                                                              Collections.<String, String> emptyMap())),
                                                                        Optional.<TokenAccess> of(context.getTokenAccess()))
                                      .get()
                                      .getData();
                    }
                });
            }
        });
    }

    @Override
    public Optional<OwnedTenantsListApi> getOwnedTenantsApi() {
        return buildApi(KNOWN_CAPS.TENANTS_LIST, new OwnedTenantsListApi() {

            private Iterable<OwnedTenantWithApi> toApi(Iterable<OwnedTenant> tenants) {
                ImmutableList.Builder<OwnedTenantWithApi> builder = new ImmutableList.Builder<OwnedTenantWithApi>();
                for (final OwnedTenant t : tenants) {
                    builder.add(new OwnedTenantWithApi() {

                        @Override
                        public String getTenantType() {
                            return t.getTenantType();
                        }

                        @Override
                        public String getTenantId() {
                            return t.getTenantId();
                        }

                        @Override
                        public String getCustomerId() {
                            return t.getCustomerId();
                        }

                        @Override
                        public Date getCreationTime() {
                            return t.getCreationTime();
                        }

                        @Override
                        public Optional<ConsumptionApi> getConsumptionApi() {
                            return AccountApiImpl.this.getConsumptionApi(t);
                        }

                        @Override
                        public Optional<TenantRolesApi> getOpenstackRolesApi() {
                            return buildApi(KNOWN_CAPS.TENANT_SHOW, new TenantRolesApi() {

                                @Override
                                public Iterable<OpenstackUserWithRoles> getUsers() throws IOException,
                                        TooManyRequestsException {
                                    return context.getWebClient()
                                                  .doRequestAndRetrieveResultAsJSON(CollectionOfOpenstackUserWithRolesImpl.class,
                                                                                    new HttpGet(context.buildPublicApiUrl(String.format("bss/1/tenants/%s/users", t.getTenantId()), //$NON-NLS-1$
                                                                                                                          Collections.<String, String> emptyMap())),
                                                                                    Optional.<TokenAccess> of(context.getTokenAccess()))
                                                  .get()
                                                  .getUsers();
                                }
                            });
                        }
                    });
                }
                return builder.build();
            }

            @Override
            public Iterable<OwnedTenantWithApi> get() throws IOException, TooManyRequestsException {
                return toApi(context.getWebClient()
                                    .doRequestAndRetrieveResultAsJSON(CollectionOfOwnedTenants.class,
                                                                      new HttpGet(context.buildPublicApiUrl(String.format("bss/1/accounts/%s/tenants", account.getCustomerId()), //$NON-NLS-1$
                                                                                                            Collections.<String, String> emptyMap())),
                                                                      Optional.<TokenAccess> of(context.getTokenAccess()))
                                    .get()
                                    .getTenants());
            }
        });
    }

    @Override
    public Optional<AccountInvoicesApi> getInvoicesApi() {
        return buildApi(KNOWN_CAPS.BILLING_INVOICES, new AccountInvoicesApi() {

            private InvoicesQueryBuilder builder = new InvoicesQueryBuilder() {

                private InvoiceExtension extensions[] = new InvoiceExtension[] { InvoiceExtension.pdf };

                public String getExtensionsAsString() {
                    String s = ""; //$NON-NLS-1$
                    for (InvoiceExtension ext : extensions) {
                        if (s.isEmpty()) {
                            s = ext.name();
                        } else {
                            s += "," + ext.name(); //$NON-NLS-1$
                        }
                    }
                    return s;
                }

                @Override
                public InvoicesQueryBuilder setExtensions(InvoiceExtension... extensions) {
                    this.extensions = extensions == null ? new InvoiceExtension[0] : extensions;
                    return this;
                }

                @Override
                public Iterable<Invoice> get() throws IOException, TooManyRequestsException {
                    return context.getWebClient()
                                  .doRequestAndRetrieveResultAsJSON(ListOfInvoicesImpl.class,
                                                                    new HttpGet(context.buildPublicApiUrl(String.format("bss/1/accounts/%s/listInvoices", account.getCustomerId()), //$NON-NLS-1$
                                                                                                          ImmutableMap.<String, String> of("extensions", //$NON-NLS-1$
                                                                                                                                           getExtensionsAsString()))),
                                                                    Optional.<TokenAccess> of(context.getTokenAccess()))
                                  .get()
                                  .getInvoices();
                }
            };

            @Override
            public InvoicesQueryBuilder get() {
                return builder;
            }
        });
    }

    @Override
    public Optional<ConsumptionApi> getConsumptionApi(final OwnedTenant tenant) {
        return buildApi(KNOWN_CAPS.CONSUMPTION, new ConsumptionApi() {

            private final ConsumptionApiBuilder builder = new ConsumptionApiBuilder() {

                private int start = 0;

                private int limit = 744; // Default to all hours in a month of 31 days

                private Date fromDate;
                {
                    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    fromDate = c.getTime();
                }

                @Override
                public Iterable<? extends HourlyEventBase> get() throws IOException, TooManyRequestsException {
                    return context.getWebClient()
                                  .doRequestAndRetrieveResultAsJSON(SerialDetails.ListOfEventsImpl.class,
                                                                    new HttpGet(context.buildPublicApiUrl(String.format("bss/accounts/1/rawConsumption/tenant/%s", tenant.getTenantId()), //$NON-NLS-1$
                                                                                                          ImmutableMap.<String, String> of("fromDate", //$NON-NLS-1$
                                                                                                                                           CommonFormats.buildIso8601Format()
                                                                                                                                                        .format(fromDate),
                                                                                                                                           "page", //$NON-NLS-1$
                                                                                                                                           String.valueOf(start),
                                                                                                                                           "limit", //$NON-NLS-1$
                                                                                                                                           String.valueOf(limit)))),
                                                                    Optional.<TokenAccess> of(context.getTokenAccess()))
                                  .get()
                                  .getEvents();
                }
            };

            @Override
            public ConsumptionApiBuilder get() {
                return builder;
            }
        });
    }
}
