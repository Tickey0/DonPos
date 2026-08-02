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
package dev.joguenco.pos.subscription;

import com.unicenta.basic.BasicException;
import com.unicenta.data.gui.ComboBoxValModel;
import com.unicenta.data.user.DirtyManager;
import com.unicenta.data.user.EditorRecord;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.util.AltEncrypter;
import dev.joguenco.effect.CursorAnimation;
import dev.joguenco.http.client.ServiceGenerator;
import dev.joguenco.http.client.ping.PingResponse;
import dev.joguenco.http.client.ping.PingService;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

/**
 *
 * @author Jorge Luis
 */
@Slf4j
public class SubscriptionEditor extends JPanel implements EditorRecord {

    private Object oId;
    private ComboBoxValModel modelAuthenticationMethod;
    private final String key = "cypherkey";

    public SubscriptionEditor(AppView app, DirtyManager dirty) {
        initComponents();

        modelAuthenticationMethod = new ComboBoxValModel();
        modelAuthenticationMethod.add("None");
        modelAuthenticationMethod.add("Token");
        modelAuthenticationMethod.add("Password");

        cbxAuthenticationMethod.setModel(modelAuthenticationMethod);

        txtName.getDocument().addDocumentListener(dirty);
        txtUrl.getDocument().addDocumentListener(dirty);
        cbxAuthenticationMethod.addActionListener(dirty);
        txtToken.getDocument().addDocumentListener(dirty);
        txtUsername.getDocument().addDocumentListener(dirty);
        txtPassword.getDocument().addDocumentListener(dirty);
        txtTimeout.getDocument().addDocumentListener(dirty);
        chkStatus.addActionListener(dirty);
    }

    @Override
    public void writeValueEOF() {
        oId = null;
        txtName.setText(null);
        txtUrl.setText(null);
        modelAuthenticationMethod.setSelectedFirst();
        txtToken.setText(null);
        txtUsername.setText(null);
        txtPassword.setText(null);
        txtTimeout.setText(null);

        txtName.setEnabled(false);
        txtUrl.setEnabled(false);
        txtToken.setEnabled(false);
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtTimeout.setEnabled(false);
        chkStatus.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        oId = null;
        txtName.setText(null);
        txtUrl.setText(null);
        modelAuthenticationMethod.setSelectedFirst();
        txtToken.setText(null);
        txtUsername.setText(null);
        txtPassword.setText(null);
        txtTimeout.setText("0");
        chkStatus.setSelected(true);

        txtName.setEnabled(true);
        txtUrl.setEnabled(true);
        txtToken.setEnabled(true);
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);
        txtTimeout.setEnabled(true);
        chkStatus.setEnabled(true);
    }

    @Override
    public void writeValueEdit(Object value) {
        Object[] subscription = (Object[]) value;
        AltEncrypter cypher = new AltEncrypter(key);

        oId = subscription[0];
        txtName.setText(Formats.STRING.formatValue(subscription[1]));
        txtUrl.setText(Formats.STRING.formatValue(subscription[2]));
        modelAuthenticationMethod.setSelectedItem(subscription[3]);
        txtTimeout.setText(Formats.INT.formatValue(subscription[7]));
        chkStatus.setSelected(Boolean.valueOf(Formats.BOOLEAN.formatValue(subscription[8])));

        txtName.setEnabled(true);
        txtUrl.setEnabled(true);
        if ("Token".equals(modelAuthenticationMethod.getSelectedText())) {
            txtToken.setText(Formats.STRING.formatValue(subscription[4]));
            txtUsername.setText(null);
            txtPassword.setText(null);
            txtToken.setEnabled(true);
        } else if ("Password".equals(modelAuthenticationMethod.getSelectedText())) {
            txtToken.setText(null);
            txtUsername.setText(Formats.STRING.formatValue(subscription[5]));
            txtPassword.setText(cypher.decrypt(Formats.STRING.formatValue(subscription[6])));
            txtUsername.setEnabled(true);
            txtPassword.setEnabled(true);
        } else {
            txtToken.setText(null);
            txtUsername.setText(null);
            txtPassword.setText(null);
        }
        txtTimeout.setEnabled(true);
        chkStatus.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] subscription = (Object[]) value;
        AltEncrypter cypher = new AltEncrypter(key);

        oId = subscription[0];
        txtName.setText(Formats.STRING.formatValue(subscription[1]));
        txtUrl.setText(Formats.STRING.formatValue(subscription[2]));
        modelAuthenticationMethod.setSelectedItem(subscription[3]);
        txtToken.setText(Formats.STRING.formatValue(subscription[4]));
        txtUsername.setText(Formats.STRING.formatValue(subscription[5]));
        txtPassword.setText(cypher.decrypt(Formats.STRING.formatValue(subscription[6])));
        txtTimeout.setText(Formats.INT.formatValue(subscription[7]));
        chkStatus.setSelected(Boolean.valueOf(Formats.BOOLEAN.formatValue(subscription[8])));

        txtName.setEnabled(false);
        txtUrl.setEnabled(false);
        txtToken.setEnabled(false);
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtTimeout.setEnabled(false);
        chkStatus.setEnabled(false);
    }

    @Override
    public void refresh() {
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {
        Object[] subscription = new Object[9];
        char[] passwordChars = txtPassword.getPassword();
        String password = new String(passwordChars);
        AltEncrypter cypher = new AltEncrypter(key);

        subscription[0] = oId == null ? UUID.randomUUID().toString() : oId;
        subscription[1] = txtName.getText();
        subscription[2] = txtUrl.getText();
        subscription[3] = modelAuthenticationMethod.getSelectedText();
        subscription[4] = txtToken.getText();
        subscription[5] = txtUsername.getText();
        subscription[6] = cypher.encrypt(password);
        subscription[7] = Integer.parseInt(txtTimeout.getText());
        subscription[8] = chkStatus.isSelected();

        return subscription;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblToken = new javax.swing.JLabel();
        txtToken = new javax.swing.JTextField();
        lblTimeout = new javax.swing.JLabel();
        txtTimeout = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        chkStatus = new javax.swing.JCheckBox();
        cmdPing = new javax.swing.JButton();
        lblAuthenticationMethod = new javax.swing.JLabel();
        cbxAuthenticationMethod = new javax.swing.JComboBox<>();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();

        lblName.setDisplayedMnemonic('N');
        lblName.setLabelFor(txtName);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        lblName.setText(bundle.getString("label.name")); // NOI18N

        lblUrl.setDisplayedMnemonic('U');
        lblUrl.setLabelFor(txtUrl);
        lblUrl.setText(bundle.getString("label.URL")); // NOI18N

        lblToken.setDisplayedMnemonic('T');
        lblToken.setLabelFor(txtToken);
        lblToken.setText(bundle.getString("label.token")); // NOI18N

        lblTimeout.setDisplayedMnemonic('I');
        lblTimeout.setLabelFor(txtTimeout);
        lblTimeout.setText(bundle.getString("label.timeout")); // NOI18N

        lblStatus.setDisplayedMnemonic('S');
        lblStatus.setLabelFor(chkStatus);
        lblStatus.setText(bundle.getString("label.Status")); // NOI18N

        chkStatus.setSelected(true);

        cmdPing.setText("Ping");
        cmdPing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPingActionPerformed(evt);
            }
        });

        lblAuthenticationMethod.setDisplayedMnemonic('U');
        lblAuthenticationMethod.setText(bundle.getString("label.authenticationMethod")); // NOI18N

        cbxAuthenticationMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAuthenticationMethodActionPerformed(evt);
            }
        });

        lblUsername.setDisplayedMnemonic('T');
        lblUsername.setText(bundle.getString("label.user")); // NOI18N

        lblPassword.setDisplayedMnemonic('T');
        lblPassword.setText(bundle.getString("label.Password")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblToken, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtToken, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblAuthenticationMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cbxAuthenticationMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(cmdPing))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkStatus))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsername)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUrl)
                    .addComponent(cmdPing))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAuthenticationMethod)
                    .addComponent(cbxAuthenticationMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblToken))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTimeout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkStatus)
                    .addComponent(lblStatus))
                .addContainerGap(310, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdPingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPingActionPerformed
        CursorAnimation.startWaitCursor(getRootPane());
        if (!txtUrl.getText().isEmpty()) {
            try {
                var generator = new ServiceGenerator(txtUrl.getText());
                var service = generator.createService(PingService.class);

                var callSync = service.ping();

                Response<PingResponse> response = callSync.execute();
                if (response.isSuccessful()) {
                    PingResponse ping = response.body();
                    JOptionPane.showMessageDialog(this,
                            ping.getMessage(), "Ok", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Not response", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalArgumentException | HeadlessException | IOException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                log.error(this.getClass().getName() + " " + ex.getMessage());
            }
        }
        CursorAnimation.stopWaitCursor(getRootPane());
    }//GEN-LAST:event_cmdPingActionPerformed

    private void cbxAuthenticationMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxAuthenticationMethodActionPerformed
        if ("Token".equals(modelAuthenticationMethod.getSelectedText())) {
            txtToken.setEnabled(true);
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
        } else if ("Password".equals(modelAuthenticationMethod.getSelectedText())) {
            txtToken.setEnabled(false);
            txtUsername.setEnabled(true);
            txtPassword.setEnabled(true);
        } else {
            txtToken.setEnabled(false);
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
        }
    }//GEN-LAST:event_cbxAuthenticationMethodActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbxAuthenticationMethod;
    private javax.swing.JCheckBox chkStatus;
    private javax.swing.JButton cmdPing;
    private javax.swing.JLabel lblAuthenticationMethod;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTimeout;
    private javax.swing.JLabel lblToken;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtTimeout;
    private javax.swing.JTextField txtToken;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
