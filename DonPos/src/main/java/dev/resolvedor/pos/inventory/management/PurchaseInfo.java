package dev.resolvedor.pos.inventory.management;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
import com.unicenta.data.loader.SerializerRead;
import com.unicenta.format.Formats;
import com.unicenta.pos.inventory.InventoryLine;
import com.unicenta.pos.suppliers.SupplierInfo;
import com.unicenta.pos.ticket.UserInfo;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge Luis
 * @web https://resolvedor.dev
 * @mail jorgeluis@resolvedor.dev
 */
@Getter
@Setter
public class PurchaseInfo implements SerializableRead {

    private String id;

    private Integer number;
    private Date createdAt;
    private Integer reason;
    private SupplierInfo supplier;
    private String purchaseTaxSupport;
    private String purchaseDocument;
    private String purchaseReference;
    private Date purchaseDate;
    private String purchaseAuthorization;
    private String location;
    private String observation;
    private Boolean status;

    private String money;
    private UserInfo user;

    private List<InventoryLine> invLines;

    public PurchaseInfo() {
        id = UUID.randomUUID().toString();
        invLines = new java.util.ArrayList<>();
        createdAt = new Date();
        user = new UserInfo("", "");
    }

    public double getSubTotal() {
        double sum = 0.0;
        sum = invLines.stream().map((line)
                -> line.getSubValue()).reduce(sum, (accumulator, _item)
                -> accumulator + _item);
        return sum;
    }

    public double getTax() {

        double sum = 0.0;

        sum = invLines.stream().map((line)
                -> line.getTaxValue()).reduce(sum, (accumulator, _item)
                -> accumulator + _item);

        return sum;
    }

    public double getTotal() {
        return getSubTotal() + getTax();
    }

    public String printSubTotal() {
        return Formats.CURRENCY.formatValue(getSubTotal());
    }

    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }

    public String printTotal() {
        return Formats.CURRENCY.formatValue(getTotal());
    }

    public InventoryLine getLine(int index) {
        return invLines.get(index);
    }

    public int getLinesCount() {
        return invLines.size();
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        number = dr.getInt(2);
        createdAt = dr.getTimestamp(3);
        reason = dr.getInt(4);
        supplier = new SupplierInfo(dr.getString(5));
        purchaseTaxSupport = dr.getString(6);
        purchaseDocument = dr.getString(7);
        purchaseReference = dr.getString(8);
        purchaseDate = dr.getTimestamp(9);
        purchaseAuthorization = dr.getString(10);
        location = dr.getString(11);
        observation = dr.getString(12);
        status = dr.getBoolean(13);
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                PurchaseInfo purchase = new PurchaseInfo();

                purchase.id = dr.getString(1);
                purchase.number = dr.getInt(2);
                purchase.createdAt = dr.getTimestamp(3);
                purchase.supplier = new SupplierInfo(dr.getString(4));
                purchase.getSupplier().setName(dr.getString(5));
                purchase.purchaseDate = dr.getTimestamp(6);
                purchase.purchaseDocument = dr.getString(7);
                purchase.purchaseReference = dr.getString(8);
                purchase.observation = dr.getString(9);

                return purchase;
            }
        };
    }

    @Override
    public String toString() {
        var createdAtFormated = Formats.DATE.formatValue(createdAt);
        var purchaseDateFormated = Formats.DATE.formatValue(purchaseDate);

        return "# " + number + " " + createdAtFormated + " | " + supplier.getName() + " - " + purchaseDocument + " " + purchaseReference + " " + purchaseDateFormated + " - " + observation;
    }
}
