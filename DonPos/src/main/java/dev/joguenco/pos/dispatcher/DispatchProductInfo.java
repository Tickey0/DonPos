package dev.joguenco.pos.dispatcher;

import com.unicenta.format.Formats;
import dev.resolvedor.util.PrintFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * Un producto que viaja dentro de una factura de la guia de remision.
 *
 * No se guarda en ninguna tabla: se arma al momento de imprimir, leyendo las
 * lineas de la factura. La guia solo referencia el documento, y duplicar aqui
 * los productos permitiria que se contradigan con la factura.
 *
 * Son los tres datos que el SRI pide en el detalle de la guia: codigo,
 * cantidad y descripcion.
 */
@Getter
@Setter
public class DispatchProductInfo {

    private String code;
    private String name;
    private Double units;

    public DispatchProductInfo() {
    }

    public DispatchProductInfo(String code, String name, Double units) {
        this.code = code;
        this.name = name;
        this.units = units;
    }

    /**
     * Codigo con el que el negocio identifica al producto.
     */
    public String printCode() {
        return PrintFormat.text(code);
    }

    /**
     * Descripcion del producto.
     */
    public String printName() {
        return PrintFormat.text(name);
    }

    /**
     * Cuantas unidades van en el viaje.
     */
    public String printUnits() {
        return units == null ? "" : Formats.DOUBLE.formatValue(units);
    }

    @Override
    public String toString() {
        return printUnits() + " " + printName();
    }
}
