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
 * @author <Jorge Luis from https://resolvedor.dev>
 */
@Slf4j
public class AuthorizeClient {

    private final String userAgent;
    private final AppView appView;
    public static final String SERVICE_NAME = "Authorize";

    public AuthorizeClient(AppView appView) {
        this.appView = appView;        
        this.userAgent = AppLocal.APP_NAME + "/" + AppLocal.APP_VERSION;
    }

    public StatusResponse post(TicketInfo ticket) {        
        try {
            var httpClient = new HttpClientSubscription(appView, SERVICE_NAME);

            if (!httpClient.isActive(SERVICE_NAME)) {
                return new StatusResponse("Service is disable");
            }

            Response<AuthTokens> responseLogin = login(httpClient);
            if (responseLogin.isSuccessful()) {
                AuthTokens auth = responseLogin.body();

                final var document = new Document(ticket.getCode(), ticket.getSerieNumber());
                Response<StatusResponse> response;
                switch (ticket.getCode()) {                    
                    case "FV":
                        response = authorize(
                                httpClient,
                                auth.getAccessToken(),
                                document
                        );
                        if (response.isSuccessful()) {
                            return response.body();
                        } else {
                            return new StatusResponse("Error al procesar la factura");
                        }

                    case "DV":
                        response =  authorize(
                                httpClient,
                                auth.getAccessToken(),
                                document
                        );
                        if (response.isSuccessful()) {
                            return response.body();
                        } else {
                            return new StatusResponse("Error al procesar la nota de crédito");
                        }

                    default:
                        return new StatusResponse("Tipo de documento no soportado para autorizar");

                }
            } else {
                return new StatusResponse("Error al iniciar sesión en el servicio de autorización");
            }
        } catch (IllegalArgumentException | HeadlessException | IOException | BasicException ex) {
            log.error(this.getClass().getName() + " " + ex.getMessage());
            return new StatusResponse(ex.getMessage());
        }
    }

    private Response<AuthTokens> login(HttpClientSubscription httpClient) throws IOException {
        final var service = httpClient.generator()
                .createService(AuthorizationService.class, userAgent);

        final var callSync = service.login(
                new Login(
                        httpClient.getUsername(),
                        httpClient.getPassword())
        );

        return callSync.execute();
    }

    private Response<StatusResponse> authorize(HttpClientSubscription httpClient, String accessToken, Document document) throws IOException {
        var service = httpClient.generator().createService(
                AuthorizationService.class,
                accessToken,
                userAgent);

        if ("FV".equals(document.getCode())) {
            var callAuthorize = service.autorizeInvoice(document);
            return callAuthorize.execute();
        }
        else if ("DV".equals(document.getCode())) {
            var callAuthorize = service.autorizeCreditNote(document);
            return callAuthorize.execute();
        }
        
        return null;
    }
}
