/**
 * 
 */
package com.cloudwatt.apis.bss.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.OpenstackUserWithRoles;
import com.cloudwatt.apis.bss.spec.domain.account.openstack.TenantRolesApi;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * @author pierre souchay
 *
 */
public class TenantRolesPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5394541861290986949L;

    /**
     * Constructor
     */
    @SuppressWarnings("nls")
    public TenantRolesPanel(TenantRolesApi api) {
        super(new BorderLayout());
        this.api = api;
        add(new JEditorPane("text/html", "<html><h1>Loading data...</h1></html>"), BorderLayout.CENTER);
    }

    private final TenantRolesApi api;

    private JTable table;

    private List<OpenstackUserWithRoles> events = new ArrayList<OpenstackUserWithRoles>();

    private final AbstractTableModel model = new AbstractTableModel() {

        /**
         * 
         */
        private static final long serialVersionUID = -4089549446929960969L;

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OpenstackUserWithRoles evt = events.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return evt.getEmail();
                case 1:
                    return evt.getRoles();
                default:
                    return null;
            }
        }

        @Override
        @SuppressWarnings("nls")
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "email";
                case 1:
                    return "roles";
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
                default:
                    return null;
            }
        }

        @Override
        public int getRowCount() {
            return events.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }
    };

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            if (table == null) {
                table = new JTable(model);
                Thread t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            Iterable<? extends OpenstackUserWithRoles> events = api.getUsers();
                            for (OpenstackUserWithRoles user : events) {
                                TenantRolesPanel.this.events.add(user);
                            }
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    model.fireTableDataChanged();
                                    removeAll();
                                    add(new JScrollPane(table), BorderLayout.CENTER);
                                    revalidate();
                                }
                            });
                        } catch (IOException | TooManyRequestsException err) {
                            JOptionPane.showMessageDialog(TenantRolesPanel.this,
                                                          err.getClass() + ": " + err.getLocalizedMessage()); //$NON-NLS-1$
                        }
                    }
                };
                t.setDaemon(true);
                t.start();
            }
        }
    }
}
