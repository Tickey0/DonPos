package dev.resolvedor.pos.sales;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
import com.unicenta.pos.ticket.ProductInfoExt;
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

    private Integer id;
    private ProductInfoExt product;
    private double priceSell;
    private int minimumQuantity;
    private double value;
    private String status;

    @Override
    public void readValues(DataRead dr) throws BasicException {
        product = new ProductInfoExt();
        
        id = dr.getInt(1);        
        product.setCode(dr.getString(2));
        product.setName(dr.getString(3));
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
