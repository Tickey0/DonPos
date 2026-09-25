package dev.joguenco.http.client.authorization;

import com.unicenta.basic.BasicException;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.ticket.TicketInfo;
import dev.joguenco.http.client.HttpClientSubscription;
import java.awt.HeadlessException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author <Jorge Luis from https://resolvedor.dev>
 */
@Slf4j
public class AuthorizeClient {

    private final String userAgent;
    private final AppView appView;
    public static final String SERVICE_NAME = "Authorize";
    private static final String API_KEY_METHOD = "X-API-KEY";

    public AuthorizeClient(AppView appView) {
        this.appView = appView;
        this.userAgent = AppLocal.APP_NAME + "/" + AppLocal.APP_VERSION;
    }

    // Del ticket solo hacian falta dos datos, asi que se los paso al metodo de
    // abajo. La retencion, la liquidacion y la guia no son tickets y necesitan
    // entrar por ahi.
    public StatusResponse post(TicketInfo ticket) {
        return post(ticket.getCode(), ticket.getSerieNumber());
    }

    public StatusResponse post(String code, String number) {
        try {
            var httpClient = new HttpClientSubscription(appView, SERVICE_NAME);

            if (!httpClient.isActive(SERVICE_NAME)) {
                return new StatusResponse("Service is disable");
            }

            final var document = new Document(code, number);

            // Con X-API-KEY no hace falta el login. La clave viaja en la cabecera, asi
            // que nos ahorramos una llamada al servidor, dos lecturas y una escritura en
            // su base de datos por cada documento.
            if (API_KEY_METHOD.equals(httpClient.getAuthenticationMethod())) {
                return resultado(authorizeWithApiKey(httpClient, document), document.getCode());
            }

            Response<AuthTokens> responseLogin = login(httpClient);
            if (!responseLogin.isSuccessful()) {
                return new StatusResponse("Error al iniciar sesión en el servicio de autorización");
            }

            AuthTokens auth = responseLogin.body();
            return resultado(
                    authorize(httpClient, auth.getAccessToken(), document),
                    document.getCode());

        } catch (IllegalArgumentException | HeadlessException | IOException | BasicException ex) {
            log.error(this.getClass().getName() + " " + ex.getMessage());
            return new StatusResponse(ex.getMessage());
        }
    }

    // Traduce la respuesta del servidor al mensaje que ve el cajero. Un null aqui
    // significa que el tipo de documento no es de los que se autorizan.
    private StatusResponse resultado(Response<StatusResponse> response, String code) {
        if (response == null) {
            return new StatusResponse("Tipo de documento no soportado para autorizar");
        }

        if (response.isSuccessful()) {
            return response.body();
        }

        return new StatusResponse("Error al procesar " + nombreDocumento(code));
    }

    // El nombre que ve el cajero. Antes solo habia dos documentos y salia con un
    // ternario; ahora son seis y asi no hay que tocarlo cada vez.
    private String nombreDocumento(String code) {
        if (code == null) {
            return "el documento";
        }

        switch (code) {
            case "FV":  return "la factura";
            case "DV":  return "la nota de crédito";
            case "ND":  return "la nota de débito";
            case "LQ":  return "la liquidación de compra";
            case "RT":  return "la retención";
            case "GUI": return "la guía de remisión";
            default:    return "el documento";
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

    private Response<StatusResponse> authorizeWithApiKey(HttpClientSubscription httpClient, Document document) throws IOException {
        var service = httpClient.generator().createServiceWithApiKey(
                AuthorizationService.class,
                httpClient.getToken(),
                userAgent);

        // Cada codigo de DonPos tiene su ruta en RoQui. El que no este en la lista
        // sale por el default y el cajero ve "Tipo de documento no soportado".
        // Comparo sobre una cadena vacia si viene null, que el switch revienta.
        final var code = document.getCode() == null ? "" : document.getCode();

        Call<StatusResponse> callAuthorize;

        switch (code) {
            case "FV":
                callAuthorize = service.autorizeInvoiceV2(document);
                break;
            case "DV":
                callAuthorize = service.autorizeCreditNoteV2(document);
                break;
            case "ND":
                callAuthorize = service.autorizeDebitNoteV2(document);
                break;
            case "LQ":
                callAuthorize = service.autorizeLiquidationV2(document);
                break;
            case "RT":
                callAuthorize = service.autorizeWithholdV2(document);
                break;
            case "GUI":
                callAuthorize = service.autorizeDeliveryNoteV2(document);
                break;
            default:
                return null;
        }

        return callAuthorize.execute();
    }
}
