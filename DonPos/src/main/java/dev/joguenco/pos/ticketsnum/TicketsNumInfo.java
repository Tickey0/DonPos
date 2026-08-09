package dev.joguenco.pos.ticketsnum;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
import lombok.Getter;

/**
 *
 * @author jorgeluis
 */
@Getter
public class TicketsNumInfo implements SerializableRead {

    private String code;
    private String serie;
    private int id;

    public TicketsNumInfo() {
    }

    public TicketsNumInfo(String code, String serie) {
        this.code = code;
        this.serie = serie;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        code = dr.getString(1);
        serie = dr.getString(2);
        id = dr.getInt(3);
    }
}
