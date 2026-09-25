package dev.joguenco.http.client.authorization;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author < Jorge Luis from http://joguenco.dev >
 */
public interface AuthorizationService {

    @POST("roqui/v1/login")
    public Call<AuthTokens> login(@Body Login login);

    @POST("roqui/v1/invoice/authorize")
    public Call<StatusResponse> autorizeInvoice(@Body Document document);
    
    @POST("roqui/v1/credit/note/authorize")
    public Call<StatusResponse> autorizeCreditNote(@Body Document document);

    // Rutas v2: llevan la clave en la cabecera y no necesitan login previo.
    @POST("roqui/v2/invoice/authorize")
    public Call<StatusResponse> autorizeInvoiceV2(@Body Document document);

    @POST("roqui/v2/creditnote/authorize")
    public Call<StatusResponse> autorizeCreditNoteV2(@Body Document document);

    @POST("roqui/v2/debitnote/authorize")
    public Call<StatusResponse> autorizeDebitNoteV2(@Body Document document);

    @POST("roqui/v2/liquidation/authorize")
    public Call<StatusResponse> autorizeLiquidationV2(@Body Document document);

    @POST("roqui/v2/withhold/authorize")
    public Call<StatusResponse> autorizeWithholdV2(@Body Document document);

    @POST("roqui/v2/deliverynote/authorize")
    public Call<StatusResponse> autorizeDeliveryNoteV2(@Body Document document);
}
