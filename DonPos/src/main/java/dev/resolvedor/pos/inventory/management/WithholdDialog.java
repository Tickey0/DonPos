package dev.resolvedor.pos.inventory.management;

import com.unicenta.data.gui.ComboBoxValModel;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.forms.DataLogicSystem;
import dev.joguenco.pos.taxpayer.DataLogicTaxpayer;
import dev.joguenco.pos.taxpayer.TaxpayerInfo;
import dev.joguenco.pos.ticketsnum.TicketsNumInfo;
import dev.joguenco.pos.ticketsnumpurchase.DataLogicTicketsNumPurchase;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * Dialogo para emitir el comprobante de retencion de una compra.
 *
 * Se abre desde PurchaseEditor sobre una compra YA GUARDADA: withholds.purchase_id
 * es una clave foranea a purchases.id, asi que la compra tiene que existir antes.
 *
 * La parte visual vive en WithholdDialog.form y se edita con el disenador de
 * NetBeans. No tocar a mano el bloque initComponents ni las marcas //GEN-.
 */
public class WithholdDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    private int returnStatus = RET_CANCEL;

    private final AppView app;
    private DataLogicWithhold dlWithhold;
    private DataLogicTaxpayer dlTaxPayer;
    private DataLogicSystem dlSystem;

    private WithholdInfo withhold = new WithholdInfo();
    private boolean editing = false;
    private boolean readOnly = false;

    private final Double subtotal;
    private final Double iva;

    private ComboBoxValModel modelWithholdTax;
    private final LinesTableModel linesModel = new LinesTableModel();

    public WithholdDialog(AppView app, java.awt.Frame parent, boolean modal,
            PurchaseInfo purchase, String supplierName, Double subtotal, Double iva) {
        super(parent, modal);

        this.app = app;
        this.subtotal = subtotal;
        this.iva = iva;

        initComponents();

        setTitle(AppLocal.getIntString("label.withhold"));
        jTableLines.setModel(linesModel);
        jTableLines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        dlWithhold = (DataLogicWithhold) app.getBean(
                "dev.resolvedor.pos.inventory.management.DataLogicWithhold");
        dlTaxPayer = (DataLogicTaxpayer) app.getBean(
                "dev.joguenco.pos.taxpayer.DataLogicTaxpayer");
        dlSystem = (DataLogicSystem) app.getBean(
                "com.unicenta.pos.forms.DataLogicSystem");

        loadWithholdTaxes();
        loadPurchase(purchase, supplierName);
        loadExistingWithhold();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public WithholdInfo getWithhold() {
        return withhold;
    }

    // -----------------------------------------------------------------------
    // Carga
    // -----------------------------------------------------------------------
    private void loadWithholdTaxes() {
        try {
            modelWithholdTax = new ComboBoxValModel(dlWithhold.getWithholdTaxList().list());
            cboWithholdTax.setModel(modelWithholdTax);
            modelWithholdTax.setSelectedKey(null);
        } catch (Exception e) {
            showError("message.cannotloaddata", e);
        }
    }

    private void loadPurchase(PurchaseInfo purchase, String supplierName) {
        withhold.setPurchaseId(purchase.getId());
        withhold.setUser(app.getAppUserView().getUser().getUserInfo());

        txtSupplier.setText(supplierName);
        txtDocument.setText(purchase.getPurchaseDocument() + "  "
                + purchase.getPurchaseReference());
        txtSubtotal.setText(Formats.CURRENCY.formatValue(subtotal));
        txtIva.setText(Formats.CURRENCY.formatValue(iva));

        var today = new Date();
        withhold.setDateWithhold(today);
        withhold.setFiscalPeriod(purchase.getPurchaseDate() == null
                ? today : purchase.getPurchaseDate());

        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(withhold.getDateWithhold()));
        txtFiscalPeriod.setText(new SimpleDateFormat("MM/yyyy").format(withhold.getFiscalPeriod()));
    }

    /**
     * Si la compra ya tiene retencion la carga y decide el modo.
     *
     * El criterio para bloquear NO es "ya existe" sino "el SRI la autorizo":
     * una devuelta o una que nunca se envio si se puede corregir, y si no se
     * pudiera, esa compra quedaria sin retencion valida para siempre.
     */
    private void loadExistingWithhold() {
        try {
            var existing = dlWithhold.getByPurchase(withhold.getPurchaseId());

            if (existing == null) {
                lblStatus.setText(AppLocal.getIntString("label.withhold.notcreated"));
                lblStatus.setForeground(java.awt.Color.GRAY);
                return;
            }

            editing = true;
            existing.setUser(withhold.getUser());
            existing.setLines(dlWithhold.getLinesByWithhold(existing.getId()));
            withhold = existing;

            txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(withhold.getDateWithhold()));
            txtFiscalPeriod.setText(new SimpleDateFormat("MM/yyyy").format(withhold.getFiscalPeriod()));
            txtObservation.setText(withhold.getObservation());

            for (WithholdLineInfo line : withhold.getLines()) {
                linesModel.addLine(line);
            }
            refreshTotal();

            var status = dlWithhold.getDocumentStatus(withhold.getCode(), withhold.getSerieNumber());

            setTitle(AppLocal.getIntString("label.withhold") + "  " + withhold.getSerieNumber());

            if ("AUTORIZADO".equalsIgnoreCase(status)) {
                lblStatus.setText(AppLocal.getIntString("label.withhold.created")
                        + " - " + withhold.getSerieNumber()
                        + " - " + AppLocal.getIntString("label.authorized"));
                lblStatus.setForeground(new java.awt.Color(0, 128, 0));
                setReadOnly(true);
            } else {
                lblStatus.setText(AppLocal.getIntString("label.withhold.created")
                        + " - " + withhold.getSerieNumber()
                        + " - " + (status == null
                                ? AppLocal.getIntString("label.notsent") : status));
                // Naranja, no gris: creada pero sin enviar es una tarea pendiente,
                // hay que ir a RoQui a autorizarla.
                lblStatus.setForeground(new java.awt.Color(196, 98, 0));
            }
        } catch (Exception e) {
            showError("message.cannotloaddata", e);
        }
    }

    /**
     * Bloquea todo el formulario. Una retencion AUTORIZADA no se toca: ya esta
     * en los servidores del SRI y el proveedor la puede usar como credito
     * tributario. Corregirla se hace anulando y emitiendo otra, no editando.
     */
    private void setReadOnly(boolean value) {
        readOnly = value;

        txtDate.setEditable(!value);
        txtFiscalPeriod.setEditable(!value);
        txtObservation.setEditable(!value);
        txtBase.setEditable(!value);
        txtValue.setEditable(!value);

        cboWithholdTax.setEnabled(!value);
        cmdAdd.setEnabled(!value);
        cmdRemove.setEnabled(!value);
        cmdSave.setVisible(!value);
    }

    // -----------------------------------------------------------------------
    // Apoyo
    // -----------------------------------------------------------------------
    private void recalculateValue() {
        var tax = getSelectedTax();
        if (tax == null) {
            return;
        }

        var base = parseDouble(txtBase.getText());
        txtValue.setText(Formats.CURRENCY.formatValue(
                round(base * tax.getPercentage() / 100)));
    }

    private boolean validateForm() {
        if (linesModel.getRowCount() == 0) {
            showMessage("message.withhold.nolines");
            return false;
        }

        for (WithholdLineInfo line : linesModel.getLines()) {
            if (line.getBaseValue() == null || line.getBaseValue() <= 0) {
                showMessage("message.withhold.invalidbase");
                return false;
            }
        }

        return true;
    }

    private WithholdTaxInfo getSelectedTax() {
        return modelWithholdTax == null
                ? null
                : (WithholdTaxInfo) modelWithholdTax.getSelectedItem();
    }

    private void refreshTotal() {
        var total = linesModel.getLines().stream()
                .mapToDouble(WithholdLineInfo::getWithholdedValue)
                .sum();

        lblTotalWithheld.setText(Formats.CURRENCY.formatValue(round(total)));
    }

    private Double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }

        try {
            return ((Number) Formats.CURRENCY.parseValue(text)).doubleValue();
        } catch (Exception e) {
            try {
                return Double.parseDouble(text.trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                return 0.0;
            }
        }
    }

    private Date parseDate(String text, String pattern, Date fallback) {
        try {
            return new SimpleDateFormat(pattern).parse(text.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private Double round(Double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void showMessage(String key) {
        JOptionPane.showMessageDialog(this,
                AppLocal.getIntString(key),
                AppLocal.getIntString("label.withhold"),
                JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String key, Exception e) {
        JOptionPane.showMessageDialog(this,
                AppLocal.getIntString(key) + "\n" + e.getMessage(),
                AppLocal.getIntString("label.withhold"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTop = new javax.swing.JPanel();
        panelStatus = new javax.swing.JPanel();
        lblStatusTitle = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        panelPurchase = new javax.swing.JPanel();
        lblSupplierTitle = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        lblDocumentTitle = new javax.swing.JLabel();
        txtDocument = new javax.swing.JTextField();
        lblSubtotalTitle = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        lblIvaTitle = new javax.swing.JLabel();
        txtIva = new javax.swing.JTextField();
        panelDates = new javax.swing.JPanel();
        lblDateTitle = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        lblFiscalPeriodTitle = new javax.swing.JLabel();
        txtFiscalPeriod = new javax.swing.JTextField();
        lblObservationTitle = new javax.swing.JLabel();
        txtObservation = new javax.swing.JTextField();
        panelType = new javax.swing.JPanel();
        lblWithholdTypeTitle = new javax.swing.JLabel();
        cboWithholdTax = new javax.swing.JComboBox();
        panelValues = new javax.swing.JPanel();
        lblBaseTitle = new javax.swing.JLabel();
        txtBase = new javax.swing.JTextField();
        lblPercentageTitle = new javax.swing.JLabel();
        txtPercentage = new javax.swing.JTextField();
        lblValueTitle = new javax.swing.JLabel();
        txtValue = new javax.swing.JTextField();
        cmdAdd = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLines = new javax.swing.JTable();
        panelBottom = new javax.swing.JPanel();
        panelTotal = new javax.swing.JPanel();
        lblTotalWithheldTitle = new javax.swing.JLabel();
        lblTotalWithheld = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        cmdSave = new javax.swing.JButton();
        cmdClose = new javax.swing.JButton();

        panelTop.setLayout(new java.awt.GridLayout(5, 1));

        panelStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        lblStatusTitle.setText(bundle.getString("label.Status")); // NOI18N
        panelStatus.add(lblStatusTitle);

        lblStatus.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblStatus.setText(" ");
        panelStatus.add(lblStatus);

        panelTop.add(panelStatus);

        panelPurchase.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblSupplierTitle.setText(bundle.getString("label.supplier")); // NOI18N
        panelPurchase.add(lblSupplierTitle);

        txtSupplier.setColumns(22);
        txtSupplier.setEditable(false);
        panelPurchase.add(txtSupplier);

        lblDocumentTitle.setText(bundle.getString("label.Number")); // NOI18N
        panelPurchase.add(lblDocumentTitle);

        txtDocument.setColumns(18);
        txtDocument.setEditable(false);
        panelPurchase.add(txtDocument);

        lblSubtotalTitle.setText(bundle.getString("label.subtotalcash")); // NOI18N
        panelPurchase.add(lblSubtotalTitle);

        txtSubtotal.setColumns(9);
        txtSubtotal.setEditable(false);
        panelPurchase.add(txtSubtotal);

        lblIvaTitle.setText(bundle.getString("label.taxes")); // NOI18N
        panelPurchase.add(lblIvaTitle);

        txtIva.setColumns(9);
        txtIva.setEditable(false);
        panelPurchase.add(txtIva);

        panelTop.add(panelPurchase);

        panelDates.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblDateTitle.setText(bundle.getString("label.date")); // NOI18N
        panelDates.add(lblDateTitle);

        txtDate.setColumns(10);
        panelDates.add(txtDate);

        lblFiscalPeriodTitle.setText(bundle.getString("label.fiscal.period")); // NOI18N
        panelDates.add(lblFiscalPeriodTitle);

        txtFiscalPeriod.setColumns(8);
        panelDates.add(txtFiscalPeriod);

        lblObservationTitle.setText(bundle.getString("label.observation")); // NOI18N
        panelDates.add(lblObservationTitle);

        txtObservation.setColumns(24);
        panelDates.add(txtObservation);

        panelTop.add(panelDates);

        panelType.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblWithholdTypeTitle.setText(bundle.getString("label.withhold.type")); // NOI18N
        panelType.add(lblWithholdTypeTitle);

        cboWithholdTax.setPreferredSize(new java.awt.Dimension(380, 26));
        cboWithholdTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboWithholdTaxActionPerformed(evt);
            }
        });
        panelType.add(cboWithholdTax);

        panelTop.add(panelType);

        panelValues.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblBaseTitle.setText(bundle.getString("label.base")); // NOI18N
        panelValues.add(lblBaseTitle);

        txtBase.setColumns(9);
        txtBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBaseActionPerformed(evt);
            }
        });
        txtBase.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBaseFocusLost(evt);
            }
        });
        panelValues.add(txtBase);

        lblPercentageTitle.setText("%");
        panelValues.add(lblPercentageTitle);

        txtPercentage.setColumns(5);
        txtPercentage.setEditable(false);
        panelValues.add(txtPercentage);

        lblValueTitle.setText(bundle.getString("label.value")); // NOI18N
        panelValues.add(lblValueTitle);

        txtValue.setColumns(9);
        panelValues.add(txtValue);

        cmdAdd.setText(bundle.getString("Button.Add")); // NOI18N
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });
        panelValues.add(cmdAdd);

        cmdRemove.setText(bundle.getString("Button.Remove")); // NOI18N
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });
        panelValues.add(cmdRemove);

        panelTop.add(panelValues);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        jTableLines.setPreferredSize(new java.awt.Dimension(740, 200));
        jScrollPane1.setViewportView(jTableLines);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBottom.setLayout(new java.awt.BorderLayout());

        panelTotal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblTotalWithheldTitle.setText(bundle.getString("label.total.withheld")); // NOI18N
        panelTotal.add(lblTotalWithheldTitle);

        lblTotalWithheld.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTotalWithheld.setText("0.00");
        panelTotal.add(lblTotalWithheld);

        panelBottom.add(panelTotal, java.awt.BorderLayout.WEST);

        panelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cmdSave.setText(bundle.getString("Button.Save")); // NOI18N
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });
        panelButtons.add(cmdSave);

        cmdClose.setText(bundle.getString("Button.Close")); // NOI18N
        cmdClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCloseActionPerformed(evt);
            }
        });
        panelButtons.add(cmdClose);

        panelBottom.add(panelButtons, java.awt.BorderLayout.EAST);

        getContentPane().add(panelBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Al elegir el tipo se rellenan porcentaje y base. La base depende de
     * tax_type: RENTA se calcula sobre el subtotal, IVA sobre el valor del IVA.
     */
    private void cboWithholdTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboWithholdTaxActionPerformed
        var tax = getSelectedTax();
        if (tax == null) {
            txtPercentage.setText("");
            txtBase.setText("");
            txtValue.setText("");
            return;
        }

        txtPercentage.setText(Formats.DOUBLE.formatValue(tax.getPercentage()));
        txtBase.setText(Formats.CURRENCY.formatValue(
                "IVA".equals(tax.getTaxType()) ? iva : subtotal));

        recalculateValue();
    }//GEN-LAST:event_cboWithholdTaxActionPerformed

    private void txtBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBaseActionPerformed
        recalculateValue();
    }//GEN-LAST:event_txtBaseActionPerformed

    private void txtBaseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFocusLost
        recalculateValue();
    }//GEN-LAST:event_txtBaseFocusLost

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        var tax = getSelectedTax();
        if (tax == null) {
            showMessage("message.withhold.selecttype");
            return;
        }

        if (linesModel.containsTax(tax.getId())) {
            showMessage("message.withhold.duplicatetype");
            return;
        }

        var line = new WithholdLineInfo();
        line.setWithholdTax(tax);
        line.setBaseValue(parseDouble(txtBase.getText()));
        line.setWithholdedValue(parseDouble(txtValue.getText()));

        linesModel.addLine(line);
        refreshTotal();

        modelWithholdTax.setSelectedKey(null);
        txtBase.setText("");
        txtPercentage.setText("");
        txtValue.setText("");
    }//GEN-LAST:event_cmdAddActionPerformed

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        var index = jTableLines.getSelectedRow();
        if (index < 0) {
            return;
        }

        linesModel.removeLine(index);
        refreshTotal();
    }//GEN-LAST:event_cmdRemoveActionPerformed

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        if (readOnly) {
            showMessage("message.withhold.cannotedit");
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            var user = withhold.getUser();

            var dlTicketsNum = (DataLogicTicketsNumPurchase) app.getBean(
                    "dev.joguenco.pos.ticketsnumpurchase.DataLogicTicketsNumPurchase");

            var ticketNum = (TicketsNumInfo) dlTicketsNum
                    .getSerial()
                    .find(user.getId(), withhold.getCode(), "primary");

            if (ticketNum == null) {
                showMessage("message.withhold.noserie");
                return;
            }

            withhold.setSerie(ticketNum.getSerie());
            withhold.setFormatNumberDigits(dlSystem.getResourceAsText("FormatTicket.NumberDigits"));
            withhold.setTaxPayerInfo((TaxpayerInfo) dlTaxPayer.getTaxPayerInfo().find("1"));
            withhold.setEnvironment(dlSystem.getResourceAsText("Electronic.Environment"));

            withhold.setDateWithhold(parseDate(txtDate.getText(), "dd-MM-yyyy",
                    withhold.getDateWithhold()));
            withhold.setFiscalPeriod(parseDate(txtFiscalPeriod.getText(), "MM/yyyy",
                    withhold.getFiscalPeriod()));
            withhold.setObservation(txtObservation.getText());
            withhold.setLines(linesModel.getLines());

            // Corregir conserva el numero: pedir otro al contador dejaria un
            // hueco en la secuencia que hay que justificar ante el SRI.
            var serieNumber = editing
                    ? dlWithhold.updateWithhold(withhold)
                    : dlWithhold.saveWithhold(withhold);

            JOptionPane.showMessageDialog(this,
                    AppLocal.getIntString("label.withhold") + ": " + serieNumber,
                    AppLocal.getIntString("label.withhold"),
                    JOptionPane.INFORMATION_MESSAGE);

            returnStatus = RET_OK;
            dispose();
        } catch (Exception e) {
            showError("message.cannotsavedata", e);
        }
    }//GEN-LAST:event_cmdSaveActionPerformed

    private void cmdCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCloseActionPerformed
        returnStatus = RET_CANCEL;
        dispose();
    }//GEN-LAST:event_cmdCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboWithholdTax;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdClose;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JButton cmdSave;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableLines;
    private javax.swing.JLabel lblBaseTitle;
    private javax.swing.JLabel lblDateTitle;
    private javax.swing.JTextField txtDocument;
    private javax.swing.JLabel lblDocumentTitle;
    private javax.swing.JLabel lblFiscalPeriodTitle;
    private javax.swing.JTextField txtIva;
    private javax.swing.JLabel lblIvaTitle;
    private javax.swing.JLabel lblObservationTitle;
    private javax.swing.JLabel lblPercentageTitle;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatusTitle;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JLabel lblSubtotalTitle;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JLabel lblSupplierTitle;
    private javax.swing.JLabel lblTotalWithheld;
    private javax.swing.JLabel lblTotalWithheldTitle;
    private javax.swing.JLabel lblValueTitle;
    private javax.swing.JLabel lblWithholdTypeTitle;
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelDates;
    private javax.swing.JPanel panelPurchase;
    private javax.swing.JPanel panelStatus;
    private javax.swing.JPanel panelTop;
    private javax.swing.JPanel panelTotal;
    private javax.swing.JPanel panelType;
    private javax.swing.JPanel panelValues;
    private javax.swing.JTextField txtBase;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtFiscalPeriod;
    private javax.swing.JTextField txtObservation;
    private javax.swing.JTextField txtPercentage;
    private javax.swing.JTextField txtValue;
    // End of variables declaration//GEN-END:variables

    // -----------------------------------------------------------------------
    // Modelo de la grilla
    // -----------------------------------------------------------------------
    private class LinesTableModel extends AbstractTableModel {

        private final List<WithholdLineInfo> rows = new ArrayList<>();

        private final String[] columns = {
            AppLocal.getIntString("label.withhold.type"),
            AppLocal.getIntString("label.code"),
            AppLocal.getIntString("label.base"),
            "%",
            AppLocal.getIntString("label.value")
        };

        public List<WithholdLineInfo> getLines() {
            return rows;
        }

        public void addLine(WithholdLineInfo line) {
            rows.add(line);
            fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
        }

        public void removeLine(int index) {
            rows.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public boolean containsTax(String withholdTaxesId) {
            return rows.stream()
                    .anyMatch(l -> withholdTaxesId.equals(l.getWithholdTaxesId()));
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
        public Object getValueAt(int row, int column) {
            var line = rows.get(row);

            switch (column) {
                case 0:
                    return line.getWithholdTaxName();
                case 1:
                    return line.getWithholdTaxCode();
                case 2:
                    return Formats.CURRENCY.formatValue(line.getBaseValue());
                case 3:
                    return Formats.DOUBLE.formatValue(line.getPercentage());
                case 4:
                    return Formats.CURRENCY.formatValue(line.getWithholdedValue());
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
