package dev.joguenco.http.client.authorization;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Luis from https://joguenco.dev
 * @web jorgeluis@resolvedor.dev
 */
public class AuthTokens {

    @Getter @Setter private String accessToken;
    @Getter @Setter private String refreshToken;

    public AuthTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
