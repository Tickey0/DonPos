package dev.joguenco.pos.taxes;

import com.unicenta.basic.BasicException;
import com.unicenta.data.user.DirtyManager;
import com.unicenta.data.user.EditorRecord;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppLocal;
import java.awt.Component;
import java.util.Date;
import java.util.UUID;
import javax.swing.JPanel;

/**
 * Formulario de un concepto de retencion del SRI.
 *
 * El tipo es un combo cerrado (IVA / RENTA) a proposito: la vista
 * v_ele_withholds_detail traduce con "when 'RENTA' then '1' when 'IVA' then '2'",
 * asi que un "Renta" o un "renta" escrito a mano romperia el XML.
 */
public class WithholdTaxEditor extends JPanel implements EditorRecord {

    private static final String TIPO_IVA = "IVA";
    private static final String TIPO_RENTA = "RENTA";

    private Object m_oId;

    public WithholdTaxEditor(DirtyManager dirty) {
        initComponents();

        cboTaxType.removeAllItems();
        cboTaxType.addItem(TIPO_RENTA);
        cboTaxType.addItem(TIPO_IVA);

        txtId.getDocument().addDocumentListener(dirty);
        txtName.getDocument().addDocumentListener(dirty);
        txtPercentage.getDocument().addDocumentListener(dirty);
        txtCode.getDocument().addDocumentListener(dirty);
        cboTaxType.addActionListener(dirty);
        txtCreatedAt.getDocument().addDocumentListener(dirty);
        chkStatus.addActionListener(dirty);

        writeValueEOF();
    }

    // -----------------------------------------------------------------------
    // EditorRecord
    // -----------------------------------------------------------------------
    @Override
    public void writeValueEOF() {
        m_oId = null;
        txtId.setText(null);
        txtName.setText(null);
        txtPercentage.setText(null);
        txtCode.setText(null);
        cboTaxType.setSelectedItem(TIPO_RENTA);
        txtCreatedAt.setText(null);
        chkStatus.setSelected(false);

        habilitar(false);
    }

    @Override
    public void writeValueInsert() {
        // Igual que TaxEditor: se prepara un UUID, pero si el usuario escribe
        // un id propio se respeta el suyo.
        m_oId = UUID.randomUUID().toString();
        txtId.setText(null);
        txtName.setText(null);
        txtPercentage.setText(null);
        txtCode.setText(null);
        cboTaxType.setSelectedItem(TIPO_RENTA);
        txtCreatedAt.setText(Formats.DATE.formatValue(new Date()));
        chkStatus.setSelected(true);

        habilitar(true);
    }

    @Override
    public void writeValueDelete(Object value) {
        cargar(value);
        habilitar(false);
    }

    @Override
    public void writeValueEdit(Object value) {
        cargar(value);
        habilitar(true);
        // El id no se cambia una vez creado: withholds_detail lo referencia.
        txtId.setEnabled(false);
    }

    @Override
    public Object createValue() throws BasicException {
        validar();

        Object[] concepto = new Object[7];

        concepto[0] = txtId.getText().isEmpty() ? m_oId : txtId.getText();
        concepto[1] = txtName.getText();
        concepto[2] = Formats.DOUBLE.parseValue(txtPercentage.getText());
        concepto[3] = txtCode.getText().isEmpty() ? null : txtCode.getText();
        concepto[4] = cboTaxType.getSelectedItem();
        concepto[5] = Formats.DATE.parseValue(txtCreatedAt.getText());
        concepto[6] = chkStatus.isSelected();

        return concepto;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
    }

    // -----------------------------------------------------------------------
    // Apoyo
    // -----------------------------------------------------------------------
    private void cargar(Object value) {
        Object[] concepto = (Object[]) value;

        m_oId = concepto[0];
        txtId.setText((String) concepto[0]);
        txtName.setText((String) concepto[1]);
        txtPercentage.setText(Formats.DOUBLE.formatValue(concepto[2]));
        txtCode.setText((String) concepto[3]);
        cboTaxType.setSelectedItem(concepto[4] == null ? TIPO_RENTA : concepto[4]);
        txtCreatedAt.setText(Formats.DATE.formatValue(concepto[5]));
        chkStatus.setSelected(Boolean.parseBoolean(Formats.BOOLEAN.formatValue(concepto[6])));
    }

    private void habilitar(boolean value) {
        txtId.setEnabled(value);
        txtName.setEnabled(value);
        txtPercentage.setEnabled(value);
        txtCode.setEnabled(value);
        cboTaxType.setEnabled(value);
        txtCreatedAt.setEnabled(value);
        chkStatus.setEnabled(value);
    }

    private void validar() throws BasicException {
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            throw new BasicException(AppLocal.getIntString("message.withholdtax.noname"));
        }

        try {
            var porcentaje = (Double) Formats.DOUBLE.parseValue(txtPercentage.getText());
            if (porcentaje == null || porcentaje < 0 || porcentaje > 100) {
                throw new BasicException(
                        AppLocal.getIntString("message.withholdtax.invalidpercentage"));
            }
        } catch (BasicException e) {
            throw e;
        } catch (Exception e) {
            throw new BasicException(
                    AppLocal.getIntString("message.withholdtax.invalidpercentage"));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFields = new javax.swing.JPanel();
        panelId = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        panelName = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        panelPercentage = new javax.swing.JPanel();
        lblPercentage = new javax.swing.JLabel();
        txtPercentage = new javax.swing.JTextField();
        panelCode = new javax.swing.JPanel();
        lblCode = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        panelTaxType = new javax.swing.JPanel();
        lblTaxType = new javax.swing.JLabel();
        cboTaxType = new javax.swing.JComboBox();
        panelCreatedAt = new javax.swing.JPanel();
        lblCreatedAt = new javax.swing.JLabel();
        txtCreatedAt = new javax.swing.JTextField();
        panelStatus = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        chkStatus = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        panelFields.setLayout(new java.awt.GridLayout(7, 1));

        panelId.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblId.setText("Identificador");
        lblId.setPreferredSize(new java.awt.Dimension(170, 30));
        panelId.add(lblId);

        txtId.setPreferredSize(new java.awt.Dimension(245, 30));
        panelId.add(txtId);

        panelFields.add(panelId);

        panelName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblName.setText(AppLocal.getIntString("label.name")); // NOI18N
        lblName.setPreferredSize(new java.awt.Dimension(170, 30));
        panelName.add(lblName);

        txtName.setPreferredSize(new java.awt.Dimension(245, 30));
        panelName.add(txtName);

        panelFields.add(panelName);

        panelPercentage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblPercentage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPercentage.setText(AppLocal.getIntString("label.percentage")); // NOI18N
        lblPercentage.setPreferredSize(new java.awt.Dimension(170, 30));
        panelPercentage.add(lblPercentage);

        txtPercentage.setPreferredSize(new java.awt.Dimension(70, 30));
        panelPercentage.add(txtPercentage);

        panelFields.add(panelPercentage);

        panelCode.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblCode.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCode.setText(AppLocal.getIntString("label.LegalCode")); // NOI18N
        lblCode.setPreferredSize(new java.awt.Dimension(170, 30));
        panelCode.add(lblCode);

        txtCode.setPreferredSize(new java.awt.Dimension(70, 30));
        panelCode.add(txtCode);

        panelFields.add(panelCode);

        panelTaxType.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblTaxType.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTaxType.setText(AppLocal.getIntString("label.withhold.taxtype")); // NOI18N
        lblTaxType.setPreferredSize(new java.awt.Dimension(170, 30));
        panelTaxType.add(lblTaxType);

        cboTaxType.setPreferredSize(new java.awt.Dimension(245, 30));
        panelTaxType.add(cboTaxType);

        panelFields.add(panelTaxType);

        panelCreatedAt.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblCreatedAt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCreatedAt.setText(AppLocal.getIntString("label.date")); // NOI18N
        lblCreatedAt.setPreferredSize(new java.awt.Dimension(170, 30));
        panelCreatedAt.add(lblCreatedAt);

        txtCreatedAt.setPreferredSize(new java.awt.Dimension(135, 30));
        panelCreatedAt.add(txtCreatedAt);

        panelFields.add(panelCreatedAt);

        panelStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStatus.setText(AppLocal.getIntString("label.Status")); // NOI18N
        lblStatus.setPreferredSize(new java.awt.Dimension(170, 30));
        panelStatus.add(lblStatus);

        chkStatus.setPreferredSize(new java.awt.Dimension(70, 30));
        panelStatus.add(chkStatus);

        panelFields.add(panelStatus);

        add(panelFields, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboTaxType;
    private javax.swing.JCheckBox chkStatus;
    private javax.swing.JLabel lblCode;
    private javax.swing.JLabel lblCreatedAt;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPercentage;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTaxType;
    private javax.swing.JPanel panelCode;
    private javax.swing.JPanel panelCreatedAt;
    private javax.swing.JPanel panelFields;
    private javax.swing.JPanel panelId;
    private javax.swing.JPanel panelName;
    private javax.swing.JPanel panelPercentage;
    private javax.swing.JPanel panelStatus;
    private javax.swing.JPanel panelTaxType;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtCreatedAt;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPercentage;
    // End of variables declaration//GEN-END:variables
}
