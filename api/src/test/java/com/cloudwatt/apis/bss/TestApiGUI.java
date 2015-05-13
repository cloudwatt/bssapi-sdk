package com.cloudwatt.apis.bss;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
     * @return the value if set in environment, exit the program otherwise
     */
    private static String getEnvOrExit(String varName, String defaultValue) {
        String val = System.getenv(varName);
        if (val != null) {
            System.out.println(varName + " has been read from environment");
            return val;
        }
        return defaultValue;
    }

    public static void main(String[] args) throws Exception {
        // Step 0, we parse command line
        final String passwordInit = (args.length > 1) ? args[1] : getEnvOrExit("OS_PASSWORD", "");
        final String email = (args.length > 0) ? args[0] : getEnvOrExit("OS_USERNAME", "email@example.com");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final JFrame jf = new JFrame("Cloudwatt Public API Demonstration");
                jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                final JTabbedPane tab = new JTabbedPane();
                JPanel contactPanel = new JPanel(new BorderLayout(5, 5));
                {
                    JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
                    contactPanel.add(connectPanel, BorderLayout.NORTH);
                    final JTextField userF = new JTextField(email);
                    final JPasswordField passwordF = new JPasswordField(passwordInit);
                    connectPanel.add(new JLabel("Email:"));
                    connectPanel.add(userF);
                    connectPanel.add(new JLabel("Password:"));
                    connectPanel.add(passwordF);
                    final Action connect = new AbstractAction() {

                        private boolean connected = false;
                        {
                            putValue(Action.NAME, "Connect");
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (connected) {
                                connected = false;
                                putValue(Action.NAME, "Connect to API");
                                while (tab.getTabCount() > 1) {
                                    tab.removeTabAt(1);
                                }
                                tab.revalidate();
                            } else {
                                setEnabled(false);
                                Thread t = new Thread("connectThread") {

                                    @Override
                                    public void run() {
                                        try {

                                            // Step 1 : initialize API with credentials
                                            final BSSAccountFactory factory = new BSSAccountFactory.Builder(userF.getText(),
                                                                                                            new String(passwordF.getPassword())).build();

                                            final BSSApiHandle mainApi = factory.getHandle();
                                            connected = true;
                                            putValue(Action.NAME, "Disconnect");
                                            System.out.println("Connected as " + mainApi.getIdentity().getEmail()
                                                               + ", name=" + mainApi.getIdentity().getName() + ", id="
                                                               + mainApi.getIdentity().getId() + "\n");
                                            final Map<String, TenantIFace> idTenants = new HashMap<String, TenantIFace>();
                                            System.out.println("=== Tenants I can access\n Tenant Identifier               \tenabled\tTenant Name\tTenant Description");
                                            for (TenantIFace t : mainApi.getTenantsList()) {
                                                idTenants.put(t.getId(), t);
                                                System.out.println(" " + t.getId() + "\t" + t.isEnabled() + "\t"
                                                                   + t.getName() + "\t" + t.getDescription());
                                            }
                                            SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                    for (AccountWithRolesWithOperations a : mainApi.getAccounts()) {
                                                        tab.add(a.getCustomerId() + "roles=" + a.getNamedRoles(),
                                                                new AccountFrame(a));
                                                    }
                                                    tab.revalidate();
                                                }
                                            });
                                        } catch (Exception err) {
                                            JOptionPane.showMessageDialog(jf,
                                                                          "Error while connecting: "
                                                                                  + err.getLocalizedMessage());
                                        } finally {
                                            setEnabled(true);
                                        }
                                    }
                                };
                                t.start();

                            }

                        }
                    };
                    // South
                    {
                        JPanel south = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
                        south.add(new JButton(connect));
                        contactPanel.add(south, BorderLayout.SOUTH);

                    }
                    // Center
                    {
                        JEditorPane text = new JEditorPane("text/html",
                                                           "<html><h1>Cloudwatt Public API Demo</h1><p>A simple Cloudwatt BSS Public API example that explains how to create a rich application displaying Cloudwatt public APIs.</p><p>By Pierre Souchay</p></html>");
                        contactPanel.add(text, BorderLayout.CENTER);
                    }

                }
                // contactPanel.add(new )
                tab.add("Connection", contactPanel);
                // Step 3, OK, lets have a look to the accounts: for each account, display all we can display

                jf.add(tab);
                jf.pack();
                jf.setVisible(true);
            }
        });

    }
}
