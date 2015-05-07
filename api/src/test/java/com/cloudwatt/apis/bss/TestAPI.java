package com.cloudwatt.apis.bss;

import java.io.IOException;
import java.util.Locale;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountRolesListApi;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.accountapi.OwnedTenantsListApi;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;

/**
 * Howto use the API very easily
 * 
 * @author pierre souchay
 *
 */
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
        System.out.println(" = Version: " + api.getVersion());
        for (Locale locale : new Locale[] { Locale.FRANCE, Locale.US }) {
            System.out.println(" = All Caps (" + locale.getLanguage() + "): " + api.getAllCapsAndDescriptions(locale));
            System.out.println(" = All Account Types (en): " + api.getAllAccountTypes(locale));
        }
    }

    /**
     * Load environment variable
     * 
     * @param varName the var name as set in the shell
     * @return the value if set in environement, exit the program otherwise
     */
    private static String getEnvOrExit(String varName) {
        String val = System.getenv(varName);
        if (val != null) {
            System.out.println(varName + " has been read from environment");
            return val;
        }
        System.err.println("Missing parameter and parameter not provided into environement variable " + varName
                           + "\n\tUsage: [email] [password]");
        Runtime.getRuntime().exit(2);
        return null;
    }

    /**
     * Main Entry point
     * 
     * @param args first parameter is email, second parameter is password
     */
    public static void main(String... args) {

        try {

            // Step 0, we parse command line
            final String password = (args.length > 1) ? args[1] : getEnvOrExit("OS_PASSWORD");
            final String email = (args.length > 0) ? args[0] : getEnvOrExit("OS_USERNAME");

            // Step 1 : initialize API with credentials
            final BSSAccountFactory factory = new BSSAccountFactory.Builder(email, password).build();

            final BSSApiHandle mainApi = factory.getHandle();

            System.out.println("Connected as " + mainApi.getIdentity().getEmail() + ", name="
                               + mainApi.getIdentity().getName() + ", id=" + mainApi.getIdentity().getId() + "\n");

            System.out.println("=== Tenants I can access\n Tenant Identifier               \tenabled\tTenant Name\tTenant Description");
            for (TenantIFace t : mainApi.getTenantsList()) {
                System.out.println(" " + t.getId() + "\t" + t.isEnabled() + "\t" + t.getName() + "\t"
                                   + t.getDescription());
            }

            System.out.println("\n=== Global public calls");

            // Step 2, we can now play with the API
            testCommonApi(mainApi.getCommmonApi());

            System.out.println("\n=== Account Information");

            // Step 3, OK, lets have a look to the accounts: for each account, display all we can display
            {
                for (AccountWithRolesWithOperations a : mainApi.getAccounts()) {
                    final AccountApi api = a.getApi();
                    System.out.println("\n*** Account " + a.getCustomerId());
                    {
                        final Optional<AccountDetailApi> detailsApi = api.getAccountDetails();
                        // We check if we have the right to look at the details
                        if (detailsApi.isPresent()) {
                            final AccountDetails account = detailsApi.get().get();
                            System.out.println("+ Account details: " + account.getName() + ", "
                                               + account.getBillingAddress() + ", city=" + account.getBillingCity()
                                               + ", caps=" + a.getCaps());
                        } else {
                            // Ooops, we cannot see the details, we don't have the rights to
                            System.out.println("- Account details not available");
                        }
                    }
                    // Show Roles
                    {
                        Optional<AccountRolesListApi> rolesApi = api.getRolesListApi();
                        if (rolesApi.isPresent()) {
                            System.out.println("+ Listing of Roles");
                            for (IdentityToAccountRole id : rolesApi.get().get()) {
                                System.out.println("\t\t" + id.getUserName() + " (" + id.getUserEmail()
                                                   + ") has roles " + id.getUsageType());
                            }
                        } else {
                            System.out.println("- Account roles not available");
                        }
                    }
                    {
                        Optional<OwnedTenantsListApi> myApi = api.getOwnedTenantsApi();
                        if (myApi.isPresent()) {
                            System.out.println("+ Listing of Tenants owned");
                            for (OwnedTenant id : myApi.get().get()) {
                                System.out.println("\t\t" + id.getTenantId() + " (" + id.getTenantType()
                                                   + ") created the " + id.getCreationTime());
                            }
                        } else {
                            System.out.println("- Tenants owned not available");
                        }
                    }

                }
            }
        } catch (IOException err) {
            System.err.println("Could not perform some calls " + err.getClass() + ": " + err.getLocalizedMessage());
            Runtime.getRuntime().exit(3);
        } catch (TooManyRequestsException err) {
            // Note that i18n is managed properly for Exceptions, if you prefer English only, use err.getMessage()
            System.err.println("Stop playing, you have been blocked: " + err.getLocalizedMessage());
            Runtime.getRuntime().exit(4);
        }
    }
}
