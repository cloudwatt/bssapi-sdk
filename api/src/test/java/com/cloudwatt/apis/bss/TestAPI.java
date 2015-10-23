package com.cloudwatt.apis.bss;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi.InvoiceExtension;
import com.cloudwatt.apis.bss.spec.accountapi.AccountRolesListApi;
import com.cloudwatt.apis.bss.spec.accountapi.ConsumptionApi;
import com.cloudwatt.apis.bss.spec.accountapi.ConsumptionApi.ConsumptionApiBuilder;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.accountapi.OwnedTenantsListApi;
import com.cloudwatt.apis.bss.spec.accountapi.RolesEditApi;
import com.cloudwatt.apis.bss.spec.commonapi.CommonApi;
import com.cloudwatt.apis.bss.spec.commonapi.FindUserApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenantWithApi;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.OpenstackRole;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.OpenstackUserWithRoles;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.TenantRolesApi;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEvent;
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
        System.out.println(api.getCountryCodes());
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
        final long start = System.currentTimeMillis();
        try {

            // Step 0, we parse command line
            final String password = (args.length > 1) ? args[1] : getEnvOrExit("OS_PASSWORD");
            final String email = (args.length > 0) ? args[0] : getEnvOrExit("OS_USERNAME");

            // Step 1 : initialize API with credentials
            final BSSAccountFactory factory = new BSSAccountFactory.Builder(email, password).build();

            final BSSApiHandle mainApi = factory.getHandle();

            System.out.println("Connected as " + mainApi.getIdentity().getEmail() + ", name="
                               + mainApi.getIdentity().getName() + ", id=" + mainApi.getIdentity().getId() + "\n");

            Optional<FindUserApi> userApi = mainApi.getFindUserApi();
            if (userApi.isPresent()) {
                Identity id = userApi.get().findUser(userApi.get().builder(email).build());
                System.out.println("Found User identity=" + id.getEmail() + ", id=" + id.getId() + ", iam="
                                   + id.getName());
            }

            final Map<String, TenantIFace> idTenants = new HashMap<String, TenantIFace>();
            System.out.println("=== Tenants I can access\n Tenant Identifier               \tenabled\tTenant Name\tTenant Description");
            for (TenantIFace t : mainApi.getTenantsList()) {
                idTenants.put(t.getId(), t);
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
                    final long startAccount = System.currentTimeMillis();
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
                                System.out.println("\t" + id.getUserName() + " (" + id.getUserEmail() + ") has roles "
                                                   + id.getUsageType());
                            }
                            Optional<RolesEditApi> optionalEditApi = rolesApi.get().getEditRolesApi();
                            if (optionalEditApi.isPresent()) {
                                RolesEditApi editApi = optionalEditApi.get();
                                Iterable<String> allowedRoles = editApi.listAllowedRolesForAccount();
                                System.out.println(" *** You can add the BSS Roles: " + allowedRoles);
                            }
                        } else {
                            System.out.println("- Account roles not available");
                        }
                    }
                    // List the tenants owned by account
                    {
                        Optional<OwnedTenantsListApi> myApi = api.getOwnedTenantsApi();
                        if (myApi.isPresent()) {
                            System.out.println("+ Listing of Tenants owned");
                            for (OwnedTenantWithApi id : myApi.get().get()) {
                                System.out.print("\t" + id.getTenantId() + " (" + id.getTenantType() + ") created the "
                                                 + id.getCreationTime());
                                TenantIFace ta = idTenants.get(id.getTenantId());
                                if (ta != null) {
                                    System.out.println("\t Caller has access to " + ta.getName() + ", enabled="
                                                       + ta.isEnabled());
                                } else {
                                    System.out.println("\t No access");
                                }
                                if (id.getOpenstackRolesApi().isPresent()) {
                                    TenantRolesApi roles = id.getOpenstackRolesApi().get();
                                    for (OpenstackUserWithRoles u : roles.getUsers()) {
                                        System.out.print("\t\t" + u.getEmail());
                                        System.out.print("\t has roles: [");
                                        for (OpenstackRole osR : u.getRoles()) {
                                            System.out.print(" " + osR.getName());
                                        }
                                        System.out.println("].");
                                    }
                                } else {
                                    System.out.println("\t\t No Access to users of tenant " + id.getTenantId());
                                }
                                if (id.getConsumptionApi().isPresent()) {
                                    System.out.println("\t Consumption for " + id.getTenantId());
                                    final ConsumptionApi consumeApi = id.getConsumptionApi().get();
                                    final ConsumptionApiBuilder builder = consumeApi.get();
                                    final Iterable<? extends HourlyEvent> events = builder.get();
                                    for (HourlyEvent h : events) {
                                        System.out.println("\t\t" + h.toString());
                                    }
                                }
                            }
                        } else {
                            System.out.println("- Tenants owned not available");
                        }
                    }
                    {
                        Optional<AccountInvoicesApi> myApi = api.getInvoicesApi();
                        if (myApi.isPresent()) {
                            System.out.println("+ Listing of Invoices");
                            for (Invoice invoice : myApi.get()
                                                        .get()
                                                        .setExtensions(InvoiceExtension.pdf, InvoiceExtension.csv)
                                                        .get()) {
                                System.out.print("\t" + invoice.getId() + " (" + invoice.getTotalInEuros()
                                                 + "EUR) created the " + invoice.getCreateDate() + ", URLs: [");
                                for (Map.Entry<String, URI> en : invoice.getInvoicesURI().entrySet()) {
                                    System.out.print(" " + en.getKey() + ": " + en.getValue().toASCIIString());
                                }
                                System.out.println("]");
                            }
                        } else {
                            System.out.println("- Invoice API is not available");
                        }
                    }
                    System.out.println("  Got account " + a.getCustomerId() + " information in "
                                       + (System.currentTimeMillis() - startAccount) + " ms");
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
        System.out.println("Finished in " + (System.currentTimeMillis() - start) + " ms");
    }
}
