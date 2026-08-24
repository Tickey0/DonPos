package dev.resolvedor.pos.inventory.management;

import com.unicenta.pos.ticket.UserInfo;
import dev.joguenco.pos.taxpayer.TaxpayerInfo;
import dev.resolvedor.util.Module11;
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

    @Override
    public String toString() {
        return code + " " + serieNumber + " - " + getTotalWithheld();
    }
}
