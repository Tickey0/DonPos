package dev.resolvedor.pos.inventory.management;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataParams;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.Datas;
import com.unicenta.data.loader.PreparedSentence;
import com.unicenta.data.loader.QBFBuilder;
import com.unicenta.data.loader.SentenceExec;
import com.unicenta.data.loader.SentenceList;
import com.unicenta.data.loader.SerializerWriteBasic;
import com.unicenta.data.loader.SerializerWriteParams;
import com.unicenta.data.loader.SerializerWriteString;
import com.unicenta.data.loader.Session;
import com.unicenta.data.loader.StaticSentence;
import com.unicenta.pos.forms.BeanFactoryDataSingle;
import java.util.List;

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
     * Siguiente numero de la serie RT del usuario. Lee el contador y lo sube en
     * uno; debe llamarse SIEMPRE dentro de la transaccion que guarda, o el
     * numero queda quemado si algo falla despues.
     */
    public final Integer getNextTicketIndex(String peopleId, String code) throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "ticketsnum_purchase", peopleId, code).find();
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
                + "t.name, t.code, t.tax_type "
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

                    return line;
                }
        ).list(withholdId);
    }

    /**
     * Estado del documento en el SRI, o null si todavia no se ha enviado.
     *
     * ele_documents la escribe RoQui; DonPos solo la lee. Es lo unico que
     * distingue una retencion corregible de una intocable: AUTORIZADO significa
     * que ya esta en los servidores del SRI y el proveedor la puede usar como
     * credito tributario, asi que cambiarla dejaria las dos copias en desacuerdo.
     */
    public final String getDocumentStatus(String code, String number) throws BasicException {
        return (String) new PreparedSentence(s,
                "SELECT status FROM ele_documents WHERE code = ? AND number = ?",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> dr.getString(1)
        ).find(new Object[]{code, number});
    }

    /**
     * Corrige una retencion CONSERVANDO su numero de serie.
     *
     * No vuelve a pedir numero al contador: gastar otro dejaria un hueco en la
     * secuencia que hay que justificar ante el SRI. La clave de acceso si se
     * recalcula, porque lleva la fecha de emision dentro.
     *
     * El detalle se borra y se reinserta completo: es mas simple y seguro que
     * comparar linea por linea, y va dentro de la misma transaccion.
     */
    public final String updateWithhold(final WithholdInfo withhold) throws BasicException {

        com.unicenta.data.loader.Transaction t;
        t = new com.unicenta.data.loader.Transaction(s) {
            @Override
            public Object transact() throws BasicException {

                withhold.setAccessKey(withhold.buildAccessKey());

                new PreparedSentence(s,
                        "UPDATE withholds SET date_withhold = ?, observation = ?, "
                        + "fiscal_period = ?, access_key = ? WHERE id = ?",
                        SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setTimestamp(1, withhold.getDateWithhold());
                                setString(2, withhold.getObservation());
                                setTimestamp(3, withhold.getFiscalPeriod());
                                setString(4, withhold.getAccessKey());
                                setString(5, withhold.getId());
                            }
                        });

                new PreparedSentence(s,
                        "DELETE FROM withholds_detail WHERE withhold_id = ?",
                        SerializerWriteString.INSTANCE)
                        .exec(withhold.getId());

                SentenceExec detailInsert = new PreparedSentence(s,
                        "INSERT INTO withholds_detail "
                        + "(withhold_id, line, withhold_taxes_id, percentage, "
                        + "base_value, withholded_value, tax_rate) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE);

                for (int i = 0; i < withhold.getLines().size(); i++) {
                    final int line = i;
                    final WithholdLineInfo detail = withhold.getLines().get(i);

                    detailInsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, withhold.getId());
                            setInt(2, line);
                            setString(3, detail.getWithholdTaxesId());
                            setDouble(4, detail.getPercentage());
                            setDouble(5, detail.getBaseValue());
                            setDouble(6, detail.getWithholdedValue());
                            setDouble(7, detail.getTaxRate());
                        }
                    });
                }

                return withhold.getSerieNumber();
            }
        };

        return t.execute().toString();
    }

    /**
     * Guarda cabecera y lineas en una sola transaccion, junto con el avance del
     * contador de la serie. O queda todo, o no queda nada: un comprobante a
     * medias con el numero ya consumido dejaria un hueco en la secuencia que
     * hay que justificar ante el SRI.
     */
    public final String saveWithhold(final WithholdInfo withhold) throws BasicException {

        com.unicenta.data.loader.Transaction t;
        t = new com.unicenta.data.loader.Transaction(s) {
            @Override
            public Object transact() throws BasicException {

                var sequence = getNextTicketIndex(withhold.getUser().getId(), withhold.getCode());

                withhold.setSerieNumber(withhold.getSerie()
                        .concat(String.format(withhold.getFormatNumberDigits(), sequence))
                );
                withhold.setAccessKey(withhold.buildAccessKey());

                new PreparedSentence(s,
                        "INSERT INTO withholds "
                        + "(id, code, serie_number, purchase_id, date_withhold, "
                        + "observation, fiscal_period, access_key, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, withhold.getId());
                                setString(2, withhold.getCode());
                                setString(3, withhold.getSerieNumber());
                                setString(4, withhold.getPurchaseId());
                                setTimestamp(5, withhold.getDateWithhold());
                                setString(6, withhold.getObservation());
                                setTimestamp(7, withhold.getFiscalPeriod());
                                setString(8, withhold.getAccessKey());
                                setBoolean(9, withhold.getStatus());
                            }
                        });

                SentenceExec detailInsert = new PreparedSentence(s,
                        "INSERT INTO withholds_detail "
                        + "(withhold_id, line, withhold_taxes_id, percentage, "
                        + "base_value, withholded_value, tax_rate) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE);

                for (int i = 0; i < withhold.getLines().size(); i++) {
                    final int line = i;
                    final WithholdLineInfo detail = withhold.getLines().get(i);

                    detailInsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, withhold.getId());
                            setInt(2, line);
                            setString(3, detail.getWithholdTaxesId());
                            setDouble(4, detail.getPercentage());
                            setDouble(5, detail.getBaseValue());
                            setDouble(6, detail.getWithholdedValue());
                            setDouble(7, detail.getTaxRate());
                        }
                    });
                }

                return withhold.getSerieNumber();
            }
        };

        return t.execute().toString();
    }
}
