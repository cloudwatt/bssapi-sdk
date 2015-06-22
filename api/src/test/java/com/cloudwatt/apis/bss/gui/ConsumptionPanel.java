/**
 * 
 */
package com.cloudwatt.apis.bss.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import com.cloudwatt.apis.bss.spec.accountapi.ConsumptionApi;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEvent;
import com.cloudwatt.apis.bss.spec.domain.consumption.HourlyEvent.EventType;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * @author pierre souchay
 *
 */
public class ConsumptionPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5394541861290986949L;

    /**
     * Constructor
     */
    @SuppressWarnings("nls")
    public ConsumptionPanel(ConsumptionApi api) {
        super(new BorderLayout());
        this.api = api;
        add(new JEditorPane("text/html", "<html><h1>Loading data...</h1></html>"), BorderLayout.CENTER);
    }

    private final ConsumptionApi api;

    private JTable table;

    private List<HourlyEvent> events = new ArrayList<HourlyEvent>();

    private final AbstractTableModel model = new AbstractTableModel() {

        /**
         * 
         */
        private static final long serialVersionUID = -4089549446929960969L;

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HourlyEvent evt = events.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return evt.getUtcComputeDate();
                case 1:
                    return evt.getEventType();
                case 2:
                    return evt;
                default:
                    return null;
            }
        }

        @Override
        @SuppressWarnings("nls")
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "date";
                case 1:
                    return "type";
                case 2:
                    return "event";
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Date.class;
                case 1:
                    return EventType.class;
                case 2:
                    return HourlyEvent.class;
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
            return 3;
        }
    };

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            if (table == null) {
                table = new JTable(model);
                table.getColumnModel().getColumn(0).setMaxWidth(128);
                table.getColumnModel().getColumn(1).setMaxWidth(128);
                Thread t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            Iterable<? extends HourlyEvent> events = api.get().get();
                            for (HourlyEvent evt : events) {
                                ConsumptionPanel.this.events.add(evt);
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
                            JOptionPane.showMessageDialog(ConsumptionPanel.this,
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
