package dev.joguenco.pos.dispatcher;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Alineacion de las grillas de despacho.
 *
 * Por defecto Swing centra los titulos y alinea el contenido a la izquierda,
 * asi que el titulo queda flotando en el medio de su columna y la tabla se ve
 * desordenada. Aqui se alinean los dos igual, columna por columna.
 *
 * Vive aparte porque lo usan las tres pantallas de despacho.
 */
final class DispatchTables {

    private DispatchTables() {
    }

    /**
     * Aplica una alineacion por columna, en el mismo orden de la grilla.
     *
     * Se pasan constantes de SwingConstants: LEFT para texto, RIGHT para
     * numeros. Las columnas
     * de casilla no se tocan: su dibujante es el del check y reemplazarlo
     * dejaria la casilla como texto "true" o "false".
     */
    static void alinear(JTable tabla, int... alineaciones) {
        var encabezadoBase = tabla.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < alineaciones.length && i < tabla.getColumnCount(); i++) {
            final int alineacion = alineaciones[i];
            var columna = tabla.getColumnModel().getColumn(i);

            if (tabla.getModel().getColumnClass(i) != Boolean.class) {
                var celda = new DefaultTableCellRenderer();
                celda.setHorizontalAlignment(alineacion);
                columna.setCellRenderer(celda);
            }

            // El dibujante del titulo se envuelve, no se reemplaza: asi
            // conserva el fondo y el borde que le pone el look and feel.
            columna.setHeaderRenderer((table, value, selected, focus, fila, col) -> {
                var c = encabezadoBase.getTableCellRendererComponent(
                        table, value, selected, focus, fila, col);

                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(alineacion);
                }

                return c;
            });
        }
    }
}
