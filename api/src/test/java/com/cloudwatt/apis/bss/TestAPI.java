package com.cloudwatt.apis.bss;

import java.io.IOException;
import java.util.Locale;
import com.cloudwatt.apis.bss.impl.BSSAcountFactory;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;

@SuppressWarnings("nls")
public class TestAPI {

    /**
     * Display results of common API
     * 
     * @param api
     * @throws IOException
     * @throws TooManyRequestsException
     */
    public static void testCommonApi(CommonApi api) throws IOException, TooManyRequestsException {
        System.out.println("Version: " + api.getVersion());
        for (Locale locale : new Locale[] { Locale.FRANCE, Locale.KOREA, Locale.US }) {
            System.out.println("All Caps (" + locale.getLanguage() + "): " + api.getAllCapsAndDescriptions(locale));
            System.out.println("All Account Types (en): " + api.getAllAccountTypes(locale));
        }
    }

    /**
     * Dump account information
     * 
     * @param api
     * @throws IOException
     * @throws TooManyRequestsException
     */
    private static void testAccountApi(AccountApi api) throws IOException, TooManyRequestsException {
        Optional<AccountDetailApi> detailsApi = api.getAccountDetails();
        if (detailsApi.isPresent()) {
            final AccountDetails account = detailsApi.get().get();
            System.out.println("+ Account details: " + account.getCustomerId() + " - " + account.getName() + ", "
                               + account.getBillingAddress());
        } else {
            System.out.println("- Account details not available");
        }
    }

    public static void main(String... args) throws IOException, TooManyRequestsException {
        if (args.length < 2) {
            System.err.println("Usage: email password");
            return;
        }
        final String email = args[0];
        final String password = args[1];
        // Step 1 : credentials
        final BSSAcountFactory factory = new BSSAcountFactory.Builder(email, password).overrideUserAgent("MyTestApi")
                                                                                      .build();

        System.out.println("=== Global public calls");
        testCommonApi(factory.getHandler().getCommmonApi());

        System.out.println("\n=== Account Information");
        Iterable<AccountWithRolesWithOperations> all_accounts = factory.getHandler().getAccounts();
        for (AccountWithRolesWithOperations a : all_accounts) {
            testAccountApi(a.getApi());
        }

    }
}
