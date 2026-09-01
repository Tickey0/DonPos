package dev.joguenco.pos.taxes;

import com.unicenta.data.loader.Datas;
import com.unicenta.data.loader.Session;
import com.unicenta.data.loader.TableDefinition;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.BeanFactoryDataSingle;

/**
 * Catalogo de conceptos de retencion del SRI (tabla withhold_taxes).
 *
 * Sigue el mismo molde que DataLogicSales.getTableTaxes(): una TableDefinition
 * es todo lo que JPanelTable necesita para listar, insertar, editar y borrar.
 */
public class DataLogicWithholdTax extends BeanFactoryDataSingle {

    private Session s;

    @Override
    public void init(Session s) {
        this.s = s;
    }

    public final TableDefinition getTableWithholdTaxes() {
        return new TableDefinition(s,
                "withhold_taxes",
                new String[]{"ID", "NAME", "PERCENTAGE", "CODE", "TAX_TYPE",
                    "CREATED_AT", "STATUS"},
                new String[]{"ID",
                    AppLocal.getIntString("label.name"),
                    AppLocal.getIntString("label.percentage"),
                    AppLocal.getIntString("label.LegalCode"),
                    AppLocal.getIntString("label.withhold.taxtype"),
                    AppLocal.getIntString("label.date"),
                    AppLocal.getIntString("label.Status")},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.STRING,
                    Datas.STRING, Datas.TIMESTAMP, Datas.BOOLEAN},
                new Formats[]{Formats.STRING, Formats.STRING, Formats.DOUBLE, Formats.STRING,
                    Formats.STRING, Formats.DATE, Formats.BOOLEAN},
                new int[]{0}
        );
    }
}
