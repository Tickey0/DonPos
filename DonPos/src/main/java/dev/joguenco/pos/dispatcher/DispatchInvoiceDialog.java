package dev.joguenco.pos.dispatcher;

import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * Selector de facturas del dia para armar una guia de remision.
 *
 * La lista es siempre la de hoy y solo trae lo que falta despachar: lo que ya
 * tiene guia no aparece. La caja de arriba no cambia de fecha, filtra dentro
 * de esa lista por numero, cliente o RUC, que es lo util cuando el dia trae
 * cien facturas.
 *
 * El visto funciona en los dos sentidos: las que ya estan en la guia abren
 * marcadas, y quitarles el visto las saca. Por eso al aceptar se devuelve la
 * lista completa de lo marcado, no solo lo nuevo, y la pantalla reemplaza su
 * grilla con eso.
 */
public class DispatchInvoiceDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    private int returnStatus = RET_CANCEL;

    private final transient DataLogicDispatch dlDispatch;
    private final InvoicesTableModel invoicesModel = new InvoicesTableModel();

    /** Las que ya estan en la guia que se arma en pantalla. */
    private final transient List<String> yaEnLaGuia;

    public DispatchInvoiceDialog(AppView app, java.awt.Frame parent, boolean modal,
            List<String> yaAgregadas) {
        super(parent, modal);

        dlDispatch = (DataLogicDispatch) app.getBean(
                "dev.joguenco.pos.dispatcher.DataLogicDispatch");

        this.yaEnLaGuia = yaAgregadas;

        initComponents();
        initBehaviour();
        loadInvoices();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * La lista final: todo lo que quedo marcado.
     *
     * No son "las que se agregaron" sino "las que van en la guia", porque el
     * usuario tambien pudo desmarcar alguna que ya estaba.
     */
    public List<DispatchLineInfo> getSelectedInvoices() {
        return invoicesModel.getSelected();
    }


    /**
     * Modelo, anchos de columna y listeners: fuera de initComponents() para
     * que el disenador no los borre al regenerar el formulario.
     */
    private void initBehaviour() {
        setTitle(AppLocal.getIntString("label.dispatch.selectinvoices"));
        setPreferredSize(new Dimension(880, 440));

        // Filtra mientras se escribe: no hay que darle a ningun boton
        txtFilter.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltro();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltro();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltro();
            }
        });

        jTableInvoices.setModel(invoicesModel);
        jTableInvoices.setRowHeight(26);

        DispatchTables.alinear(jTableInvoices, CENTER, LEFT, LEFT, LEFT, RIGHT);

        TableColumn marca = jTableInvoices.getColumnModel().getColumn(0);
        marca.setPreferredWidth(30);
        marca.setMaxWidth(30);
        jTableInvoices.getColumnModel().getColumn(1).setPreferredWidth(140);
        jTableInvoices.getColumnModel().getColumn(2).setPreferredWidth(220);
        jTableInvoices.getColumnModel().getColumn(3).setPreferredWidth(120);
        jTableInvoices.getColumnModel().getColumn(4).setPreferredWidth(110);

        cmdAccept.addActionListener(evt -> cmdAcceptActionPerformed());
        cmdCancel.addActionListener(evt -> cmdCancelActionPerformed());

        pack();
        setLocationRelativeTo(getParent());
    }

    private void loadInvoices() {
        try {
            invoicesModel.setRows(dlDispatch.getInvoicesOfToday(), yaEnLaGuia);
            refreshCount();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    AppLocal.getIntString("message.cannotloaddata") + "\n" + e.getMessage(),
                    AppLocal.getIntString("label.dispatch"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltro() {
        invoicesModel.setFilter(txtFilter.getText());
        refreshCount();
    }

    private void refreshCount() {
        lblCount.setText(AppLocal.getIntString("label.dispatch.available")
                + ": " + invoicesModel.countAvailable()
                + "    " + AppLocal.getIntString("label.dispatch.selected")
                + ": " + invoicesModel.getSelected().size());
    }

    /**
     * Aceptar con nada marcado es valido: significa vaciar la guia, que es lo
     * que pasa si el usuario le quita el visto a todo. La regla de "al menos
     * una factura" vive en Guardar, que es donde de verdad importa.
     */
    private void cmdAcceptActionPerformed() {
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
    private class InvoicesTableModel extends AbstractTableModel {

        /**
         * Una fila con su estado. El estado viaja pegado a la factura y no en
         * listas paralelas: al filtrar, los indices se mueven y unas listas
         * por indice terminarian marcando la factura equivocada.
         */
        private class Fila {

            private final DispatchLineInfo factura;
            private boolean marcada;

            Fila(DispatchLineInfo factura, boolean marcada) {
                this.factura = factura;
                this.marcada = marcada;
            }

            /**
             * Texto donde busca el filtro: numero, cliente y RUC juntos.
             */
            boolean coincide(String texto) {
                var todo = (factura.getReferenceCode() + " "
                        + factura.getReferenceNumber() + " "
                        + (factura.getCustomerName() == null ? "" : factura.getCustomerName())
                        + " "
                        + (factura.getCustomerTaxId() == null ? "" : factura.getCustomerTaxId()))
                        .toLowerCase();

                return todo.contains(texto);
            }
        }

        private final List<Fila> todas = new ArrayList<>();
        private final List<Fila> visibles = new ArrayList<>();

        private final String[] columns = {
            "",
            AppLocal.getIntString("label.Number"),
            AppLocal.getIntString("label.customer"),
            AppLocal.getIntString("label.suppliertaxid"),
            AppLocal.getIntString("label.totalcash")
        };

        /**
         * Las que ya estan en la guia entran con el visto puesto; el usuario
         * puede quitarselo para sacarlas.
         */
        public void setRows(List<DispatchLineInfo> facturas, List<String> yaEnLaGuia) {
            todas.clear();

            for (DispatchLineInfo factura : facturas) {
                todas.add(new Fila(factura,
                        yaEnLaGuia.contains(factura.getReferenceNumber())));
            }

            setFilter(null);
        }

        /**
         * Deja visibles solo las que coinciden. Lo marcado no se pierde: vive
         * en la fila, no en la vista.
         */
        public void setFilter(String texto) {
            var busca = texto == null ? "" : texto.trim().toLowerCase();

            visibles.clear();
            for (Fila fila : todas) {
                if (busca.isEmpty() || fila.coincide(busca)) {
                    visibles.add(fila);
                }
            }

            fireTableDataChanged();
        }

        public long countAvailable() {
            return todas.size();
        }

        /**
         * Todo lo marcado, este o no desde antes. Es la guia completa.
         */
        public List<DispatchLineInfo> getSelected() {
            var seleccionadas = new ArrayList<DispatchLineInfo>();
            for (Fila fila : todas) {
                if (fila.marcada) {
                    seleccionadas.add(fila.factura);
                }
            }
            return seleccionadas;
        }

        @Override
        public int getRowCount() {
            return visibles.size();
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
        public Class<?> getColumnClass(int column) {
            return column == 0 ? Boolean.class : String.class;
        }

        /**
         * El visto se pone y se quita libremente: quitarlo saca la factura de
         * la guia, como un Quitar desde aqui mismo.
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 0) {
                visibles.get(row).marcada = Boolean.TRUE.equals(value);
                refreshCount();
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            var fila = visibles.get(row);
            var factura = fila.factura;

            switch (column) {
                case 0:
                    return fila.marcada;
                case 1:
                    return factura.getReferenceCode() + " " + factura.getReferenceNumber();
                case 2:
                    return factura.getCustomerName();
                case 3:
                    return factura.getCustomerTaxId();
                case 4:
                    return Formats.CURRENCY.formatValue(factura.getTotal());
                default:
                    return null;
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTop = new javax.swing.JPanel();
        lblFilter = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableInvoices = new javax.swing.JTable();
        panelBottom = new javax.swing.JPanel();
        panelCount = new javax.swing.JPanel();
        lblCount = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        cmdAccept = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();

        panelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblFilter.setText(AppLocal.getIntString("label.search")); // NOI18N
        lblFilter.setPreferredSize(new java.awt.Dimension(170, 30));
        panelTop.add(lblFilter);

        txtFilter.setPreferredSize(new java.awt.Dimension(320, 30));
        panelTop.add(txtFilter);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(jTableInvoices);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBottom.setLayout(new java.awt.BorderLayout());

        panelCount.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblCount.setText(" ");
        panelCount.add(lblCount);

        panelBottom.add(panelCount, java.awt.BorderLayout.WEST);

        panelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 4));

        cmdAccept.setText(AppLocal.getIntString("Button.Accept")); // NOI18N
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableInvoices;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelCount;
    private javax.swing.JPanel panelTop;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
