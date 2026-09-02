package dev.resolvedor.util;

import com.unicenta.format.Formats;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formatos de impresion compartidos por los documentos que emite el sistema.
 *
 * Existe para que la liquidacion, la retencion y la guia de remision impriman
 * igual que la factura sin repetir tres veces la misma cuenta.
 *
 * Todo devuelve String y nunca null. Es a proposito: cuando Velocity encuentra
 * null en una plantilla no imprime vacio, imprime el texto crudo de la variable
 * (${objeto.metodo()}) y eso sale tal cual en el papel.
 */
public final class PrintFormat {

    /** La clave de acceso del SRI siempre tiene 49 digitos. */
    private static final int ACCESS_KEY_LENGTH = 49;

    /** Se parte en 39 + 10 porque el rollo es de 40 columnas. */
    private static final int ACCESS_KEY_SPLIT = 39;

    /** Establecimiento (3) + punto de emision (3) + secuencial (9). */
    private static final int SEQUENTIAL_LENGTH = 15;

    private PrintFormat() {
        // Solo metodos estaticos: no tiene sentido instanciarla.
    }

    /**
     * Texto seguro para la plantilla: null y espacios sobrantes salen vacios.
     */
    public static String text(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Fecha corta, con el formato que el usuario tiene configurado.
     */
    public static String date(Date value) {
        return Formats.DATE.formatValue(value);
    }

    /**
     * Fecha con hora, igual que la que imprime la factura.
     */
    public static String dateTime(Date value) {
        return Formats.TIMESTAMP.formatValue(value);
    }

    /**
     * Periodo fiscal en MM/yyyy, que es como lo pide el SRI en la retencion.
     */
    public static String fiscalPeriod(Date value) {
        return value == null ? "" : new SimpleDateFormat("MM/yyyy").format(value);
    }

    /**
     * Importe con el simbolo de moneda configurado.
     */
    public static String currency(Double value) {
        return Formats.CURRENCY.formatValue(value);
    }

    /**
     * Porcentaje con dos decimales: 1.75 sale como "1.75%".
     */
    public static String percentage(Double value) {
        if (value == null) {
            return "";
        }

        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString() + "%";
    }

    /**
     * Numero de documento con guiones: 001001000000002 sale 001-001-000000002.
     *
     * En la base se guarda pegado, que es como lo arma la secuencia, pero al
     * SRI y al cliente se les muestra separado en establecimiento, punto de
     * emision y secuencial. Si el numero todavia no tiene los 15 digitos se
     * devuelve tal cual, para no esconder un dato a medio armar.
     */
    public static String sequential(String serieNumber) {
        var value = text(serieNumber);

        if (value.length() != SEQUENTIAL_LENGTH) {
            return value;
        }

        return value.substring(0, 3) + "-"
                + value.substring(3, 6) + "-"
                + value.substring(6, SEQUENTIAL_LENGTH);
    }

    /**
     * Primeros 39 digitos de la clave de acceso.
     *
     * Los 49 no entran en una linea de 40 columnas, asi que se parte en dos
     * igual que en la factura. Si la clave no esta completa devuelve vacio, y
     * asi la plantilla se salta todo el bloque en vez de imprimir basura.
     */
    public static String accessKeyLine1(String accessKey) {
        var value = text(accessKey);

        return value.length() == ACCESS_KEY_LENGTH
                ? value.substring(0, ACCESS_KEY_SPLIT)
                : "";
    }

    /**
     * Los 10 digitos que sobran de la clave de acceso.
     */
    public static String accessKeyLine2(String accessKey) {
        var value = text(accessKey);

        return value.length() == ACCESS_KEY_LENGTH
                ? value.substring(ACCESS_KEY_SPLIT, ACCESS_KEY_LENGTH)
                : "";
    }

    /**
     * Ambiente del SRI: 1 es pruebas, 2 es produccion.
     */
    public static String environment(String environment) {
        switch (text(environment)) {
            case "1":
                return "Ambiente: Pruebas";
            case "2":
                return "Ambiente: Producción";
            default:
                return "";
        }
    }
}
