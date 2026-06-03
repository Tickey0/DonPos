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
import com.unicenta.pos.inventory.InventoryLine;
import com.unicenta.pos.inventory.InventoryRecord;
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
                    "purchase_document", "purchase_reference", "purchase_date", "purchase_authorization", "observation", "status"},
                new Datas[]{Datas.STRING, Datas.INT, Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING,
                    Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.BOOLEAN},
                new Formats[]{Formats.STRING, Formats.INT, Formats.TIMESTAMP, Formats.INT, Formats.STRING, Formats.STRING,
                    Formats.STRING, Formats.STRING, Formats.TIMESTAMP, Formats.STRING, Formats.STRING, Formats.BOOLEAN},
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
                "select number, created_at, reason, supplier, purchase_tax_support, purchase_document,"
                + "purchase_reference, purchase_date, purchase_authorization, observation, status "
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
                        + "(id, number, reason, supplier, purchase_tax_support, purchase_document, purchase_reference, purchase_date, purchase_authorization, observation) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {

                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, purchase.getId());
                                setInt(2, purchase.getNumber());
                                setInt(3, purchase.getReason());
                                setString(4, purchase.getSupplier());
                                setString(5, purchase.getPurchaseTaxSupport());
                                setString(6, purchase.getPurchaseDocument());
                                setString(7, purchase.getPurchaseReference());
                                setTimestamp(8, purchase.getPurchaseDate());
                                setString(9, purchase.getPurchaseAuthorization());
                                setString(10, purchase.getObservation());
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

    public final SentenceList getPurchaseListByData(String data) {
        return new StaticSentence(s,
                "SELECT "
                + "p.id, "
                + "p.number purchase, "
                + "p.created_at saved, "
                + "s.name supplier, "
                + "p.purchase_date date, "
                + "d.name document, "
                + "p.purchase_reference reference, "
                + "p .observation "
                + "from purchases p "
                + "join suppliers s on s.id = p.supplier  "
                + "join document_types d on d.id = p.purchase_document",
                null,
                PurchaseInfo.getSerializerRead());
    }
}
