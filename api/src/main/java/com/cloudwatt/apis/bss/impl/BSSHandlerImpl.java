package com.cloudwatt.apis.bss.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.http.client.methods.HttpGet;
import com.cloudwatt.apis.bss.impl.accountapi.AccountApiImpl;
import com.cloudwatt.apis.bss.impl.commonapi.CommonApiImpl;
import com.cloudwatt.apis.bss.impl.contactapi.AccountRoles;
import com.cloudwatt.apis.bss.impl.contactapi.ContactInformationWithRoles;
import com.cloudwatt.apis.bss.impl.domain.keystone.TenantsResult;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRoles;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Implementation class
 * 
 * @author pierre souchay
 *
 */
public class BSSHandlerImpl implements BSSApiHandle {

    private class AccountApiWrapper implements AccountWithRolesWithOperations {

        @Override
        public Set<String> getNamedRoles() {
            return account.getNamedRoles();
        }

        @Override
        public Set<String> getCaps() {
            return account.getCaps();
        }

        @Override
        public String getCustomerId() {
            return account.getCustomerId();
        }

        @Override
        public AccountApi getApi() {
            return new AccountApiImpl(account, context);
        }

        public AccountApiWrapper(AccountWithRoles account) {
            super();
            this.account = account;
        }

        private final AccountWithRoles account;

    }

    /**
     * Package protected Constructor
     * 
     * @param email
     * @param password
     * @param id
     */
    public BSSHandlerImpl(ApiContext context) throws IOException, TooManyRequestsException {
        this.context = context;
        this.identity = new CWIdentity(context.getTokenAccess().getUser().getId(),
                                       context.getTokenAccess().getUser().getName(),
                                       context.getTokenAccess().getUser().getEmail());
        Iterable<AccountRoles> ret = context.getWebClient()
                                            .doRequestAndRetrieveResultAsJSON(ContactInformationWithRoles.class,
                                                                              new HttpGet(context.buildPublicApiUrl("bss/1/contact/roles", //$NON-NLS-1$
                                                                                                                    Collections.<String, String> emptyMap())),
                                                                              Optional.of(context.getTokenAccess()))
                                            .get()
                                            .getAccounts();
        ImmutableSet.Builder<AccountWithRolesWithOperations> builder = new ImmutableSet.Builder<AccountWithRolesWithOperations>();
        for (AccountRoles a : ret) {
            builder.add(new AccountApiWrapper(a));
        }
        this.accounts = builder.build();
    }

    private Iterable<AccountWithRolesWithOperations> accounts;

    private final ApiContext context;

    private final CWIdentity identity;

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public Iterable<AccountWithRolesWithOperations> getAccounts() {
        return accounts;
    }

    @Override
    public CommonApi getCommmonApi() {
        return new CommonApiImpl(context);
    }

    @Override
    public Optional<AccountApi> getAccountApi(String customerId) {
        for (AccountWithRoles r : getAccounts()) {
            if (customerId.equals(r.getCustomerId()))
                return Optional.<AccountApi> of(new AccountApiImpl(r, context));
        }
        return Optional.<AccountApi> absent();
    }

    @Override
    public Iterable<TenantIFace> getTenantsList() throws IOException, TooManyRequestsException {
        final String url = context.buildKeystoneUrl("tenants"); //$NON-NLS-1$
        Optional<TenantsResult> res = context.getWebClient()
                                             .doRequestAndRetrieveResultAsJSON(TenantsResult.class,
                                                                               new HttpGet(url),
                                                                               Optional.of(context.getTokenAccess()));
        if (!res.isPresent()) {
            throw new IOException("HTTP 404 for " + url); //$NON-NLS-1$
        } else {
            return res.get().getTenants();
        }
    }
}
