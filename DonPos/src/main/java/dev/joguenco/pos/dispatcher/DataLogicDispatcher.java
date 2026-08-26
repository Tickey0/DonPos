package dev.joguenco.pos.dispatcher;

import com.unicenta.data.loader.Datas;
import com.unicenta.data.loader.Session;
import com.unicenta.data.loader.TableDefinition;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author Jorge Luis
 * @web https://resolvers.dev
 * @mail jorgeluis@resolvers.dev
 */
public class DataLogicDispatcher extends BeanFactoryDataSingle {

    private Session s;
    private TableDefinition tdDispatcher;

    @Override
    public void init(Session s) {
        this.s = s;

        tdDispatcher = new TableDefinition(s,
                "dispatchers",
                new String[]{"id", "taxid_type", "taxid", "name",
                    "plate", "phone", "observation", "status"},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING,
                    Datas.STRING, Datas.STRING, Datas.STRING, Datas.BOOLEAN},
                new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING,
                    Formats.STRING, Formats.STRING, Formats.STRING, Formats.BOOLEAN},
                new int[]{0});
    }

    public final TableDefinition getTableDispatcher() {
        return tdDispatcher;
    }
}
