package com.cloudwatt.apis.bss;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountRolesListApi;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.accountapi.OwnedTenantsListApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenant;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.google.common.base.Optional;

public class TestApiGUI {

    private static class AccountFrame extends JPanel {

        private final AccountWithRolesWithOperations account;

        public AccountFrame(AccountWithRolesWithOperations account) {
            super(new GridBagLayout());
            this.account = account;
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.ipady = 5;
            c.ipadx = 5;
            c.weighty = 1;
            c.weighty = 2;
            c.anchor = GridBagConstraints.EAST;
            c.gridwidth = 1;
            add(new JLabel("Details"), c);
            c.gridy++;
            add(new JLabel("Roles"), c);
            c.gridy++;
            add(new JLabel("Owned Tenants"), c);
            c.gridy++;
            add(new JLabel("Invoices"), c);
            c.gridy++;
            c.gridy = 0;
            c.gridx = 1;
            c.gridwidth = 2;
            c.weightx = 2;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(detailsWidget, c);
            c.gridy++;
            add(rolesWidget, c);
            c.gridy++;
            add(ownedTenantsWidget, c);
            c.gridy++;
            add(invoicesWidget, c);
        }

        private final JTextArea detailsWidget = new JTextArea("Details are not Available");

        private final JTextArea rolesWidget = new JTextArea("Roles are not Available");

        private final JTextArea ownedTenantsWidget = new JTextArea("Owned Tenants are not Available");

        private final JTextArea invoicesWidget = new JTextArea("Invoices are not Available");

        public boolean isInitialized = false;

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if (!isInitialized && visible) {
                isInitialized = true;
                Thread t = new Thread("refreshAccountThread") {

                    @Override
                    public void run() {
                        try {
                            refresh();
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    revalidate();
                                }
                            });
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (TooManyRequestsException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        }

        public void refresh() throws IOException, TooManyRequestsException {
            final AccountApi api = account.getApi();
            {
                final Optional<AccountDetailApi> detailsApi = api.getAccountDetails();
                // We check if we have the right to look at the details
                if (detailsApi.isPresent()) {
                    final AccountDetails account = detailsApi.get().get();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            detailsWidget.setText("+ Account details: " + account.getName() + "\n"
                                                  + account.getBillingAddress() + "\ncity=" + account.getBillingCity());
                        }
                    });

                } else {
                    // Ooops, we cannot see the details, we don't have the rights to
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            detailsWidget.setText("- Account details not available");
                        }
                    });
                }
            }
            // Show Roles
            {
                Optional<AccountRolesListApi> rolesApi = api.getRolesListApi();
                if (rolesApi.isPresent()) {
                    final StringBuilder sb = new StringBuilder();
                    for (IdentityToAccountRole id : rolesApi.get().get()) {
                        sb.append(id.getUserName() + " (" + id.getUserEmail() + ") has roles " + id.getUsageType())
                          .append("\n");
                    }
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            rolesWidget.setText(sb.toString());
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            rolesWidget.setText("- Account roles not available");
                        }
                    });
                }
            }
            // List the tenants owned by account
            {
                Optional<OwnedTenantsListApi> myApi = api.getOwnedTenantsApi();
                if (myApi.isPresent()) {
                    final StringBuilder sb = new StringBuilder();
                    for (OwnedTenant id : myApi.get().get()) {

                        sb.append(id.getTenantId() + " (" + id.getTenantType() + ") created the "
                                  + id.getCreationTime()).append("\n");
                    }
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ownedTenantsWidget.setText(sb.toString());
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ownedTenantsWidget.setText("- Tenants owned not available");

                        }
                    });
                }
            }
            {
                Optional<AccountInvoicesApi> myApi = api.getInvoicesApi();
                if (myApi.isPresent()) {
                    final StringBuilder sb = new StringBuilder();
                    for (Invoice invoice : myApi.get().getInvoices()) {
                        sb.append(invoice.getId() + " (" + invoice.getTotalInEuros() + "EUR) created the "
                                  + invoice.getCreateDate() + "\n");
                        for (Map.Entry<String, URI> en : invoice.getInvoicesURI().entrySet()) {
                            sb.append("   - " + en.getKey() + ": " + en.getValue().toASCIIString()).append("\n");
                        }
                        sb.append("----\n");
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                invoicesWidget.setText(sb.toString());
                            }
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            invoicesWidget.setText("- Invoice API is not available");
                        }
                    });
                }
            }

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

    public static void main(String[] args) throws Exception {
        // Step 0, we parse command line
        final String password = (args.length > 1) ? args[1] : getEnvOrExit("OS_PASSWORD");
        final String email = (args.length > 0) ? args[0] : getEnvOrExit("OS_USERNAME");

        // Step 1 : initialize API with credentials
        final BSSAccountFactory factory = new BSSAccountFactory.Builder(email, password).build();

        final BSSApiHandle mainApi = factory.getHandle();

        System.out.println("Connected as " + mainApi.getIdentity().getEmail() + ", name="
                           + mainApi.getIdentity().getName() + ", id=" + mainApi.getIdentity().getId() + "\n");
        final Map<String, TenantIFace> idTenants = new HashMap<String, TenantIFace>();
        System.out.println("=== Tenants I can access\n Tenant Identifier               \tenabled\tTenant Name\tTenant Description");
        for (TenantIFace t : mainApi.getTenantsList()) {
            idTenants.put(t.getId(), t);
            System.out.println(" " + t.getId() + "\t" + t.isEnabled() + "\t" + t.getName() + "\t" + t.getDescription());
        }

        System.out.println("\n=== Account Information");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame jf = new JFrame("Accounts information");
                jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JTabbedPane tab = new JTabbedPane();
                // Step 3, OK, lets have a look to the accounts: for each account, display all we can display
                {
                    for (AccountWithRolesWithOperations a : mainApi.getAccounts()) {
                        tab.add(a.getCustomerId() + "roles=" + a.getNamedRoles(), new AccountFrame(a));
                    }
                }
                jf.add(tab);
                jf.pack();
                jf.setVisible(true);
            }
        });

    }

}
