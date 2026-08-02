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
import com.unicenta.data.loader.SerializerWriteBasicExt;
import com.unicenta.data.loader.SerializerWriteParams;
import com.unicenta.data.loader.SerializerWriteString;
import com.unicenta.data.loader.Session;
import com.unicenta.data.loader.StaticSentence;
import com.unicenta.data.loader.TableDefinition;
import com.unicenta.data.loader.Transaction;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.BeanFactoryDataSingle;
import com.unicenta.pos.forms.DataLogicSales;
import com.unicenta.pos.inventory.InventoryLine;
import com.unicenta.pos.inventory.InventoryRecord;
import com.unicenta.pos.inventory.MovementReason;
import com.unicenta.pos.sales.TaxesLogic;
import com.unicenta.pos.ticket.ProductInfoExt;
import com.unicenta.pos.ticket.TaxInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Jorge Luis
 * @web https://resolvedor.dev
 * @mail jorgeluis@resolvedor.dev
 */
public class DataLogicPurchase extends BeanFactoryDataSingle {

    private Session s;
    private TableDefinition tdPurchases;

    protected Datas[] stockdiaryDatas;

    @Override
    public void init(Session s) {
        this.s = s;

        tdPurchases = new TableDefinition(s,
                "purchases",
                new String[]{"id", "number", "created_at", "reason", "supplier", "purchase_tax_support",
                    "purchase_document", "purchase_reference", "purchase_date", "purchase_authorization",
                    "observation", "location", "status"},
                new Datas[]{Datas.STRING, Datas.INT, Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING,
                    Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.STRING,
                    Datas.STRING, Datas.STRING, Datas.BOOLEAN},
                new Formats[]{Formats.STRING, Formats.INT, Formats.TIMESTAMP, Formats.INT, Formats.STRING, Formats.STRING,
                    Formats.STRING, Formats.STRING, Formats.TIMESTAMP, Formats.STRING,
                    Formats.STRING, Formats.STRING, Formats.BOOLEAN},
                new int[]{0});

        stockdiaryDatas = new Datas[]{
            Datas.STRING, Datas.TIMESTAMP, Datas.INT, Datas.STRING,
            Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE,
            Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING};
    }

    public final TableDefinition getTablePurchase() {
        return tdPurchases;
    }

    public final PreparedSentence getPurchaseInfo() {
        return new PreparedSentence(s,
                "select id, number, created_at, reason, supplier, purchase_tax_support, "
                + "purchase_document, purchase_reference, purchase_date, purchase_authorization, "
                + "location, observation, status "
                + "from purchases "
                + "where id = ?",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> {
                    var purchaseInfo = new PurchaseInfo();
                    purchaseInfo.readValues(dr);

                    return purchaseInfo;
                });
    }

    public final Integer savePurchase(final PurchaseInfo purchase, final InventoryRecord rec) throws BasicException {

        Transaction t;
        t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {
                new PreparedSentence(s,
                        "INSERT INTO receipts (ID, MONEY, DATENEW, PERSON) VALUES (?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {

                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, purchase.getId());
                                setString(2, purchase.getMoney());
                                setTimestamp(3, purchase.getCreatedAt());
                                setString(4, purchase.getUser().getId());
                            }
                        });

                new PreparedSentence(s,
                        "INSERT INTO purchases "
                        + "(id, number, reason, supplier, purchase_tax_support, "
                        + "purchase_document, purchase_reference, purchase_date, purchase_authorization, "
                        + "location, observation) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {

                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, purchase.getId());
                                setInt(2, purchase.getNumber());
                                setInt(3, purchase.getReason());
                                setString(4, purchase.getSupplier().getID());
                                setString(5, purchase.getPurchaseTaxSupport());
                                setString(6, purchase.getPurchaseDocument());
                                setString(7, purchase.getPurchaseReference());
                                setTimestamp(8, purchase.getPurchaseDate());
                                setString(9, purchase.getPurchaseAuthorization());
                                setString(10, purchase.getLocation());
                                setString(11, purchase.getObservation());
                            }
                        });

                SentenceExec purchaselineinsert = new PreparedSentence(s,
                        "INSERT INTO purchaselines (PURCHASE, LINE, "
                        + "PRODUCT, UNITS, PRICE, TAXID, LOT) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE);

                for (int i = 0; i < purchase.getInvLines().size(); i++) {
                    final int line = i;

                    InventoryLine inv = rec.getLines().get(i);

                    purchaselineinsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, purchase.getId());
                            setInt(2, line);
                            setString(3, inv.getProductID());
                            setDouble(4, inv.getMultiply());
                            setDouble(5, inv.getPrice());
                            setString(6, inv.getTax().getId());
                            setString(7, inv.getLot());
                        }
                    });

                    Object params = new Object[]{
                        UUID.randomUUID().toString(),
                        purchase.getCreatedAt(),
                        rec.getReason().getKey(),
                        rec.getLocation().getID(),
                        inv.getProductID(),
                        inv.getProductAttSetInstId(),
                        rec.getReason().samesignum(inv.getMultiply()),
                        inv.getPrice(),
                        rec.getUser(),
                        rec.getSupplier().getID(),
                        rec.getSupplierDoc(),
                        inv.getLot()
                    };

                    new PreparedSentence(s,
                            "UPDATE products SET pricebuy = ? "
                            + "WHERE id = ?",
                            new SerializerWriteBasicExt(stockdiaryDatas, new int[]{7, 4})).exec(params);

                    if (inv.isService() != true) {

                        int updateresult = ((Object[]) params)[5] == null
                                ? new PreparedSentence(s,
                                        "UPDATE stockcurrent SET UNITS = (UNITS + ?) "
                                        + "WHERE LOCATION = ? AND PRODUCT = ? AND LOT = ? "
                                        + "AND ATTRIBUTESETINSTANCE_ID IS NULL",
                                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4, 11})).exec(params)
                                : new PreparedSentence(s,
                                        "UPDATE stockcurrent SET UNITS = (UNITS + ?) "
                                        + "WHERE LOCATION = ? AND PRODUCT = ? "
                                        + "AND ATTRIBUTESETINSTANCE_ID = ? AND LOT = ?",
                                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4, 5, 11})).exec(params);

                        if (updateresult == 0) {
                            new PreparedSentence(s,
                                    "INSERT INTO stockcurrent (LOCATION, PRODUCT, "
                                    + "ATTRIBUTESETINSTANCE_ID, UNITS, LOT) "
                                    + "VALUES (?, ?, ?, ?, ?)",
                                    new SerializerWriteBasicExt(stockdiaryDatas, new int[]{3, 4, 5, 6, 11})).exec(params);
                        }
                        new PreparedSentence(s,
                                "INSERT INTO stockdiary (ID, DATENEW, REASON, LOCATION, PRODUCT, "
                                + "ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, AppUser, "
                                + "SUPPLIER, SUPPLIERDOC, LOT) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                new SerializerWriteBasicExt(stockdiaryDatas,
                                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})).exec(params);

                    }

                }

                return purchase.getNumber();
            }
        };

        return Integer.valueOf(t.execute().toString());
    }

    public PreparedSentence getPurchaseSequence() {
        return new PreparedSentence(s,
                "SELECT ifnull(max(number) + 1, 1) as sequence from purchases",
                null,
                (DataRead dr) -> dr.getInt(1));
    }

    public SentenceList getTaxSupportList() {
        return new StaticSentence(s,
                new QBFBuilder("SELECT id, CONCAT(id, ' - ', name) name from tax_supports "
                        + "where status = true "
                        + "order by id asc",
                        new String[]{"id", "name"}),
                new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING
        }),
                (DataRead dr) -> {
                    var taxSupport = new TaxSupportInfo(
                            dr.getString(1),
                            dr.getString(2)
                    );

                    return taxSupport;
                });
    }

    public SentenceList getDocumentTypeList() {
        return new StaticSentence(s,
                new QBFBuilder("SELECT id, CONCAT(id, ' - ', name) name from document_types "
                        + "where status = true "
                        + "order by id asc",
                        new String[]{"id", "name"}),
                new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING
        }),
                (DataRead dr) -> {
                    var taxSupport = new TaxSupportInfo(
                            dr.getString(1),
                            dr.getString(2)
                    );

                    return taxSupport;
                });
    }

    public final SentenceList getPurchaseListByData(Date startDate, Date endDate, String data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        var startDateFormatted = format.format(startDate);
        var endDateFormatted = format.format(endDate);
        var sql = "SELECT "
                + "p.id, "
                + "p.number purchase, "
                + "p.created_at saved, "
                + "s.id supplier_id, "
                + "s.name supplier, "
                + "p.purchase_date date, "
                + "d.name document, "
                + "p.purchase_reference reference, "
                + "p.observation "
                + "from purchases p "
                + "join suppliers s on s.id = p.supplier  "
                + "join document_types d on d.id = p.purchase_document "
                + "where DATE_FORMAT(p.created_at, '%Y-%m-%d') BETWEEN '" + startDateFormatted + "' and '" + endDateFormatted + "' "
                + "and (s.name like '%" + data + "%' or p.purchase_reference like '%" + data + "%')";

        return new StaticSentence(s,
                sql,
                null,
                PurchaseInfo.getSerializerRead());
    }

    public final PurchaseInfo loadPurchase(String purchaseId, DataLogicSales dlSales) throws BasicException {

        var purchase = (PurchaseInfo) getPurchaseInfo().find(purchaseId);
        SentenceList sentTax;
        TaxesLogic taxeslogic;

        sentTax = dlSales.getTaxList();
        java.util.List<TaxInfo> taxlist;

        taxlist = sentTax.list();
        taxeslogic = new TaxesLogic(taxlist);

        purchase.setInvLines(
                new PreparedSentence(s, "SELECT "
                        + "l.product, p.name, l.units, l.price, l.taxid, l.lot, tx.category "
                        + "FROM purchaselines l "
                        + "join products p on p.id = l.product "
                        + "join taxes tx on tx.id = l.taxid "
                        + "where l.purchase = ? "
                        + "order by l.line asc ",
                        SerializerWriteString.INSTANCE,
                        (DataRead dr) -> {
                            var product = new ProductInfoExt();
                            product.setID(dr.getString(1));
                            product.setName(dr.getString(2));

                            var tax = taxeslogic.getTaxInfo(dr.getString(7), null);
                            var line = new InventoryLine(
                                    product,
                                    dr.getDouble(3),
                                    Math.round(dr.getDouble(3) * dr.getDouble(4)),
                                    dr.getString(6),
                                    tax
                            );

                            return line;
                        })
                        .list(purchaseId));

        return purchase;
    }

    public final void deleteTicket(final PurchaseInfo purchase, DataLogicSales dlSales) throws BasicException {

        Transaction t;
        t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {

                for (int i = 0; i < purchase.getLinesCount(); i++) {
                    if (purchase.getLine(i).getProductID() != null) {
                        dlSales.getStockDiaryInsert().exec(new Object[]{
                            UUID.randomUUID().toString(),
                            new Date(),
                            MovementReason.OUT_MOVEMENT.getKey(),
                            purchase.getLocation(),
                            purchase.getLine(i).getProductID(),
                            purchase.getLine(i).getProductAttSetInstId(),
                            MovementReason.OUT_MOVEMENT.samesignum(purchase.getLine(i).getMultiply()),
                            purchase.getLine(i).getPrice(),
                            purchase.getUser().getName(),
                            purchase.getLine(i).getLot()
                        });
                    }
                }

                new StaticSentence(s,
                        "DELETE FROM purchases WHERE ID = ?",
                        SerializerWriteString.INSTANCE).exec(purchase.getId());
                new StaticSentence(s,
                        "DELETE FROM receipts WHERE ID = ?",
                        SerializerWriteString.INSTANCE).exec(purchase.getId());
                return null;
            }
        };
        t.execute();
    }
}
