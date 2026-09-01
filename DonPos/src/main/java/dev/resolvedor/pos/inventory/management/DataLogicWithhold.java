package dev.resolvedor.pos.inventory.management;

import java.util.List;
import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.Datas;
import com.unicenta.data.loader.PreparedSentence;
import com.unicenta.data.loader.QBFBuilder;
import com.unicenta.data.loader.SentenceList;
import com.unicenta.data.loader.SerializerWriteBasic;
import com.unicenta.data.loader.SerializerWriteString;
import com.unicenta.data.loader.Session;
import com.unicenta.data.loader.StaticSentence;
import com.unicenta.pos.forms.BeanFactoryDataSingle;

/**
 * Acceso a datos del comprobante de retencion.
 *
 * Copia el molde de DataLogicPurchase.savePurchase: todo lo que pertenece al
 * mismo documento -- contador de la serie, cabecera y lineas -- va dentro de
 * una sola transaccion.
 */
public class DataLogicWithhold extends BeanFactoryDataSingle {

    private Session s;

    @Override
    public void init(Session s) {
        this.s = s;
    }

    /**
     * Catalogo de tipos de retencion para el combo del dialogo.
     */
    public SentenceList getWithholdTaxList() {
        return new StaticSentence(s,
                new QBFBuilder("SELECT id, name, percentage, code, tax_type "
                        + "FROM withhold_taxes "
                        + "WHERE status = true "
                        + "ORDER BY tax_type, name",
                        new String[]{"id", "name", "percentage", "code", "tax_type"}),
                new SerializerWriteBasic(new Datas[]{
                    Datas.OBJECT, Datas.STRING,
                    Datas.OBJECT, Datas.STRING,
                    Datas.OBJECT, Datas.DOUBLE,
                    Datas.OBJECT, Datas.STRING,
                    Datas.OBJECT, Datas.STRING
                }),
                (DataRead dr) -> new WithholdTaxInfo(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getDouble(3),
                        dr.getString(4),
                        dr.getString(5)
                ));
    }








    /**
     * Retencion ya emitida para esa compra, o null si no hay.
     */
    public final WithholdInfo getByPurchase(String purchaseId) throws BasicException {
        return (WithholdInfo) new PreparedSentence(s,
                "SELECT id, code, serie_number, purchase_id, date_withhold, "
                + "observation, fiscal_period, access_key, status "
                + "FROM withholds WHERE purchase_id = ?",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> {
                    var withhold = new WithholdInfo();
                    withhold.setId(dr.getString(1));
                    withhold.setCode(dr.getString(2));
                    withhold.setSerieNumber(dr.getString(3));
                    withhold.setPurchaseId(dr.getString(4));
                    withhold.setDateWithhold(dr.getTimestamp(5));
                    withhold.setObservation(dr.getString(6));
                    withhold.setFiscalPeriod(dr.getTimestamp(7));
                    withhold.setAccessKey(dr.getString(8));
                    withhold.setStatus(dr.getBoolean(9));

                    return withhold;
                }
        ).find(purchaseId);
    }

    /**
     * Lineas de una retencion ya guardada. Une con withhold_taxes para traer
     * tambien el nombre y el codigo del tipo, que la grilla necesita mostrar y
     * no viven en withholds_detail.
     */
    @SuppressWarnings("unchecked")
    public final List<WithholdLineInfo> getLinesByWithhold(String withholdId) throws BasicException {
        return (List<WithholdLineInfo>) new PreparedSentence(s,
                "SELECT d.withhold_id, d.line, d.withhold_taxes_id, d.percentage, "
                + "d.base_value, d.withholded_value, d.tax_rate, "
                + "t.name, t.code, t.tax_type, d.tax_support "
                + "FROM withholds_detail d "
                + "JOIN withhold_taxes t ON t.id = d.withhold_taxes_id "
                + "WHERE d.withhold_id = ? "
                + "ORDER BY d.line",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> {
                    var line = new WithholdLineInfo();
                    line.setWithholdId(dr.getString(1));
                    line.setLine(dr.getInt(2));
                    line.setWithholdTaxesId(dr.getString(3));
                    line.setPercentage(dr.getDouble(4));
                    line.setBaseValue(dr.getDouble(5));
                    line.setWithholdedValue(dr.getDouble(6));
                    line.setTaxRate(dr.getDouble(7));
                    line.setWithholdTaxName(dr.getString(8));
                    line.setWithholdTaxCode(dr.getString(9));
                    line.setTaxType(dr.getString(10));
                    line.setTaxSupport(dr.getString(11));

                    return line;
                }
        ).list(withholdId);
    }


    /**
     * Cuantas retenciones tiene ya esa compra. El indice unico
     * uk_withholds_purchase impide que sea mas de una, pero preguntarlo antes
     * permite avisar al usuario en vez de dejar que reviente la base.
     */
    public final Integer countByPurchase(String purchaseId) throws BasicException {
        return (Integer) new PreparedSentence(s,
                "SELECT COUNT(*) FROM withholds WHERE purchase_id = ?",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> dr.getInt(1)
        ).find(purchaseId);
    }


}
