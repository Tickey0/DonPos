package dev.joguenco.pos.dispatcher;

import com.unicenta.pos.ticket.UserInfo;
import dev.joguenco.pos.taxpayer.TaxpayerInfo;
import dev.resolvedor.util.Module11;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Cabecera de la guia de remision (tabla dispatches).
 *
 * Documenta un traslado: quien transporta, desde donde, entre que fechas, y que
 * facturas van en ese viaje.
 */
@Getter
@Setter
public class DispatchInfo {

    /**
     * Motivo del traslado que se propone al abrir la pantalla.
     *
     * Esta pantalla despacha facturas de venta, asi que casi siempre es este.
     * No se quema: el SRI lo pide como texto libre en motivoTraslado y el
     * usuario lo puede cambiar si el traslado es por otra cosa.
     */
    public static final String TRANSFER_REASON = "Ventas de Productos";

    private String id;
    private String dispatcherId;
    private String code;
    private String serieNumber;
    private Date dateDispatch;
    private Date dateEndDispatch;
    private String addressStart;
    private String accessKey;
    private String observation;
    private String transferReason;
    private Boolean status;

    // Datos de apoyo: sirven para armar el numero y la clave, no se guardan
    private String serie;
    private String formatNumberDigits;
    private UserInfo user;
    private TaxpayerInfo taxPayerInfo;
    private String environment; // Test -> 1; Production -> 2
    private String dispatcherLabel; // nombre y placa, solo para la busqueda
    private Integer lineCount;      // cuantas facturas trae, para la busqueda

    private List<DispatchLineInfo> lines;

    public DispatchInfo() {
        id = UUID.randomUUID().toString();
        code = "GUI";
        lines = new ArrayList<>();
        dateDispatch = new Date();
        dateEndDispatch = new Date();
        status = true;
        user = new UserInfo("", "");
    }

    /**
     * Clave de acceso de 49 digitos. Mismo armado que PurchaseInfo y
     * WithholdInfo, cambiando el codigo de documento: 06 = guia de remision.
     */
    public String buildAccessKey() {
        final var m11 = new Module11();
        try {

            var codeDocument = "06"; // Delivery note

            accessKey = new SimpleDateFormat("ddMMyyyy").format(getDateDispatch());
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

    @Override
    public String toString() {
        return code + " " + serieNumber + " - " + lines.size();
    }
}
