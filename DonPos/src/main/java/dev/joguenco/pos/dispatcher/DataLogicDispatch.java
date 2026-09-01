package dev.joguenco.pos.dispatcher;

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
import com.unicenta.data.loader.Transaction;
import com.unicenta.pos.forms.BeanFactoryDataSingle;
import java.util.Date;
import java.util.List;

/**
 * Acceso a datos de la guia de remision.
 *
 * Copia el molde de DataLogicPurchase.savePurchase: contador de la serie,
 * cabecera y detalle van dentro de una sola transaccion.
 */
public class DataLogicDispatch extends BeanFactoryDataSingle {

    private Session s;

    @Override
    public void init(Session s) {
        this.s = s;
    }

    /**
     * Transportistas activos para el combo.
     *
     * Se lee con un SerializerRead propio en vez de getListSentence(), porque
     * ese devuelve cada fila como Object[] y el combo terminaria mostrando
     * "[Ljava.lang.Object;@5ab79515" en lugar del nombre.
     */
    public SentenceList getDispatcherList() {
        return new StaticSentence(s,
                "SELECT `id`, `name`, `plate` FROM `dispatchers` "
                + "WHERE `status` = TRUE ORDER BY `name`",
                null,
                (DataRead dr) -> new DispatcherComboInfo(
                        dr.getString(1), dr.getString(2), dr.getString(3)));
    }

    /**
     * Lugares de partida: almacenes y establecimientos juntos.
     *
     * Se muestran los dos porque una guia puede salir de una bodega o de un
     * establecimiento. Se filtran los que no tienen direccion, que es lo que
     * realmente se guarda en dispatches.address_start.
     */
    public SentenceList getAddressStartList() {
        return new StaticSentence(s,
                new QBFBuilder(
                        "SELECT direccion, origen FROM ("
                        + "SELECT `l`.`address` direccion, "
                        + "CONCAT('Almacen: ', `l`.`name`, ' - ', `l`.`address`) origen "
                        + "FROM `locations` `l` "
                        + "WHERE `l`.`address` IS NOT NULL AND TRIM(`l`.`address`) <> '' "
                        + "UNION ALL "
                        + "SELECT `e`.`address`, "
                        + "CONCAT('Establecimiento ', `e`.`id`, ': ', `e`.`comercial_name`, "
                        + "' - ', `e`.`address`) "
                        + "FROM `establishments` `e` "
                        + "WHERE `e`.`status` = 1 "
                        + "AND `e`.`address` IS NOT NULL AND TRIM(`e`.`address`) <> '' "
                        + ") origenes ORDER BY origen",
                        new String[]{"direccion", "origen"}),
                new SerializerWriteBasic(new Datas[]{
                    Datas.OBJECT, Datas.STRING,
                    Datas.OBJECT, Datas.STRING
                }),
                (DataRead dr) -> new AddressStartInfo(dr.getString(1), dr.getString(2)));
    }

    /**
     * Facturas de HOY que todavia no tienen guia.
     *
     * Una guia se hace el mismo dia del despacho, asi que la lista es siempre
     * la del dia; el buscador de la pantalla filtra dentro de esta lista, no
     * cambia de fecha.
     *
     * El HAVING deja fuera las que el LEFT JOIN encontro en dispatches_detail:
     * si una factura ya salio en otra guia no hay nada que hacer con ella
     * aqui, y mostrarla solo para bloquearla era ruido.
     *
     * Tampoco salen las de Consumidor Final: sin destinatario identificado no
     * se puede emitir la guia.
     */
    @SuppressWarnings("unchecked")
    public final List<DispatchLineInfo> getInvoicesOfToday() throws BasicException {
        return (List<DispatchLineInfo>) new PreparedSentence(s,
                "SELECT `t`.`code`, `t`.`serie_number`, `c`.`name`, `c`.`taxid`, "
                + "`c`.`address`, "
                + "ROUND(SUM(`tl`.`units` * `tl`.`price`), 2) total, "
                + "MAX(`r`.`datenew`) fecha "
                + "FROM `tickets` `t` "
                + "JOIN `receipts` `r` ON `r`.`id` = `t`.`id` "
                + "LEFT JOIN `customers` `c` ON `c`.`id` = `t`.`customer` "
                + "JOIN `ticketlines` `tl` ON `tl`.`ticket` = `t`.`id` "
                + "LEFT JOIN `dispatches_detail` `d` "
                + "ON `d`.`reference_code` = `t`.`code` "
                + "AND `d`.`reference_number` = `t`.`serie_number` "
                + "WHERE `t`.`code` = 'FV' "
                + "AND CAST(`r`.`datenew` AS DATE) = CURDATE() "
                // La guia necesita un destinatario identificado. Consumidor
                // Final (codigo CF, identificacion 9999999999999) no lo es, y
                // el SRI rechaza la guia por falta de destinatario.
                + "AND `c`.`taxid_type` <> 'CF' "
                + "GROUP BY `t`.`code`, `t`.`serie_number`, `c`.`name`, `c`.`taxid`, "
                + "`c`.`address` "
                + "HAVING MAX(CASE WHEN `d`.`reference_number` IS NULL THEN 0 ELSE 1 END) = 0 "
                + "ORDER BY `t`.`serie_number`",
                null,
                (DataRead dr) -> {
                    var linea = new DispatchLineInfo();
                    linea.setReferenceCode(dr.getString(1));
                    linea.setReferenceNumber(dr.getString(2));
                    linea.setCustomerName(dr.getString(3));
                    linea.setCustomerTaxId(dr.getString(4));
                    linea.setCustomerAddress(dr.getString(5));
                    linea.setTotal(dr.getDouble(6));
                    linea.setDate(dr.getTimestamp(7));

                    return linea;
                }
        ).list();
    }

    /**
     * Guias emitidas en una fecha, para el buscador.
     */
    @SuppressWarnings("unchecked")
    public final List<DispatchInfo> getDispatchesByDate(final Date date) throws BasicException {
        return (List<DispatchInfo>) new PreparedSentence(s,
                "SELECT `d`.`id`, `d`.`serie_number`, `d`.`date_dispatch`, "
                + "`d`.`date_end_dispatch`, `d`.`address_start`, `d`.`access_key`, "
                + "`d`.`observation`, `p`.`name`, `p`.`plate`, `d`.`dispatcher_id`, "
                + "`d`.`transfer_reason`, "
                + "(SELECT COUNT(*) FROM `dispatches_detail` `dd` "
                + "WHERE `dd`.`dispatches_id` = `d`.`id`) facturas "
                + "FROM `dispatches` `d` "
                + "LEFT JOIN `dispatchers` `p` ON `p`.`id` = `d`.`dispatcher_id` "
                + "WHERE CAST(`d`.`date_dispatch` AS DATE) = ? "
                + "ORDER BY `d`.`serie_number` DESC",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> {
                    var guia = new DispatchInfo();
                    guia.setId(dr.getString(1));
                    guia.setSerieNumber(dr.getString(2));
                    guia.setDateDispatch(dr.getTimestamp(3));
                    guia.setDateEndDispatch(dr.getTimestamp(4));
                    guia.setAddressStart(dr.getString(5));
                    guia.setAccessKey(dr.getString(6));
                    guia.setObservation(dr.getString(7));
                    guia.setDispatcherId(dr.getString(10));
                    guia.setTransferReason(dr.getString(11));
                    guia.setLineCount(dr.getInt(12));

                    var nombre = dr.getString(8);
                    var placa = dr.getString(9);
                    guia.setDispatcherLabel(placa == null || placa.isEmpty()
                            ? nombre : nombre + " - " + placa);

                    return guia;
                }
        ).list(new java.text.SimpleDateFormat("yyyy-MM-dd").format(date));
    }

    /**
     * Las facturas de una guia ya guardada.
     */
    @SuppressWarnings("unchecked")
    public final List<DispatchLineInfo> getLinesByDispatch(final String dispatchId)
            throws BasicException {
        return (List<DispatchLineInfo>) new PreparedSentence(s,
                "SELECT `dd`.`reference_code`, `dd`.`reference_number`, "
                + "`c`.`name`, `c`.`taxid`, `c`.`address`, "
                + "ROUND(SUM(`tl`.`units` * `tl`.`price`), 2) total, `r`.`datenew` "
                + "FROM `dispatches_detail` `dd` "
                + "LEFT JOIN `tickets` `t` ON `t`.`code` = `dd`.`reference_code` "
                + "AND `t`.`serie_number` = `dd`.`reference_number` "
                + "LEFT JOIN `receipts` `r` ON `r`.`id` = `t`.`id` "
                + "LEFT JOIN `customers` `c` ON `c`.`id` = `t`.`customer` "
                + "LEFT JOIN `ticketlines` `tl` ON `tl`.`ticket` = `t`.`id` "
                + "WHERE `dd`.`dispatches_id` = ? "
                + "GROUP BY `dd`.`line`, `dd`.`reference_code`, `dd`.`reference_number`, "
                + "`c`.`name`, `c`.`taxid`, `c`.`address`, `r`.`datenew` "
                + "ORDER BY `dd`.`line`",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> {
                    var linea = new DispatchLineInfo();
                    linea.setReferenceCode(dr.getString(1));
                    linea.setReferenceNumber(dr.getString(2));
                    linea.setCustomerName(dr.getString(3));
                    linea.setCustomerTaxId(dr.getString(4));
                    linea.setCustomerAddress(dr.getString(5));
                    linea.setTotal(dr.getDouble(6));
                    linea.setDate(dr.getTimestamp(7));

                    return linea;
                }
        ).list(dispatchId);
    }

    /**
     * Siguiente numero de la serie GUI. Debe llamarse SIEMPRE dentro de la
     * transaccion que guarda, o el numero queda quemado si algo falla despues.
     */
    public final Integer getNextTicketIndex(String peopleId, String code) throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "ticketsnum", peopleId, code).find();
    }

    /**
     * Guarda cabecera y detalle en una sola transaccion, junto con el avance
     * del contador. O queda todo, o no queda nada: una guia a medias con el
     * numero ya consumido dejaria un hueco en la secuencia que hay que
     * justificar ante el SRI.
     */
    public final String saveDispatch(final DispatchInfo dispatch) throws BasicException {

        Transaction t;
        t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {

                var sequence = getNextTicketIndex(dispatch.getUser().getId(), dispatch.getCode());

                dispatch.setSerieNumber(dispatch.getSerie()
                        .concat(String.format(dispatch.getFormatNumberDigits(), sequence))
                );
                dispatch.setAccessKey(dispatch.buildAccessKey());

                new PreparedSentence(s,
                        "INSERT INTO dispatches "
                        + "(id, dispatcher_id, code, serie_number, date_dispatch, "
                        + "date_end_dispatch, address_start, access_key, observation, "
                        + "transfer_reason, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, dispatch.getId());
                                setString(2, dispatch.getDispatcherId());
                                setString(3, dispatch.getCode());
                                setString(4, dispatch.getSerieNumber());
                                setTimestamp(5, dispatch.getDateDispatch());
                                setTimestamp(6, dispatch.getDateEndDispatch());
                                setString(7, dispatch.getAddressStart());
                                setString(8, dispatch.getAccessKey());
                                setString(9, dispatch.getObservation());
                                setString(10, dispatch.getTransferReason());
                                setBoolean(11, dispatch.getStatus());
                            }
                        });

                SentenceExec detailInsert = new PreparedSentence(s,
                        "INSERT INTO dispatches_detail "
                        + "(dispatches_id, line, reference_code, reference_number, "
                        + "transfer_reason) "
                        + "VALUES (?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE);

                for (int i = 0; i < dispatch.getLines().size(); i++) {
                    final int line = i;
                    final DispatchLineInfo detail = dispatch.getLines().get(i);

                    detailInsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, dispatch.getId());
                            setInt(2, line);
                            setString(3, detail.getReferenceCode());
                            setString(4, detail.getReferenceNumber());
                            setString(5, dispatch.getTransferReason());
                        }
                    });
                }

                return dispatch.getSerieNumber();
            }
        };

        return t.execute().toString();
    }

    /**
     * Un lugar de partida para el combo: se muestra el nombre completo pero se
     * guarda solo la direccion, que es lo que espera address_start.
     */
    public static class AddressStartInfo
            implements com.unicenta.data.loader.IKeyed {

        private final String address;
        private final String label;

        public AddressStartInfo(String address, String label) {
            this.address = address;
            this.label = label;
        }

        @Override
        public Object getKey() {
            return address;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * Fila del combo de transportistas: la clave es el id, el texto visible
     * es el nombre con la placa.
     */
    public static class DispatcherComboInfo
            implements com.unicenta.data.loader.IKeyed {

        private final String id;
        private final String name;
        private final String plate;

        public DispatcherComboInfo(String id, String name, String plate) {
            this.id = id;
            this.name = name;
            this.plate = plate;
        }

        @Override
        public Object getKey() {
            return id;
        }

        @Override
        public String toString() {
            return plate == null || plate.isEmpty() ? name : name + " - " + plate;
        }
    }
}
