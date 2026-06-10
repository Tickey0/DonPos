package dev.resolvedor.pos.sales;

import com.unicenta.basic.BasicException;
import com.unicenta.data.gui.ComboBoxValModel;
import com.unicenta.data.loader.LocalRes;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.forms.BeanFactoryApp;
import com.unicenta.pos.forms.BeanFactoryException;
import com.unicenta.pos.forms.JPanelView;
import com.unicenta.pos.ticket.ProductInfoExt;
import java.awt.Font;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Jorge Luis
 * @web https://resolvedor.dev
 * @mail jorgeluis@resolvedor.dev
 */
@Slf4j
public class VolumeDiscount extends JPanel implements JPanelView, BeanFactoryApp {

    private AppView app;
    private DataLogicDiscount dlDiscount;

    private ComboBoxValModel modelCategory;
    private VolumeDiscount.DiscountTableModel modelDiscount;

    public VolumeDiscount() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panFilter = new javax.swing.JPanel();
        lblCategories = new javax.swing.JLabel();
        cboCategories = new javax.swing.JComboBox<>();
        cmdReload = new javax.swing.JButton();
        panProduct = new javax.swing.JPanel();
        cmdFindProduct = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableProducts = new javax.swing.JTable();
        cmdOkProducts = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        panFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("label.find"))); // NOI18N

        lblCategories.setText(bundle.getString("label.prodcategory")); // NOI18N
        lblCategories.setPreferredSize(new java.awt.Dimension(90, 36));

        cboCategories.setPreferredSize(new java.awt.Dimension(90, 36));
        cboCategories.addActionListener(this::cboCategoriesActionPerformed);

        cmdReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/unicenta/images/reload.png"))); // NOI18N
        cmdReload.setPreferredSize(new java.awt.Dimension(36, 36));
        cmdReload.addActionListener(this::cmdReloadActionPerformed);

        javax.swing.GroupLayout panFilterLayout = new javax.swing.GroupLayout(panFilter);
        panFilter.setLayout(panFilterLayout);
        panFilterLayout.setHorizontalGroup(
            panFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdReload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(260, Short.MAX_VALUE))
        );
        panFilterLayout.setVerticalGroup(
            panFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdReload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panProduct.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("label.stockproduct"))); // NOI18N

        cmdFindProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/unicenta/images/search24.png"))); // NOI18N
        cmdFindProduct.setPreferredSize(new java.awt.Dimension(36, 36));
        cmdFindProduct.addActionListener(this::cmdFindProductActionPerformed);

        javax.swing.GroupLayout panProductLayout = new javax.swing.GroupLayout(panProduct);
        panProduct.setLayout(panProductLayout);
        panProductLayout.setHorizontalGroup(
            panProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProductLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdFindProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panProductLayout.setVerticalGroup(
            panProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProductLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdFindProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tableProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableProducts);

        cmdOkProducts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/unicenta/images/ok.png"))); // NOI18N
        cmdOkProducts.setPreferredSize(new java.awt.Dimension(120, 36));
        cmdOkProducts.addActionListener(this::cmdOkProductsActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(cmdOkProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panProduct, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 341, Short.MAX_VALUE)
                .addComponent(cmdOkProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(177, 177, 177)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(90, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdOkProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOkProductsActionPerformed
        try {
            for (int i = 0; i < modelDiscount.getRowCount(); i++) {
                VolumeDiscountInfo product = modelDiscount.discount.get(i);
                dlDiscount.updateDiscount(
                        product.getId(),
                        Math.abs(0)
                );
            }
            JOptionPane.showMessageDialog(
                    this,
                    AppLocal.getIntString("Menu.Discount"),
                    LocalRes.getIntString("sgn.success"),
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (BasicException ex) {
            log.error(VolumeDiscount.class.getName() + " cmdOkProductsActionPerformed " + ex);
        }

        loadVolumeDiscount((String) modelCategory.getSelectedKey());

    }//GEN-LAST:event_cmdOkProductsActionPerformed

    private void cboCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoriesActionPerformed
        loadVolumeDiscount(modelCategory.getSelectedKey().toString());
    }//GEN-LAST:event_cboCategoriesActionPerformed

    private void cmdFindProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFindProductActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProductFinder dialog = new ProductFinder(app, new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        dialog.setVisible(false);
                    }
                });
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                if (dialog.getReturnStatus() == ProductFinder.RET_OK) {
                    ProductInfoExt product = dialog.getProduct();
                    System.out.println(product);
                    //incProduct(product, dialog.getTax());
                }
            }
        });
    }//GEN-LAST:event_cmdFindProductActionPerformed

    private void cmdReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdReloadActionPerformed
        loadCategory();
        loadVolumeDiscount("");
    }//GEN-LAST:event_cmdReloadActionPerformed

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Discounts");
    }

    @Override
    public void activate() throws BasicException {
        loadCategory();
        loadVolumeDiscount("");
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
        dlDiscount = (DataLogicDiscount) app.getBean("dev.resolvedor.pos.sales.DataLogicDiscount");
    }

    @Override
    public Object getBean() {
        return this;
    }

    private void loadCategory() {
        try {
            modelCategory = new ComboBoxValModel(dlDiscount.getCategoryList().list());
            cboCategories.setModel(modelCategory);

        } catch (BasicException ex) {
            log.error(VolumeDiscount.class.getName() + " loadCategory " + ex);
        }
    }

    private void loadVolumeDiscount(String categoryId) {
        try {
            List<VolumeDiscountInfo> discount = dlDiscount.getVolumeDiscountList(categoryId).list();

            modelDiscount = new VolumeDiscount.DiscountTableModel(discount);
            tableProducts.setModel(modelDiscount);

        } catch (BasicException ex) {
            log.error(VolumeDiscount.class.getName() + " loadVolumeDiscount " + ex);
        }
        resetTable(tableProducts);
    }

    public void resetTable(JTable table) {

        Font headerFont = new Font("Arial", Font.BOLD, 14);
        Font bodyFont = new Font("Arial", Font.PLAIN, 18);

        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont);
        table.setFont(bodyFont);
        table.setRowHeight(27);
        table.setPreferredScrollableViewportSize(
                new java.awt.Dimension(table.getPreferredScrollableViewportSize().width, 24 * 8)
        );

        table.getTableHeader().setReorderingAllowed(true);
        table.setAutoCreateRowSorter(true);

        table.repaint();
    }

    class DiscountTableModel extends AbstractTableModel {

        String id = AppLocal.getIntString("label.catid");
        String code = AppLocal.getIntString("label.prodbarcode");
        String name = AppLocal.getIntString("label.name");
        String priceSell = AppLocal.getIntString("label.price");
        String minimumQuantity = AppLocal.getIntString("label.units2");
        String value = AppLocal.getIntString("button.discount");
        String status = AppLocal.getIntString("label.Status");

        List<VolumeDiscountInfo> discount;
        String[] columnNames = {id, code, name, priceSell, minimumQuantity, value, status};

        public DiscountTableModel(List<VolumeDiscountInfo> list) {
            discount = list;
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public int getRowCount() {
            return discount.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            VolumeDiscountInfo volumeDiscount = discount.get(row);

            switch (column) {
                case 0:
                    return volumeDiscount.getId();
                case 1:
                    return volumeDiscount.getCode();
                case 2:
                    return volumeDiscount.getName();
                case 3:
                    return volumeDiscount.getPriceSell();
                case 4:
                    return volumeDiscount.getMinimumQuantity();
                case 5:
                    return volumeDiscount.getValue();
                case 6:
                    return volumeDiscount.getStatus();
                default:
                    return "";
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboCategories;
    private javax.swing.JButton cmdFindProduct;
    private javax.swing.JButton cmdOkProducts;
    private javax.swing.JButton cmdReload;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCategories;
    private javax.swing.JPanel panFilter;
    private javax.swing.JPanel panProduct;
    private javax.swing.JTable tableProducts;
    // End of variables declaration//GEN-END:variables
}
