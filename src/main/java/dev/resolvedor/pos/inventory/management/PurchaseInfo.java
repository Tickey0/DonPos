package dev.resolvedor.pos.inventory.management;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
import com.unicenta.data.loader.SerializerRead;
import com.unicenta.format.Formats;
import com.unicenta.pos.inventory.InventoryLine;
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
    private String supplier;
    private String purchaseTaxSupport;
    private String purchaseDocument;
    private String purchaseReference;
    private Date purchaseDate;
    private String purchaseAuthorization;
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

    @Override
    public void readValues(DataRead dr) throws BasicException {
        number = dr.getInt(1);
        createdAt = dr.getTimestamp(2);
        reason = dr.getInt(3);
        supplier = dr.getString(4);
        purchaseTaxSupport = dr.getString(5);
        purchaseDocument = dr.getString(6);
        purchaseReference = dr.getString(7);
        purchaseDate = dr.getTimestamp(8);
        purchaseAuthorization = dr.getString(9);
        observation = dr.getString(10);
        status = dr.getBoolean(11);
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                PurchaseInfo purchase = new PurchaseInfo();

                purchase.id = dr.getString(1);
                purchase.number = dr.getInt(2);
                purchase.createdAt = dr.getTimestamp(3);
                purchase.supplier = dr.getString(4);
                purchase.purchaseDate = dr.getTimestamp(5);
                purchase.purchaseDocument = dr.getString(6);
                purchase.purchaseReference = dr.getString(7);
                purchase.observation = dr.getString(8);

                return purchase;
            }
        };
    }
}
