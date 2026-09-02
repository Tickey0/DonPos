package dev.resolvedor.pos.inventory.management;

import java.math.BigDecimal;
import java.math.RoundingMode;
import dev.resolvedor.util.PrintFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * Una linea del comprobante de retencion (tabla withholds_detail).
 *
 * El porcentaje se guarda copiado del catalogo a proposito: si manana el SRI
 * cambia el 1.75% a 2%, las retenciones ya emitidas deben conservar el
 * porcentaje con el que salieron.
 */
@Getter
@Setter
public class WithholdLineInfo {

    private String withholdId;
    private Integer line;
    private String withholdTaxesId;
    private Double percentage;
    private Double baseValue;
    private Double withholdedValue;
    private Double taxRate;

    /**
     * Sustento tributario al que pertenece esta retencion.
     *
     * El SRI agrupa por sustento: cada docSustento del XML lleva uno solo y
     * sus retenciones adentro. Sin este dato no hay como repartir las lineas
     * cuando la compra mezcla varios sustentos.
     */
    private String taxSupport;

    // Solo para mostrar en la grilla, no se guardan en withholds_detail
    private String withholdTaxName;
    private String withholdTaxCode;
    private String taxType;

    public WithholdLineInfo() {
        percentage = 0.0;
        baseValue = 0.0;
        withholdedValue = 0.0;
        taxRate = -1.0;
    }

    public WithholdLineInfo(WithholdTaxInfo tax, Double baseValue) {
        this();
        setWithholdTax(tax);
        this.baseValue = baseValue;
        calculate();
    }

    public final void setWithholdTax(WithholdTaxInfo tax) {
        withholdTaxesId = tax.getId();
        withholdTaxName = tax.getName();
        withholdTaxCode = tax.getCode();
        taxType = tax.getTaxType();
        percentage = tax.getPercentage();
    }

    /**
     * valor retenido = base x porcentaje. Se recalcula cada vez que cambia la
     * base o el tipo, pero el usuario puede sobrescribir el resultado: el SRI
     * a veces obliga a redondeos que no salen de esta cuenta.
     */
    public final void calculate() {
        withholdedValue = round(baseValue * percentage / 100);
    }

    // -----------------------------------------------------------------------
    // Impresion
    // -----------------------------------------------------------------------

    /**
     * Nombre del concepto retenido, tal como esta en el catalogo.
     */
    public String printName() {
        return PrintFormat.text(withholdTaxName);
    }

    /**
     * Codigo del SRI para ese concepto (312, 3440, etc).
     */
    public String printCode() {
        return PrintFormat.text(withholdTaxCode);
    }

    /**
     * Codigo del sustento tributario al que pertenece la linea.
     */
    public String printTaxSupport() {
        return PrintFormat.text(taxSupport);
    }

    /**
     * Base imponible sobre la que se calculo la retencion.
     */
    public String printBaseValue() {
        return PrintFormat.currency(baseValue);
    }

    /**
     * Porcentaje aplicado: 1.75 sale como "1.75%".
     */
    public String printPercentage() {
        return PrintFormat.percentage(percentage);
    }

    /**
     * Valor efectivamente retenido.
     */
    public String printWithholdedValue() {
        return PrintFormat.currency(withholdedValue);
    }

    private Double round(Double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
