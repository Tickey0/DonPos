package dev.joguenco.pos.taxes;

import com.unicenta.data.gui.ListCellRendererBasic;
import com.unicenta.data.loader.ComparatorCreator;
import com.unicenta.data.loader.TableDefinition;
import com.unicenta.data.loader.Vectorer;
import com.unicenta.data.user.EditorRecord;
import com.unicenta.data.user.ListProvider;
import com.unicenta.data.user.ListProviderCreator;
import com.unicenta.data.user.SaveProvider;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.panels.JPanelTable;
import javax.swing.ListCellRenderer;

/**
 * Pantalla de mantenimiento de los conceptos de retencion.
 *
 * Copia del molde de TaxPanel (Impuestos): JPanelTable ya trae la mecanica de
 * buscar, insertar, editar, borrar y guardar; aqui solo se declara que tabla
 * usar y como mostrar cada fila.
 */
public class WithholdTaxPanel extends JPanelTable {

    private TableDefinition tWithholdTaxes;
    private WithholdTaxEditor jeditor;

    public WithholdTaxPanel() {
    }

    @Override
    protected void init() {
        var dlWithholdTax = (DataLogicWithholdTax) app.getBean(
                "dev.joguenco.pos.taxes.DataLogicWithholdTax");

        tWithholdTaxes = dlWithholdTax.getTableWithholdTaxes();
        jeditor = new WithholdTaxEditor(dirty);
    }

    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(tWithholdTaxes);
    }

    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tWithholdTaxes);
    }

    /**
     * Columnas de la grilla: nombre, porcentaje, codigo y tipo.
     */
    @Override
    public Vectorer getVectorer() {
        return tWithholdTaxes.getVectorerBasic(new int[]{1, 2, 3, 4});
    }

    @Override
    public ComparatorCreator getComparatorCreator() {
        return tWithholdTaxes.getComparatorCreator(new int[]{1, 2, 3, 4});
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tWithholdTaxes.getRenderStringBasic(new int[]{1}));
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.WithholdTaxes");
    }
}
