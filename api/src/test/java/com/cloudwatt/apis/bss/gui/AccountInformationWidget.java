package com.cloudwatt.apis.bss.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import com.cloudwatt.apis.bss.spec.accountapi.AccountApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountDetailApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi;
import com.cloudwatt.apis.bss.spec.accountapi.AccountInvoicesApi.InvoiceExtension;
import com.cloudwatt.apis.bss.spec.accountapi.AccountRolesListApi;
import com.cloudwatt.apis.bss.spec.accountapi.IdentityToAccountRole;
import com.cloudwatt.apis.bss.spec.accountapi.OwnedTenantsListApi;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRolesWithOperations;
import com.cloudwatt.apis.bss.spec.domain.account.AccountDetails;
import com.cloudwatt.apis.bss.spec.domain.account.OwnedTenantWithApi;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.google.common.base.Optional;

/**
 * Information about a commercial account
 * 
 * @author pierre souchay
 *
 */
@SuppressWarnings("nls")
public class AccountInformationWidget extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 4357856524523001745L;

    private final AccountWithRolesWithOperations account;

    private final ExecutorService executor;

    private final static String TEXT_HTML = "text/html"; //$NON-NLS-1$

    private static String errorMessageToHTML(String type, String msg) {
        return "<html><div><b>" + type + "</b>:</div><pre>" + msg + "</pre></html>";
    }

    private static String notAvailable() {
        return "<html><div><i>Not Available, you probably don't have the rights to see this information</i></html>";
    }

    private static String loading() {
        return "<html><div><i>Loading from API...</i></html>";
    }

    public AccountInformationWidget(ExecutorService executor, AccountWithRolesWithOperations account) {
        super(new GridBagLayout());
        this.executor = executor;
        this.account = account;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 5;
        c.ipadx = 5;
        c.weighty = 1f;
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        add(new JLabel("Details: "), c);
        c.gridy++;
        add(new JLabel("Roles: "), c);
        c.gridy++;
        add(new JLabel("Owned Tenants: "), c);
        c.gridy++;
        add(new JLabel("Invoices: "), c);

        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 2f;
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

    private final JEditorPane detailsWidget = new JEditorPane(TEXT_HTML, notAvailable());

    private final JEditorPane rolesWidget = new JEditorPane(TEXT_HTML, notAvailable());

    private final JEditorPane ownedTenantsWidgetText = new JEditorPane(TEXT_HTML, notAvailable());

    private final JPanel ownedTenantsWidget = new JPanel(new BorderLayout());

    {
        ownedTenantsWidget.add(ownedTenantsWidgetText, BorderLayout.CENTER);
    }

    private final JEditorPane invoicesWidget = new JEditorPane(TEXT_HTML, notAvailable());

    public boolean isInitialized = false;

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!isInitialized && visible) {
            isInitialized = true;
            {
                HyperlinkListener listener = new HyperlinkListener() {

                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e.getEventType() == EventType.ACTIVATED) {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (Throwable e1) {
                                System.err.println("Failed to open hyperlink: " + e1.getLocalizedMessage());
                                e1.printStackTrace();
                            }
                        }
                    }
                };
                for (JEditorPane ed : new JEditorPane[] { detailsWidget, rolesWidget, ownedTenantsWidgetText,
                                                         invoicesWidget }) {
                    ed.setText(loading());
                    ed.setEditable(false);
                    ed.addHyperlinkListener(listener);
                }
            }
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
                                detailsWidget.setText("<html>" + account.getName() + "<hr>"
                                                      + account.getBillingAddress() + "<br>"
                                                      + account.getBillingAddressPostCode() + " "
                                                      + account.getBillingCity() + "<br>" + account.getBillingCountry()
                                                      + "</html>");
                            }
                        });

                    } else {
                        // Ooops, we cannot see the details, we don't have the rights to
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                detailsWidget.setText(notAvailable());
                            }
                        });
                    }
                } catch (final Exception err) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            detailsWidget.setText(errorMessageToHTML("Failed to get account details", err.getMessage()));
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
                        final StringBuilder sb = new StringBuilder("<html><ol>");
                        for (IdentityToAccountRole id : rolesApi.get().get()) {
                            sb.append("<li>")
                              .append(id.getUserName())
                              .append("&lt;<a href=\"mailto:")
                              .append(id.getUserEmail())
                              .append("\">")
                              .append(id.getUserEmail())
                              .append("</a>&gt; has roles <i>")
                              .append(id.getUsageType())
                              .append("</i></li>");
                        }
                        sb.append("</ol></html>");
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
                                rolesWidget.setText(notAvailable());
                            }
                        });
                    }
                } catch (final Exception err) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            rolesWidget.setText(errorMessageToHTML("Failed to get account roles", err.getMessage()));
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
                    final Optional<OwnedTenantsListApi> myApi = api.getOwnedTenantsApi();
                    if (myApi.isPresent()) {
                        final List<OwnedTenantWithApi> tenants = new ArrayList<OwnedTenantWithApi>();
                        for (OwnedTenantWithApi id : myApi.get().get()) {
                            tenants.add(id);
                        }

                        final AbstractTableModel model = new AbstractTableModel() {

                            /**
                             * 
                             */
                            private static final long serialVersionUID = -2774483270266701078L;

                            @Override
                            public String getColumnName(int column) {
                                switch (column) {
                                    case 0:
                                        return "id";
                                    case 1:
                                        return "type";
                                    case 2:
                                        return "creation Time";
                                    default:
                                        return null;
                                }
                            }

                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                switch (columnIndex) {
                                    case 0:
                                        return String.class;
                                    case 1:
                                        return String.class;
                                    case 2:
                                        return Date.class;
                                    default:
                                        return null;
                                }
                            }

                            @Override
                            public Object getValueAt(int rowIndex, int columnIndex) {
                                final OwnedTenantWithApi ow = tenants.get(rowIndex);
                                switch (columnIndex) {
                                    case 0:
                                        return ow.getTenantId();
                                    case 1:
                                        return ow.getTenantType();
                                    case 2:
                                        return ow.getCreationTime();
                                    default:
                                        return null;
                                }

                            }

                            @Override
                            public int getRowCount() {
                                return tenants.size();
                            }

                            @Override
                            public int getColumnCount() {
                                return 3;
                            }
                        };

                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                ownedTenantsWidget.removeAll();
                                JTable table = new JTable(model);
                                table.setDefaultRenderer(OwnedTenantWithApi.class, new TableCellRenderer() {

                                    private final JButton button = new JButton("consumption");

                                    @Override
                                    public Component getTableCellRendererComponent(JTable table, Object value,
                                            boolean isSelected, boolean hasFocus, int row, int column) {
                                        OwnedTenantWithApi ow = (OwnedTenantWithApi) value;
                                        button.setEnabled(ow.getConsumptionApi().isPresent());
                                        return button;
                                    }
                                });
                                JTabbedPane jt = new JTabbedPane();
                                jt.addTab("OwnedTenants", table);
                                for (OwnedTenantWithApi id : tenants) {
                                    if (id.getConsumptionApi().isPresent()) {
                                        jt.add("Consumption " + id.getTenantId(),
                                               new ConsumptionPanel(id.getConsumptionApi().get()));
                                    }
                                }
                                ownedTenantsWidget.add(jt, BorderLayout.CENTER);
                                ownedTenantsWidget.revalidate();
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                ownedTenantsWidgetText.setText(notAvailable());

                            }
                        });
                    }
                } catch (final Exception err) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ownedTenantsWidgetText.setText(errorMessageToHTML("Failed to get the tenants owned",
                                                                              err.getLocalizedMessage()));
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
                        final StringBuilder sb = new StringBuilder("<html><ol>");
                        for (Invoice invoice : myApi.get().get().setExtensions(InvoiceExtension.pdf).get()) {
                            sb.append("<li>Invoice number ")
                              .append(invoice.getId())
                              .append(" of ")
                              .append(invoice.getTotalInEuros())
                              .append("&euro; the " + invoice.getCreateDate() + " Download ");
                            for (Map.Entry<String, URI> en : invoice.getInvoicesURI().entrySet()) {
                                sb.append(" <a href=\"")
                                  .append(en.getValue().toASCIIString())
                                  .append("\">as ")
                                  .append(en.getKey())
                                  .append("</a>...");
                            }
                            sb.append("</li>");
                        }
                        sb.append("</ol></html>");
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                invoicesWidget.setText(sb.toString());
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                invoicesWidget.setText(notAvailable());
                            }
                        });
                    }
                } catch (final Exception err) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            invoicesWidget.setText(errorMessageToHTML("Failed to get the invoices", err.getMessage()));
                        }
                    });
                }

            }
        });

    }
}