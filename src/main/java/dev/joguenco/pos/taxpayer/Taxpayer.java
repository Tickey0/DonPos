//    Mestizo Pos - Touch Friendly Point Of Sale
//    https://resolvedor.dev
//
//    Mestizo Pos is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Mestizo Pos is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Mestizo Pos.  If not, see <http://www.gnu.org/licenses/>.
package dev.joguenco.pos.taxpayer;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.TableDefinition;
import com.unicenta.pos.forms.AppConfig;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.forms.BeanFactoryApp;
import com.unicenta.pos.forms.BeanFactoryException;
import com.unicenta.pos.forms.JPanelView;
import dev.joguenco.error.ErrorMessage;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jorgeluis
 */
@Slf4j
public class Taxpayer extends JPanel implements JPanelView, BeanFactoryApp {

    private AppView app;
    private DataLogicTaxpayer dlTaxpayer;
    private TableDefinition tdTaxpayer;
    private String country;

    public Taxpayer() {
        initComponents();
        final var config = new AppConfig(new File((System.getProperty("user.home")), AppLocal.APP_ID + ".properties"));
        config.load();
        country = config.getProperty("user.country");
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Taxpayer");
    }

    @Override
    public void activate() throws BasicException {
        if ("EC".equals(this.country)) {
            ecLoadData();
            tabContainer.remove(panelAll);
        } else {            
            tabContainer.addTab(country, panelAll);
            allLoadData();
            tabContainer.remove(panelEcuador);
        }
    }

    private void ecLoadData() {
        try {
            TaxpayerInfo taxpayer = (TaxpayerInfo) this.dlTaxpayer.getTaxPayerInfo().find("1");

            txtEcIdentification.setText(taxpayer.getIdentification());
            txtEcLegalName.setText(taxpayer.getLegalName());

            if (taxpayer.getForcedAccounting().equals("SI")) {
                chkEcForcedAccounting.setText("SI");
                chkEcForcedAccounting.setSelected(true);
            } else {
                chkEcForcedAccounting.setText("NO");
                chkEcForcedAccounting.setSelected(false);
            }

            txtEcSpecialTaxPayer.setText(taxpayer.getSpecialTaxpayer());
            txtEcRetentionAgent.setText(taxpayer.getRetentionAgent());
            txtEcOther.setText(taxpayer.getOther());

        } catch (BasicException ex) {
            log.error(ex.getMessage());
        }

    }

    private void allLoadData() {
        try {
            TaxpayerInfo taxpayer = (TaxpayerInfo) this.dlTaxpayer.getTaxPayerInfo().find("1");

            txtAllIdentification.setText(taxpayer.getIdentification());
            txtAllLegalName.setText(taxpayer.getLegalName());

        } catch (BasicException ex) {
            log.error(ex.getMessage());
        }

    }

    @Override
    public boolean deactivate() {
        return true;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        this.app = app;
        this.dlTaxpayer = (DataLogicTaxpayer) this.app.getBean("dev.joguenco.pos.taxpayer.DataLogicTaxpayer");
        this.tdTaxpayer = this.dlTaxpayer.getTableTaxPayer();
    }

    @Override
    public Object getBean() {
        return this;
    }

    private Object createValue() {
        Object[] taxPayer = new Object[7];

        taxPayer[0] = 1;
        if ("EC".equals(this.country)) {
            taxPayer[1] = txtEcIdentification.getText();
            taxPayer[2] = txtEcLegalName.getText();
            taxPayer[3] = chkEcForcedAccounting.getText();
            taxPayer[4] = txtEcSpecialTaxPayer.getText().isEmpty() ? null : txtEcSpecialTaxPayer.getText();
            taxPayer[5] = txtEcRetentionAgent.getText().isEmpty() ? null : txtEcRetentionAgent.getText();
            taxPayer[6] = txtEcOther.getText().isEmpty() ? null : txtEcOther.getText();
        } else {
            taxPayer[1] = txtAllIdentification.getText();
            taxPayer[2] = txtAllLegalName.getText();
        }

        return taxPayer;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        tabContainer = new javax.swing.JTabbedPane();
        panelEcuador = new javax.swing.JPanel();
        jPanelInformation = new javax.swing.JPanel();
        lblIdentification = new javax.swing.JLabel();
        txtEcIdentification = new javax.swing.JTextField();
        lblLegalName = new javax.swing.JLabel();
        txtEcLegalName = new javax.swing.JTextField();
        jPanelData = new javax.swing.JPanel();
        lblForcedAccounting = new javax.swing.JLabel();
        chkEcForcedAccounting = new javax.swing.JCheckBox();
        lblSpecialTaxPayer = new javax.swing.JLabel();
        txtEcRetentionAgent = new javax.swing.JTextField();
        lblRetentionAgent = new javax.swing.JLabel();
        txtEcSpecialTaxPayer = new javax.swing.JTextField();
        lblOther = new javax.swing.JLabel();
        txtEcOther = new javax.swing.JTextField();
        panelAll = new javax.swing.JPanel();
        jPanelInformation1 = new javax.swing.JPanel();
        lblAllIdentification = new javax.swing.JLabel();
        txtAllIdentification = new javax.swing.JTextField();
        lblAllLegalName = new javax.swing.JLabel();
        txtAllLegalName = new javax.swing.JTextField();
        cmdOk = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(700, 510));

        jPanelMain.setName("jPanelMain"); // NOI18N

        jPanelInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblIdentification.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        lblIdentification.setText(bundle.getString("label.taxid")); // NOI18N

        txtEcIdentification.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        lblLegalName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblLegalName.setText(bundle.getString("label.namem")); // NOI18N

        txtEcLegalName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanelInformationLayout = new javax.swing.GroupLayout(jPanelInformation);
        jPanelInformation.setLayout(jPanelInformationLayout);
        jPanelInformationLayout.setHorizontalGroup(
            jPanelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInformationLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEcIdentification, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIdentification))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEcLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(243, Short.MAX_VALUE))
        );
        jPanelInformationLayout.setVerticalGroup(
            jPanelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIdentification)
                    .addComponent(lblLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEcIdentification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEcLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanelData.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblForcedAccounting.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblForcedAccounting.setText(bundle.getString("label.forcedAccounting")); // NOI18N

        chkEcForcedAccounting.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkEcForcedAccounting.setText(bundle.getString("label.yesNo")); // NOI18N
        chkEcForcedAccounting.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkEcForcedAccountingStateChanged(evt);
            }
        });

        lblSpecialTaxPayer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblSpecialTaxPayer.setText(bundle.getString("label.specialTaxpayer")); // NOI18N

        txtEcRetentionAgent.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        lblRetentionAgent.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblRetentionAgent.setText(bundle.getString("label.retentionAgent")); // NOI18N

        txtEcSpecialTaxPayer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        lblOther.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblOther.setText(bundle.getString("label.other")); // NOI18N

        txtEcOther.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanelDataLayout = new javax.swing.GroupLayout(jPanelData);
        jPanelData.setLayout(jPanelDataLayout);
        jPanelDataLayout.setHorizontalGroup(
            jPanelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEcOther, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOther)
                    .addComponent(lblForcedAccounting)
                    .addComponent(chkEcForcedAccounting)
                    .addComponent(lblSpecialTaxPayer)
                    .addComponent(txtEcSpecialTaxPayer, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEcRetentionAgent, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRetentionAgent))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelDataLayout.setVerticalGroup(
            jPanelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblForcedAccounting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEcForcedAccounting)
                .addGap(18, 18, 18)
                .addComponent(lblSpecialTaxPayer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEcSpecialTaxPayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblRetentionAgent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEcRetentionAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblOther)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEcOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelEcuadorLayout = new javax.swing.GroupLayout(panelEcuador);
        panelEcuador.setLayout(panelEcuadorLayout);
        panelEcuadorLayout.setHorizontalGroup(
            panelEcuadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEcuadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEcuadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelEcuadorLayout.setVerticalGroup(
            panelEcuadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEcuadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        tabContainer.addTab("EC", panelEcuador);

        jPanelInformation1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblAllIdentification.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblAllIdentification.setText(bundle.getString("label.taxid")); // NOI18N

        txtAllIdentification.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        lblAllLegalName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblAllLegalName.setText(bundle.getString("label.namem")); // NOI18N

        txtAllLegalName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanelInformation1Layout = new javax.swing.GroupLayout(jPanelInformation1);
        jPanelInformation1.setLayout(jPanelInformation1Layout);
        jPanelInformation1Layout.setHorizontalGroup(
            jPanelInformation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInformation1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanelInformation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAllIdentification, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAllIdentification))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelInformation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAllLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAllLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(243, Short.MAX_VALUE))
        );
        jPanelInformation1Layout.setVerticalGroup(
            jPanelInformation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInformation1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInformation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAllIdentification)
                    .addComponent(lblAllLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelInformation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAllIdentification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAllLegalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAllLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelInformation1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelAllLayout.setVerticalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAllLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelInformation1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(306, Short.MAX_VALUE))
        );

        tabContainer.addTab("ALL", panelAll);

        cmdOk.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cmdOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/unicenta/images/ok.png"))); // NOI18N
        cmdOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                .addGap(812, 812, 812)
                .addComponent(cmdOk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdOk, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 38, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkEcForcedAccountingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkEcForcedAccountingStateChanged
        if (chkEcForcedAccounting.isSelected()) {
            chkEcForcedAccounting.setText("SI");
        } else {
            chkEcForcedAccounting.setText("NO");
        }
    }//GEN-LAST:event_chkEcForcedAccountingStateChanged

    private void cmdOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOkActionPerformed
        ErrorMessage validate = validateData();

        if (validate.getIsError()) {
            JOptionPane.showMessageDialog(this, validate.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Object taxPayer = createValue();
            int status = this.tdTaxpayer.getUpdateSentence().exec(taxPayer);

            if (status > 0) {
                JOptionPane.showMessageDialog(this, "Se guardó con éxito",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se guardó con éxito",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (BasicException ex) {
            log.error(ex.getMessage());
        }
    }//GEN-LAST:event_cmdOkActionPerformed

    private ErrorMessage validateData() {

        if ("EC".equals(this.country) && txtEcIdentification.getText().trim().isEmpty()) {
            return new ErrorMessage(AppLocal.getIntString("message.identification"));
        }

        if ("EC".equals(this.country) && txtEcLegalName.getText().trim().isEmpty()) {
            return new ErrorMessage(AppLocal.getIntString("message.name"));
        }

        if ("EC".equals(this.country)) {
            if (txtEcIdentification.getText().length() != 13) {
                return new ErrorMessage("La identificación debe tener 13 dígitos");
            }
        }

        return new ErrorMessage();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkEcForcedAccounting;
    private javax.swing.JButton cmdOk;
    private javax.swing.JPanel jPanelData;
    private javax.swing.JPanel jPanelInformation;
    private javax.swing.JPanel jPanelInformation1;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JLabel lblAllIdentification;
    private javax.swing.JLabel lblAllLegalName;
    private javax.swing.JLabel lblForcedAccounting;
    private javax.swing.JLabel lblIdentification;
    private javax.swing.JLabel lblLegalName;
    private javax.swing.JLabel lblOther;
    private javax.swing.JLabel lblRetentionAgent;
    private javax.swing.JLabel lblSpecialTaxPayer;
    private javax.swing.JPanel panelAll;
    private javax.swing.JPanel panelEcuador;
    private javax.swing.JTabbedPane tabContainer;
    private javax.swing.JTextField txtAllIdentification;
    private javax.swing.JTextField txtAllLegalName;
    private javax.swing.JTextField txtEcIdentification;
    private javax.swing.JTextField txtEcLegalName;
    private javax.swing.JTextField txtEcOther;
    private javax.swing.JTextField txtEcRetentionAgent;
    private javax.swing.JTextField txtEcSpecialTaxPayer;
    // End of variables declaration//GEN-END:variables

}
