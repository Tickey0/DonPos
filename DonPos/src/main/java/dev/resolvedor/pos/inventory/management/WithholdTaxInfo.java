package dev.resolvedor.pos.inventory.management;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.IKeyed;
import com.unicenta.data.loader.SerializableRead;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Un tipo de retencion del catalogo del SRI (tabla withhold_taxes).
 *
 * Se usa para llenar el combo del dialogo de retencion. El campo taxType
 * ('IVA' o 'RENTA') decide sobre que base se calcula la linea:
 * RENTA -> el subtotal de la compra; IVA -> el valor del IVA.
 */
@Getter
@AllArgsConstructor
public class WithholdTaxInfo implements SerializableRead, IKeyed {

    private String id;
    private String name;
    private Double percentage;
    private String code;
    private String taxType;

    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        name = dr.getString(2);
        percentage = dr.getDouble(3);
        code = dr.getString(4);
        taxType = dr.getString(5);
    }

    @Override
    public Object getKey() {
        return getId();
    }

    /**
     * Codigo de impuesto que espera el SRI en el XML: 1 = Renta, 2 = IVA.
     */
    public String getTaxCode() {
        return "IVA".equals(taxType) ? "2" : "1";
    }

    @Override
    public String toString() {
        return code == null ? name : name + " (" + code + ")";
    }
}
