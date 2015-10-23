package com.cloudwatt.apis.bss.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import com.cloudwatt.apis.bss.spec.domain.BSSApiHandle;
import com.cloudwatt.apis.bss.spec.domain.Identity;
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

    private static class ListOfIdentityToAccountRole extends AbstractListModel<IdentityToAccountRole> {

        /**
         * 
         */
        private static final long serialVersionUID = 5706717422482497352L;

        private final List<IdentityToAccountRole> list = new ArrayList<IdentityToAccountRole>();

        @Override
        public int getSize() {
            return list.size();
        }

        @Override
        public IdentityToAccountRole getElementAt(int index) {
            return list.get(index);
        }

        public void setList(final Collection<IdentityToAccountRole> elements) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final int oldSize = list.size();
                    list.clear();
                    list.addAll(0, elements);
                    final int newSize = list.size();
                    if (oldSize > newSize) {
                        fireIntervalRemoved(this, newSize, oldSize - 1);
                    } else if (oldSize < newSize) {
                        fireIntervalAdded(this, oldSize, newSize - 1);
                    }
                    if (newSize > 0)
                        fireContentsChanged(ListOfIdentityToAccountRole.this, 0, newSize - 1);
                }
            });

        }

    }

    private static String loading() {
        return "<html><div><i>Loading from API...</i></html>";
    }

    private final BSSApiHandle mainApi;

    private final ListOfIdentityToAccountRole rolesOnAccount = new ListOfIdentityToAccountRole();

    public AccountInformationWidget(ExecutorService executor, AccountWithRolesWithOperations account,
            BSSApiHandle mainApi) {
        super(new GridBagLayout());
        this.executor = executor;
        this.account = account;
        this.mainApi = mainApi;
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

    private final JPanel rolesWidget = new JPanel(new BorderLayout());

    private final JEditorPane rolesNotAvailable = new JEditorPane(TEXT_HTML, notAvailable());

    private final JScrollPane rolesWidgetPanel = new JScrollPane(rolesNotAvailable);

    private final JEditorPane detailsWidget = new JEditorPane(TEXT_HTML, notAvailable());

    private final DefaultComboBoxModel<String> rolesToAddModel = new DefaultComboBoxModel<String>();

    private final Action deleteRoleAction = new AbstractAction() {

        /**
         * 
         */
        private static final long serialVersionUID = 4072793782162076658L;

        {
            putValue(Action.NAME, "Remove Role");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final IdentityToAccountRole r = listOfRoles.getSelectedValue();
            if (r != null) {
                setEnabled(false);
                executor.submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            account.getApi().getRolesListApi().get().getEditRolesApi().get().removeRole(r);
                        } catch (Throwable err) {
                            JOptionPane.showMessageDialog(null,
                                                          "Failed to remove Role " + r.getUserEmail() + " ("
                                                                  + r.getUsageType() + ")  due to error "
                                                                  + err.getClass() + ": " + err.getMessage());
                        } finally {
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    refresh();
                                    setEnabled(listOfRoles.getSelectedValue() != null);
                                }
                            });
                        }
                    }
                });

            }
        }
    };

    private final JList<IdentityToAccountRole> listOfRoles = new JList<IdentityToAccountRole>(rolesOnAccount);
    {
        listOfRoles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOfRoles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (listOfRoles.getSelectedValue() == null) {
                    deleteRoleAction.setEnabled(false);
                } else {
                    deleteRoleAction.setEnabled(account.getCaps().contains("ACCOUNT_ROLES_EDIT"));
                }
            }
        });
        listOfRoles.setCellRenderer(new DefaultListCellRenderer() {

            /**
             * 
             */
            private static final long serialVersionUID = -8862135515110195678L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof IdentityToAccountRole) {
                    IdentityToAccountRole val = (IdentityToAccountRole) value;
                    value = val.getUserEmail() + " (" + val.getUsageType() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        });
        rolesWidget.add(rolesWidgetPanel, BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add(new JButton(deleteRoleAction), BorderLayout.WEST);
        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.add(new JLabel("Add user: "));
        final JComboBox<String> roleToAddCombo = new JComboBox<String>(rolesToAddModel);
        final JTextField emailToAdd = new JTextField(32);
        final Action addAction = new AbstractAction() {

            /**
             * 
             */
            private static final long serialVersionUID = 3512755900934735798L;

            {
                setEnabled(false);
                putValue(Action.NAME, "addRole");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                executor.submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Identity identity = mainApi.getFindUserApi()
                                                       .get()
                                                       .findUser(mainApi.getFindUserApi()
                                                                        .get()
                                                                        .builder(emailToAdd.getText())
                                                                        .build());
                            account.getApi()
                                   .getRolesListApi()
                                   .get()
                                   .getEditRolesApi()
                                   .get()
                                   .addRoleToIdentity(identity, String.valueOf(roleToAddCombo.getSelectedItem()));
                        } catch (Throwable err) {
                            JOptionPane.showMessageDialog(null,
                                                          "Failed to add Role " + roleToAddCombo.getSelectedItem()
                                                                  + " to user " + emailToAdd.getText()
                                                                  + " due to error " + err.getClass() + ": "
                                                                  + err.getMessage());
                        } finally {
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    refresh();
                                    setEnabled(listOfRoles.getSelectedValue() != null);
                                }
                            });

                        }
                    }
                });
            }
        };

        emailToAdd.setToolTipText("email");
        addPanel.add(emailToAdd);
        addPanel.add(roleToAddCombo);
        south.add(addPanel, BorderLayout.CENTER);
        emailToAdd.getDocument().addDocumentListener(new DocumentListener() {

            private void check() {
                addAction.setEnabled(roleToAddCombo.getSelectedItem() != null && emailToAdd.getText() != null
                                     && emailToAdd.getText().length() > 4 && emailToAdd.getText().contains("@"));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                check();
            }
        });
        south.add(new JButton(addAction), BorderLayout.EAST);
        rolesWidget.add(south, BorderLayout.SOUTH);
    }

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
                for (JEditorPane ed : new JEditorPane[] { detailsWidget, ownedTenantsWidgetText, invoicesWidget }) {
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
                        final LinkedList<IdentityToAccountRole> roles = new LinkedList<IdentityToAccountRole>();
                        for (IdentityToAccountRole id : rolesApi.get().get()) {
                            roles.add(id);
                        }
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                rolesOnAccount.setList(roles);
                                rolesWidgetPanel.setViewportView(listOfRoles);
                            }
                        });
                        if (rolesApi.get().getEditRolesApi().isPresent()) {
                            if (rolesToAddModel.getSize() < 1) {
                                final Iterable<String> allowedRoles = rolesApi.get()
                                                                              .getEditRolesApi()
                                                                              .get()
                                                                              .listAllowedRolesForAccount();
                                SwingUtilities.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        for (String a : allowedRoles) {
                                            rolesToAddModel.addElement(a);
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                rolesNotAvailable.setText(notAvailable());
                                rolesWidgetPanel.setViewportView(rolesNotAvailable);
                            }
                        });
                    }
                } catch (final Exception err) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            rolesNotAvailable.setText(errorMessageToHTML("Failed to get account roles",
                                                                         err.getMessage()));
                            rolesWidgetPanel.setViewportView(rolesNotAvailable);
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
                                    if (id.getOpenstackRolesApi().isPresent()) {
                                        jt.add("Users " + id.getTenantId(),
                                               new TenantRolesPanel(id.getOpenstackRolesApi().get()));
                                    }
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