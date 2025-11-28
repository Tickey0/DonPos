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
}
