package dev.joguenco.http.client.authorization;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Luis from https://joguenco.dev
 * @email jorgeluis@resolvedor.dev
 */
public class Login {

    @Getter @Setter private String username;
    @Getter @Setter private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
