package dev.joguenco.http.client.authorization;

import com.unicenta.basic.BasicException;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.ticket.TicketInfo;
import dev.joguenco.http.client.HttpClientSubscription;
import java.awt.HeadlessException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

/**
 * @author Jorge Luis from https://joguenco.dev
 * @web jorgeluis@resolvedor.dev
 */
@Slf4j
public class AuthorizeClient {

    public StatusResponse post(AppView appView, TicketInfo ticket) {
        final var serviceName = "Authorize";
        try {
            var httpClient = new HttpClientSubscription(appView, serviceName);

            if (!httpClient.isActive(serviceName)) {
                return new StatusResponse("Service is disable");
            }

            final var userAgent = AppLocal.APP_NAME + "/" + AppLocal.APP_VERSION;

            var service =
                    httpClient.generator().createService(AuthorizationService.class, userAgent);

            var callSync =
                    service.login(new Login(httpClient.getUsername(), httpClient.getPassword()));

            Response<AuthTokens> response = callSync.execute();
            if (response.isSuccessful()) {
                AuthTokens auth = response.body();

                var document = new Document(ticket.getCode(), ticket.getSerieNumber());
                service =
                        httpClient
                                .generator()
                                .createService(
                                        AuthorizationService.class,
                                        auth.getAccessToken(),
                                        userAgent);

                var callAuthorize = service.autorizeInvoice(document);
                Response<StatusResponse> responseAuthorize = callAuthorize.execute();
                if (responseAuthorize.isSuccessful()) {
                    return responseAuthorize.body();
                } else {
                    return new StatusResponse("Unsuccessful response");
                }

            } else {
                return new StatusResponse("Unsuccessful response");
            }
        } catch (IllegalArgumentException | HeadlessException | IOException | BasicException ex) {
            log.error(this.getClass().getName() + " " + ex.getMessage());
            return new StatusResponse(ex.getMessage());
        }
    }
}
