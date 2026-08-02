package dev.resolvedor.util;

/**
 *
 * @author Jorge Luis
 * @web https://resolvedor.dev
 * @mail jorgeluis@resolvedor.dev
 */
public class StringUtils {

    public static boolean isNumber(String strNumber) {
        if (strNumber == null) {
            return false;
        }
        try {
            Double.parseDouble(strNumber);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
