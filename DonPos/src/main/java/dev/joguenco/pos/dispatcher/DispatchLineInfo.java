package dev.joguenco.pos.dispatcher;

import dev.resolvedor.util.PrintFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * Una factura dentro de una guia de remision (tabla dispatches_detail).
 *
 * Solo se guarda la referencia al documento: el destinatario y los productos se
 * sacan de la factura al armar el XML, para no duplicar datos que ya existen.
 */
@Getter
@Setter
public class DispatchLineInfo {

    private String referenceCode;
    private String referenceNumber;

    /**
     * Motivo propio de esta factura. Si va vacio se hereda el de la cabecera:
     * asi el usuario pone uno solo para todo el viaje, o uno distinto por
     * factura cuando hace falta.
     */

    // Solo para mostrar en la grilla, no se guardan
    private String customerName;
    private String customerTaxId;
    private String customerAddress;
    private Double total;
    private String transferReason;
    private java.util.Date date; // fecha de la factura, solo para mostrar

    /**
     * Los productos de esta factura.
     *
     * No se guardan: se leen de la factura al momento de imprimir, porque el
     * SRI pide el detalle de lo que viaja y la guia solo referencia el
     * documento.
     */
    private java.util.List<DispatchProductInfo> products = new java.util.ArrayList<>();

    public DispatchLineInfo() {
    }

    public DispatchLineInfo(String referenceCode, String referenceNumber) {
        this.referenceCode = referenceCode;
        this.referenceNumber = referenceNumber;
    }

    // -----------------------------------------------------------------------
    // Impresion
    // -----------------------------------------------------------------------

    /**
     * Documento que ampara el traslado: "FV 001-001-000000123".
     */
    public String printReference() {
        return (PrintFormat.text(referenceCode) + " "
                + PrintFormat.sequential(referenceNumber)).trim();
    }

    public String printCustomerName() {
        return PrintFormat.text(customerName);
    }

    public String printCustomerTaxId() {
        return PrintFormat.text(customerTaxId);
    }

    public String printCustomerAddress() {
        return PrintFormat.text(customerAddress);
    }

    /**
     * Fecha de emision de la factura que ampara el traslado.
     */
    public String printDate() {
        return PrintFormat.date(date);
    }

    @Override
    public String toString() {
        return referenceCode + " " + referenceNumber + " - " + customerName;
    }
}
