package com.cloudwatt.apis.bss;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.AbstractTableModel;
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
import com.cloudwatt.apis.bss.spec.exceptions.WrongCredentialsException;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class TestApiGUI {

    private static class AccountFrame extends JPanel {

        private final AccountWithRolesWithOperations account;

        private final ExecutorService executor;

        public AccountFrame(ExecutorService executor, AccountWithRolesWithOperations account) {
            super(new GridBagLayout());
            this.executor = executor;
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
                detailsWidget.setText("Loading...");
                invoicesWidget.setText("Loading...");
                ownedTenantsWidget.setText("Loading...");
                rolesWidget.setText("Loading...");
                executor.submit(new Runnable() {

                    @Override
                    public void run() {
                        refresh();
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                revalidate();
                            }
                        });
                    }
                });
            }
        }

        public void refresh() {
            final AccountApi api = account.getApi();
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        final Optional<AccountDetailApi> detailsApi = api.getAccountDetails();
                        // We check if we have the right to look at the details
                        if (detailsApi.isPresent()) {
                            final AccountDetails account = detailsApi.get().get();
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    detailsWidget.setText("+ Account details: " + account.getName() + "\n"
                                                          + account.getBillingAddress() + "\ncity="
                                                          + account.getBillingCity());
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
                    } catch (final Exception err) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                detailsWidget.setText("Failed to get account details: " + err.getMessage());
                            }
                        });
                    }
                }
            });
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        Optional<AccountRolesListApi> rolesApi = api.getRolesListApi();
                        if (rolesApi.isPresent()) {
                            final StringBuilder sb = new StringBuilder();
                            for (IdentityToAccountRole id : rolesApi.get().get()) {
                                sb.append(id.getUserName() + " (" + id.getUserEmail() + ") has roles "
                                          + id.getUsageType()).append("\n");
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
                    } catch (final Exception err) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                rolesWidget.setText("Failed to get account roles: " + err.getMessage());
                            }
                        });
                    }

                }
            });
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    // List the tenants owned by account
                    try {
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
                    } catch (final Exception err) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                ownedTenantsWidget.setText("Failed to get the tenants owned: " + err.getMessage());
                            }
                        });
                    }

                }
            });
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        Optional<AccountInvoicesApi> myApi = api.getInvoicesApi();
                        if (myApi.isPresent()) {
                            final StringBuilder sb = new StringBuilder();
                            for (Invoice invoice : myApi.get().getInvoices()) {
                                sb.append(invoice.getId() + " (" + invoice.getTotalInEuros() + "EUR) created the "
                                          + invoice.getCreateDate() + "\n");
                                for (Map.Entry<String, URI> en : invoice.getInvoicesURI().entrySet()) {
                                    sb.append("   - " + en.getKey() + ": " + en.getValue().toASCIIString())
                                      .append("\n");
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
                    } catch (final Exception err) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                invoicesWidget.setText("Failed to get the invoices: " + err.getMessage());
                            }
                        });
                    }

                }
            });

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
                final ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactory() {

                    private final AtomicInteger counter = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable target) {
                        Thread t = new Thread(target, "cw-gui-demo-thread-" + counter.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                });
                JPanel contactPanel = new JPanel(new BorderLayout(5, 5));
                {
                    JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
                    contactPanel.add(connectPanel, BorderLayout.NORTH);
                    final JTextField userF = new JTextField(email, 32);
                    final JPasswordField passwordF = new JPasswordField(passwordInit, 16);
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
                                executor.execute(new Runnable() {

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
                                            SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {

                                                    executor.execute(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            try {
                                                                final ImmutableList<TenantIFace> tenants;
                                                                {
                                                                    ImmutableList.Builder<TenantIFace> builder = new ImmutableList.Builder<TenantIFace>();
                                                                    for (TenantIFace t : mainApi.getTenantsList()) {
                                                                        builder.add(t);
                                                                    }
                                                                    tenants = builder.build();
                                                                }
                                                                final AbstractTableModel model = new AbstractTableModel() {

                                                                    @Override
                                                                    public String getColumnName(int column) {
                                                                        switch (column) {
                                                                            case 0:
                                                                                return "id";
                                                                            case 1:
                                                                                return "name";
                                                                            case 2:
                                                                                return "description";
                                                                            default:
                                                                                return "enabled";
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public Class<?> getColumnClass(int columnIndex) {
                                                                        if (columnIndex > 2)
                                                                            return Boolean.class;
                                                                        return super.getColumnClass(columnIndex);
                                                                    }

                                                                    @Override
                                                                    public Object getValueAt(int rowIndex,
                                                                            int columnIndex) {
                                                                        TenantIFace t = tenants.get(rowIndex);
                                                                        switch (columnIndex) {
                                                                            case 0:
                                                                                return t.getId();
                                                                            case 1:
                                                                                return t.getName();
                                                                            case 2:
                                                                                return t.getDescription();
                                                                            default:
                                                                                return t.isEnabled();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public int getRowCount() {
                                                                        return tenants.size();
                                                                    }

                                                                    @Override
                                                                    public int getColumnCount() {
                                                                        return 4;
                                                                    }
                                                                };
                                                                SwingUtilities.invokeLater(new Runnable() {

                                                                    @Override
                                                                    public void run() {
                                                                        tab.insertTab("My Tenants",
                                                                                      null,
                                                                                      new JScrollPane(new JTable(model)),
                                                                                      "The tenants I can access",
                                                                                      1);
                                                                    }
                                                                });
                                                            } catch (Exception err) {
                                                                JOptionPane.showMessageDialog(jf,
                                                                                              "Cannot get the list of my tenants: "
                                                                                                      + err.getLocalizedMessage());
                                                            }

                                                        }
                                                    });

                                                    for (AccountWithRolesWithOperations a : mainApi.getAccounts()) {
                                                        tab.add(a.getCustomerId() + "roles=" + a.getNamedRoles(),
                                                                new AccountFrame(executor, a));
                                                    }
                                                    tab.revalidate();
                                                }
                                            });
                                        } catch (Throwable err) {
                                            if (err instanceof WrongCredentialsException)
                                                JOptionPane.showMessageDialog(jf, err.getLocalizedMessage());
                                            else
                                                JOptionPane.showMessageDialog(jf,
                                                                              "Error while connecting: "
                                                                                      + err.getLocalizedMessage());
                                        } finally {
                                            setEnabled(true);
                                        }
                                    }
                                });

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
                                                           "<html><h1>Cloudwatt Public API Demo</h1><p>A simple Cloudwatt BSS Public API example that explains how to create a rich application displaying Cloudwatt public APIs.</p><p>Simply use your Cloudwatt credentials to see your BSS accounts.</p><h2>Using the API</h2><p>Have a look at the source code of this example:<br> <a href=\"https://git.corp.cloudwatt.com/pierre.souchay/cloudwatt-pub-api-client/blob/master/api/src/test/java/com/cloudwatt/apis/bss/TestApiGUI.java\">https://git.corp.cloudwatt.com/pierre.souchay/cloudwatt-pub-api-client/blob/master/api/src/test/java/com/cloudwatt/apis/bss/TestApiGUI.java</a>.</p><p>There is also a much more simpler example:<br><a href=\"https://git.corp.cloudwatt.com/pierre.souchay/cloudwatt-pub-api-client/blob/master/api/src/test/java/com/cloudwatt/apis/bss/TestAPI.java\">https://git.corp.cloudwatt.com/pierre.souchay/cloudwatt-pub-api-client/blob/master/api/src/test/java/com/cloudwatt/apis/bss/TestAPI.java</a>.</p><p>By Pierre Souchay</p></html>");
                        text.addHyperlinkListener(new HyperlinkListener() {

                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent e) {
                                if (e.getEventType() == EventType.ACTIVATED) {
                                    try {
                                        Desktop.getDesktop().browse(e.getURL().toURI());
                                    } catch (IOException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    } catch (URISyntaxException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                }

                            }
                        });
                        text.setEditable(false);
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
