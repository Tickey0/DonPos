package dev.joguenco.http.client.kent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Las dos rutas del servidor Kent (kent-brockman) que usa DonPos.
 */
public interface KentService {

    @POST("send_message")
    public Call<Void> sendMessage(@Body KentMessage message);

    @POST("clear_message")
    public Call<Void> clearMessage(@Body KentMessage message);
}
