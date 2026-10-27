package dev.joguenco.http.client.kent;

import lombok.AllArgsConstructor;

/**
 * Los datos del cliente tal como el cajero los escribio en la ventana de Pagos.
 * Los nombres de los campos son los que lee la tablet.
 */
@AllArgsConstructor
public class KentCustomer {

    private String identification;
    private String name;
    private String email;
    private String address;
    private String phone;
}
