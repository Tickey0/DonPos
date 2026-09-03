package dev.resolvedor.pos.inventory.management;

import com.unicenta.pos.ticket.UserInfo;
import dev.joguenco.pos.taxpayer.TaxpayerInfo;
import dev.joguenco.pos.establishment.EstablishmentInfo;
import dev.resolvedor.util.Module11;
import dev.resolvedor.util.PrintFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Cabecera del comprobante de retencion (tabla withholds).
 *
 * El proveedor no se guarda aqui: llega por purchases.supplier a traves de
 * purchaseId. Guardarlo dos veces permitiria que se contradigan.
 */
@Getter
@Setter
public class WithholdInfo {

    private String id;
    private String code;
    private String serieNumber;
    private String purchaseId;
    private Date dateWithhold;
    private String observation;
    private Date fiscalPeriod;
    private String accessKey;
    private Boolean status;

    // Datos de apoyo: se usan para armar el numero y la clave, no se guardan
    private String serie;
    private String formatNumberDigits;
    private UserInfo user;
    private TaxpayerInfo taxPayerInfo;
    private EstablishmentInfo establishment;
    private String environment; // Test -> 1; Production -> 2

    private List<WithholdLineInfo> lines;

    public WithholdInfo() {
        id = UUID.randomUUID().toString();
        code = "RT";
        lines = new ArrayList<>();
        dateWithhold = new Date();
        fiscalPeriod = new Date();
        status = true;
        user = new UserInfo("", "");
    }

    /**
     * Clave de acceso de 49 digitos. Mismo armado que PurchaseInfo, cambiando
     * el codigo de documento: 07 = comprobante de retencion.
     */
    public String buildAccessKey() {
        final var m11 = new Module11();
        try {

            var codeDocument = "07"; // Withholding receipt

            accessKey = new SimpleDateFormat("ddMMyyyy").format(getDateWithhold());
            accessKey = accessKey + codeDocument;
            accessKey = accessKey + getTaxPayerInfo().getIdentification();
            accessKey = accessKey + getEnvironment();
            accessKey = accessKey + getSerieNumber().replace("-", "");
            accessKey = accessKey + "12345678" + "1";
            accessKey = accessKey + m11.module11(accessKey);

            return accessKey;
        } catch (Exception e) {
            accessKey = "";
            return accessKey;
        }
    }

    public Double getTotalWithheld() {
        var total = lines.stream()
                .mapToDouble(WithholdLineInfo::getWithholdedValue)
                .sum();

        return BigDecimal.valueOf(total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // -----------------------------------------------------------------------
    // Impresion
    //
    // La plantilla no toca getters directos: los Date y los Double crudos se
    // imprimen feos, y un null en Velocity sale como "${withhold.getX()}".
    // -----------------------------------------------------------------------

    /**
     * Numero del comprobante con guiones: 001-001-000000002.
     */
    public String printSequential() {
        return PrintFormat.sequential(serieNumber);
    }

    /**
     * Fecha de emision de la retencion.
     */
    public String printDate() {
        return PrintFormat.date(dateWithhold);
    }

    /**
     * Periodo fiscal en MM/yyyy, que es como lo pide el SRI.
     */
    public String printFiscalPeriod() {
        return PrintFormat.fiscalPeriod(fiscalPeriod);
    }

    /**
     * Total retenido, con simbolo de moneda.
     */
    public String printTotalWithheld() {
        return PrintFormat.currency(getTotalWithheld());
    }

    public String printObservation() {
        return PrintFormat.text(observation);
    }

    public String printUser() {
        return user == null ? "" : PrintFormat.text(user.getName());
    }

    // --- Emisor ------------------------------------------------------------

    public String printLegalName() {
        return taxPayerInfo == null ? "" : taxPayerInfo.printLegalName();
    }

    public String printIdentification() {
        return taxPayerInfo == null ? "" : taxPayerInfo.printIdentification();
    }

    public String printForcedAccounting() {
        return taxPayerInfo == null ? "" : taxPayerInfo.printForcedAccounting();
    }

    public String printSpecialTaxpayer() {
        return taxPayerInfo == null ? "" : taxPayerInfo.printSpecialTaxpayer();
    }

    public String printRetentionAgent() {
        return taxPayerInfo == null ? "" : taxPayerInfo.printRetentionAgent();
    }

    public String printOther() {
        return taxPayerInfo == null ? "" : taxPayerInfo.printOther();
    }

    public String printComercialName() {
        return establishment == null ? "" : PrintFormat.text(establishment.getComercialName());
    }

    public String printEstablishmentAddress() {
        return establishment == null ? "" : PrintFormat.text(establishment.getAddress());
    }

    public String printEstablishmentPhone() {
        return establishment == null ? "" : PrintFormat.text(establishment.getPhone());
    }

    public String printEstablishmentEmail() {
        return establishment == null ? "" : PrintFormat.text(establishment.getEmail());
    }

    // --- Clave de acceso ---------------------------------------------------

    public String printAccessKeyLine1() {
        return PrintFormat.accessKeyLine1(accessKey);
    }

    public String printAccessKeyLine2() {
        return PrintFormat.accessKeyLine2(accessKey);
    }

    public String printEnvironment() {
        return PrintFormat.environment(environment);
    }

    @Override
    public String toString() {
        return code + " " + serieNumber + " - " + getTotalWithheld();
    }
}
