package com.cloudwatt.apis.bss;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.AbstractTableModel;
import com.cloudwatt.apis.bss.gui.AccountInformationWidget;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.domain.keystone.TenantIFace;
import com.cloudwatt.apis.bss.spec.exceptions.WrongCredentialsException;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("nls")
public class TestApiGUI {

    /**
     * Load environment variable
     * 
     * @param varName the var name as set in the shell
     * @return the value if set in environment, exit the program otherwise
     */
    private static String getEnvOrExit(String varName, String defaultValue) {
        String val = System.getenv(varName);
        if (val != null) {
            System.out.println(varName + " has been read from environment"); //$NON-NLS-1$
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
                        Thread t = new Thread(target, "cw-gui-demo-thread-" + counter.incrementAndGet()); //$NON-NLS-1$
                        t.setDaemon(true);
                        return t;
                    }
                });
                JPanel contactPanel = new JPanel(new BorderLayout(5, 5));
                final JLabel status = new JLabel("xxx", JLabel.TRAILING);
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

                        /**
                         * 
                         */
                        private static final long serialVersionUID = 1448831485136149625L;

                        private volatile boolean connected = false;

                        private void updateGUI() {
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    putValue(Action.NAME, connected ? "Disconnect" : "Connect");
                                    status.setText(connected ? "Connected" : "Not Connected");
                                    userF.setEnabled(!connected);
                                    passwordF.setEnabled(!connected);
                                }
                            });

                        }

                        {
                            updateGUI();
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (connected) {
                                connected = false;
                                updateGUI();

                                while (tab.getTabCount() > 1) {
                                    tab.removeTabAt(1);
                                }
                                tab.revalidate();
                            } else {
                                setEnabled(false);
                                SwingUtilities.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        status.setText("Connecting to APIs...");
                                    }
                                });
                                executor.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {

                                            // Step 1 : initialize API with credentials
                                            final BSSAccountFactory factory = new BSSAccountFactory.Builder(userF.getText(),
                                                                                                            new String(passwordF.getPassword())).keystonePublicEndpoint(new java.net.URL("http://127.0.0.1:9479/rest/kspublic/keystone/v2.0/"))
                                                                                                                                                .overrideBSSAPIEndpoint(new java.net.URL("http://127.0.0.1:9479/rest/public"))
                                                                                                                                                .build();

                                            final BSSApiHandle mainApi = factory.getHandle();
                                            connected = true;
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

                                                                    /**
                                                                     * 
                                                                     */
                                                                    private static final long serialVersionUID = -6502459171631045831L;

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
                                                        final String tabName = a.getAccountMinimalInformation()
                                                                                .isPresent() ? String.format("%s %s (%s) %s roles: %s",
                                                                                                             a.getCustomerId(),
                                                                                                             a.getAccountMinimalInformation()
                                                                                                              .get()
                                                                                                              .getName(),
                                                                                                             a.getAccountMinimalInformation()
                                                                                                              .get()
                                                                                                              .getEmail(),
                                                                                                             a.getAccountMinimalInformation()
                                                                                                              .get()
                                                                                                              .getCorporateName()
                                                                                                              .or(""),
                                                                                                             a.getNamedRoles()) : String.format("%s roles: %s",
                                                                                                                                                a.getCustomerId(),
                                                                                                                                                a.getNamedRoles());
                                                        tab.addTab(tabName, new AccountInformationWidget(executor, a));
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
                                            SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                    updateGUI();
                                                    setEnabled(true);
                                                }
                                            });
                                        }
                                    }
                                });

                            }

                        }
                    };
                    // South
                    {
                        JPanel south = new JPanel(new FlowLayout(FlowLayout.TRAILING, 20, 5));
                        south.add(status);
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
                                if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                                    try {
                                        Desktop.getDesktop().browse(e.getURL().toURI());
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    } catch (URISyntaxException e1) {
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
