package dev.joguenco.http.client.kent;

import lombok.AllArgsConstructor;

/**
 * Lo que recibe Kent: el cajero y los datos del cliente que se muestran en su
 * tablet. Para limpiar la tablet se envia sin datos del cliente.
 */
@AllArgsConstructor
public class KentMessage {

    private String cashier;
    private KentCustomer message;
}
