package com.cloudwatt.apis.bss.impl.accountapi;

import java.io.IOException;
import java.util.Collections;
import org.apache.http.client.methods.HttpGet;
import com.cloudwatt.apis.bss.impl.ApiContext;
import com.cloudwatt.apis.bss.impl.TokenResult.TokenAccess;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.CollectionOfOwnedTenants;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.CollectionOfRolesList;
import com.cloudwatt.apis.bss.impl.accountapi.SerialDetails.ListOfInvoicesImpl;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi.InvoiceExtension;
import com.cloudwatt.apis.bss.spec.accountapi.AccountRolesListApi;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.accountapi.OwnedTenantsListApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRoles;
import com.cloudwatt.apis.bss.spec.domain.BSSCap.KNOWN_CAPS;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;
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
        });
    }

    @Override
    public Optional<OwnedTenantsListApi> getOwnedTenantsApi() {
        return buildApi(KNOWN_CAPS.TENANTS_LIST, new OwnedTenantsListApi() {

            @Override
            public Iterable<OwnedTenant> get() throws IOException, TooManyRequestsException {
                return context.getWebClient()
                              .doRequestAndRetrieveResultAsJSON(CollectionOfOwnedTenants.class,
                                                                new HttpGet(context.buildPublicApiUrl(String.format("bss/1/accounts/%s/tenants", account.getCustomerId()), //$NON-NLS-1$
                                                                                                      Collections.<String, String> emptyMap())),
                                                                Optional.<TokenAccess> of(context.getTokenAccess()))
                              .get()
                              .getTenants();
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
}
