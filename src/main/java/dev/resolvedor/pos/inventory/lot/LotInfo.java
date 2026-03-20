package dev.resolvedor.pos.inventory.lot;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
import java.util.Date;
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
public class LotInfo implements SerializableRead {
    
    private String name;
    private Date expirationDate;
    private Boolean status;
    

    @Override
    public void readValues(DataRead dr) throws BasicException {
        name = dr.getString(1);
        expirationDate = dr.getTimestamp(2);
        status = dr.getBoolean(3);
    }
}
