package dev.joguenco.pos.dispatcher;

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
 *
 * @author Jorge Luis
 * @web https://resolvers.dev
 * @mail jorgeluis@resolvers.dev
 */
public class DispatcherPanel extends JPanelTable {

    private TableDefinition tdDispatcher;
    private DispatcherEditor dispatcherEditor;

    @Override
    protected void init() {
        var dlDispatcher = (DataLogicDispatcher) app.getBean("dev.joguenco.pos.dispatcher.DataLogicDispatcher");
        tdDispatcher = dlDispatcher.getTableDispatcher();
        dispatcherEditor = new DispatcherEditor(app, dirty);
    }

    @Override
    public EditorRecord getEditor() {
        return dispatcherEditor;
    }

    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(tdDispatcher);
    }

    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tdDispatcher);
    }

    @Override
    public Vectorer getVectorer() {
        return tdDispatcher.getVectorerBasic(new int[]{2, 3, 4});
    }

    @Override
    public ComparatorCreator getComparatorCreator() {
        return tdDispatcher.getComparatorCreator(new int[]{2, 3, 4});
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Dispatcher");
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tdDispatcher.getRenderStringBasic(new int[]{4, 3}));
    }
}
