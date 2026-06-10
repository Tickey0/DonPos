package dev.resolvedor.pos.sales;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
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
public class VolumeDiscountInfo implements SerializableRead {

    private String id;
    private String code;
    private String name;
    private double priceSell;
    private int minimumQuantity;
    private double value;
    private String status;

    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        code = dr.getString(2);
        name = dr.getString(3);
        priceSell = dr.getDouble(4);
        minimumQuantity = dr.getInt(5);
        value = dr.getDouble(6);
        if (dr.getBoolean(7)) {
            status = "Active";
        } else {
            status = "Inactive";
        }
    }
}
