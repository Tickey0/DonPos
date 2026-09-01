package dev.joguenco.pos.dispatcher;

import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * Buscador de guias de remision ya emitidas.
 *
 * Abre con la fecha de hoy porque es lo que se consulta el noventa por ciento
 * de las veces, pero deja cambiarla para revisar guias de otros dias. Al
 * aceptar devuelve la guia elegida; el editor la carga en modo lectura.
 */
public class DispatchSearchDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    private int returnStatus = RET_CANCEL;

    private final transient DataLogicDispatch dlDispatch;
    private final DispatchesTableModel dispatchesModel = new DispatchesTableModel();

    public DispatchSearchDialog(AppView app, java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        dlDispatch = (DataLogicDispatch) app.getBean(
                "dev.joguenco.pos.dispatcher.DataLogicDispatch");

        initComponents();
        initBehaviour();
        search();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * La guia marcada en la grilla, o null si no hay ninguna.
     */
    public DispatchInfo getSelectedDispatch() {
        var fila = jTableDispatches.getSelectedRow();
        return fila < 0 ? null : dispatchesModel.getAt(fila);
    }

    /**
     * Modelo, listeners y tamanos: fuera de initComponents() para que el
     * disenador no los borre al regenerar el formulario.
     */
    private void initBehaviour() {
        setTitle(AppLocal.getIntString("label.dispatch.search"));
        setPreferredSize(new Dimension(820, 420));

        txtDate.setText(Formats.SIMPLEDATE.formatValue(new Date()));

        jTableDispatches.setModel(dispatchesModel);
        jTableDispatches.setRowHeight(26);
        jTableDispatches.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DispatchTables.alinear(jTableDispatches, LEFT, LEFT, LEFT, LEFT, RIGHT);

        jTableDispatches.getColumnModel().getColumn(0).setPreferredWidth(130);
        jTableDispatches.getColumnModel().getColumn(1).setPreferredWidth(90);
        jTableDispatches.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTableDispatches.getColumnModel().getColumn(3).setPreferredWidth(260);
        jTableDispatches.getColumnModel().getColumn(4).setPreferredWidth(70);

        cmdSearch.addActionListener(evt -> search());
        cmdAccept.addActionListener(evt -> cmdAcceptActionPerformed());
        cmdCancel.addActionListener(evt -> cmdCancelActionPerformed());

        // Enter en la fecha busca, sin obligar a ir al boton con el mouse
        txtDate.addActionListener(evt -> search());

        pack();
        setLocationRelativeTo(getParent());
    }

    private void search() {
        try {
            dispatchesModel.setRows(dlDispatch.getDispatchesByDate(
                    parseDate(txtDate.getText())));

            lblCount.setText(AppLocal.getIntString("label.dispatch.found")
                    + ": " + dispatchesModel.getRowCount());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    AppLocal.getIntString("message.cannotloaddata") + "\n" + e.getMessage(),
                    AppLocal.getIntString("label.dispatch"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Si la fecha escrita no se entiende se usa hoy, en vez de reventar.
     */
    private Date parseDate(String texto) {
        try {
            return (Date) Formats.SIMPLEDATE.parseValue(texto.trim());
        } catch (Exception e) {
            var hoy = new Date();
            txtDate.setText(Formats.SIMPLEDATE.formatValue(hoy));
            return hoy;
        }
    }

    private void cmdAcceptActionPerformed() {
        if (getSelectedDispatch() == null) {
            JOptionPane.showMessageDialog(this,
                    AppLocal.getIntString("message.dispatch.nodispatchselected"),
                    AppLocal.getIntString("label.dispatch"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        returnStatus = RET_OK;
        dispose();
    }

    private void cmdCancelActionPerformed() {
        returnStatus = RET_CANCEL;
        dispose();
    }

    // -----------------------------------------------------------------------
    // Modelo de la grilla
    // -----------------------------------------------------------------------
    private class DispatchesTableModel extends AbstractTableModel {

        private final List<DispatchInfo> rows = new ArrayList<>();

        private final String[] columns = {
            AppLocal.getIntString("label.Number"),
            AppLocal.getIntString("label.date"),
            AppLocal.getIntString("Menu.Dispatcher"),
            AppLocal.getIntString("label.dispatch.addressstart"),
            AppLocal.getIntString("label.dispatch.invoices")
        };

        public DispatchInfo getAt(int row) {
            return rows.get(row);
        }

        public void setRows(List<DispatchInfo> nuevas) {
            rows.clear();
            rows.addAll(nuevas);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Object getValueAt(int row, int column) {
            var guia = rows.get(row);

            switch (column) {
                case 0:
                    return guia.getSerieNumber();
                case 1:
                    return guia.getDateDispatch() == null ? ""
                            : Formats.SIMPLEDATE.formatValue(guia.getDateDispatch());
                case 2:
                    return guia.getDispatcherLabel();
                case 3:
                    return guia.getAddressStart();
                case 4:
                    return guia.getLineCount();
                default:
                    return null;
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTop = new javax.swing.JPanel();
        lblDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        cmdSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDispatches = new javax.swing.JTable();
        panelBottom = new javax.swing.JPanel();
        panelCount = new javax.swing.JPanel();
        lblCount = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        cmdAccept = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new java.awt.BorderLayout());

        panelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 2));
        lblDate.setText(AppLocal.getIntString("label.date")); // NOI18N
        lblDate.setPreferredSize(new java.awt.Dimension(170, 30));
        panelTop.add(lblDate);

        txtDate.setPreferredSize(new java.awt.Dimension(135, 30));
        panelTop.add(txtDate);

        cmdSearch.setText(AppLocal.getIntString("label.dispatch.search")); // NOI18N
        panelTop.add(cmdSearch);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(jTableDispatches);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBottom.setLayout(new java.awt.BorderLayout());
        panelCount.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 2));
        lblCount.setText(" ");
        panelCount.add(lblCount);

        panelBottom.add(panelCount, java.awt.BorderLayout.WEST);

        panelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 4));
        cmdAccept.setText(AppLocal.getIntString("label.dispatch.viewdata")); // NOI18N
        panelButtons.add(cmdAccept);

        cmdCancel.setText(AppLocal.getIntString("Button.Close2")); // NOI18N
        panelButtons.add(cmdCancel);

        panelBottom.add(panelButtons, java.awt.BorderLayout.EAST);

        getContentPane().add(panelBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAccept;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdSearch;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDispatches;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblDate;
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelCount;
    private javax.swing.JPanel panelTop;
    private javax.swing.JTextField txtDate;
    // End of variables declaration//GEN-END:variables
}
