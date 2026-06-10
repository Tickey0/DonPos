package dev.resolvedor.pos.sales;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.LocalRes;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.forms.BeanFactoryApp;
import com.unicenta.pos.forms.BeanFactoryException;
import com.unicenta.pos.forms.JPanelView;
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

    private VolumeDiscount.CategoryTableModel categoryModel;
    private VolumeDiscount.DiscountTableModel discountModel;

    public VolumeDiscount() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPanel = new javax.swing.JTabbedPane();
        panCategories = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableCategories = new javax.swing.JTable();
        panDiscount = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableProducts = new javax.swing.JTable();
        cmdOkProducts = new javax.swing.JButton();

        tabPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabPanel.addChangeListener(this::tabPanelStateChanged);

        panCategories.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        tableCategories.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableCategories);

        javax.swing.GroupLayout panCategoriesLayout = new javax.swing.GroupLayout(panCategories);
        panCategories.setLayout(panCategoriesLayout);
        panCategoriesLayout.setHorizontalGroup(
            panCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
        );
        panCategoriesLayout.setVerticalGroup(
            panCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCategoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addGap(48, 48, 48))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        tabPanel.addTab(bundle.getString("Menu.Categories"), panCategories); // NOI18N

        panDiscount.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

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

        javax.swing.GroupLayout panDiscountLayout = new javax.swing.GroupLayout(panDiscount);
        panDiscount.setLayout(panDiscountLayout);
        panDiscountLayout.setHorizontalGroup(
            panDiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .addGroup(panDiscountLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(cmdOkProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panDiscountLayout.setVerticalGroup(
            panDiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDiscountLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdOkProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabPanel.addTab(bundle.getString("button.discount"), panDiscount); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdOkProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOkProductsActionPerformed
        try {
            for (int i = 0; i < discountModel.getRowCount(); i++) {
                VolumeDiscountInfo product = discountModel.discount.get(i);
                dlDiscount.updateProductCommission(
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
        int selectedRow = tableCategories.getSelectedRow();
        if (selectedRow >= 0) {
            String categoryId = (String) categoryModel.getValueAt(tableCategories.convertRowIndexToModel(selectedRow), 0);
            loadVolumeDiscount(categoryId);
        }
    }//GEN-LAST:event_cmdOkProductsActionPerformed

    private void tabPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPanelStateChanged

        if (tabPanel.getSelectedIndex() == 1) {
            int selectedRow = tableCategories.getSelectedRow();
            if (selectedRow >= 0) {
                String categoryId = (String) categoryModel.getValueAt(tableCategories.convertRowIndexToModel(selectedRow), 0);
                loadVolumeDiscount(categoryId);
            }
        }
    }//GEN-LAST:event_tabPanelStateChanged

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Discounts");
    }

    @Override
    public void activate() throws BasicException {
        loadCategory();
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
            List<CategoryInfo> categories = dlDiscount.getCategoryList().list();

            categoryModel = new VolumeDiscount.CategoryTableModel(categories);
            tableCategories.setModel(categoryModel);

        } catch (BasicException ex) {
            log.error(VolumeDiscount.class.getName() + " loadCategory " + ex);
        }
        resetTable(tableCategories);
    }

    private void loadVolumeDiscount(String categoryId) {
        try {
            List<VolumeDiscountInfo> discount = dlDiscount.getVolumeDiscountList(categoryId).list();

            discountModel = new VolumeDiscount.DiscountTableModel(discount);
            tableProducts.setModel(discountModel);

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

    class CategoryTableModel extends AbstractTableModel {

        String id = AppLocal.getIntString("label.catid");
        String name = AppLocal.getIntString("label.catname");

        List<CategoryInfo> category;
        String[] columnNames = {id, name};

        public CategoryTableModel(List<CategoryInfo> list) {
            category = list;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return category.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            CategoryInfo categoryStock = category.get(row);

            switch (column) {
                case 0:
                    return categoryStock.getId();
                case 1:
                    return categoryStock.getName();
                default:
                    return "";
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
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
    private javax.swing.JButton cmdOkProducts;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panCategories;
    private javax.swing.JPanel panDiscount;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTable tableCategories;
    private javax.swing.JTable tableProducts;
    // End of variables declaration//GEN-END:variables
}
