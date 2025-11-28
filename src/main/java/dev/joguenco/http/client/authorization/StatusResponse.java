package dev.joguenco.http.client.authorization;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Luis from https://joguenco.dev
 * @web jorgeluis@resolvedor.dev
 */
public class StatusResponse {

    @Getter @Setter private String status;

    public StatusResponse(String status) {
        this.status = status;
    }
}
