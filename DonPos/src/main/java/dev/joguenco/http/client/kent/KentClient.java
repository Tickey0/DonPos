package dev.joguenco.http.client.kent;

import com.unicenta.basic.BasicException;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.forms.AppView;
import dev.joguenco.http.client.HttpClientService;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;

/**
 * Muestra los datos del cliente en la tablet del cajero por medio del servidor
 * Kent, configurado en Mantenimiento > Servicios con el nombre "Kent".
 *
 * Los envios salen por detras en un solo hilo: el cajero nunca espera aunque el
 * servidor este apagado, y los mensajes llegan en orden (primero mostrar,
 * despues limpiar). Si un envio falla solo queda en el log y la venta sigue.
 */
@Slf4j
public class KentClient {

    private static final String SERVICE_NAME = "Kent";

    private static final ExecutorService sender = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, SERVICE_NAME);
        thread.setDaemon(true);
        return thread;
    });

    private KentService service;

    public KentClient(AppView app) {
        try {
            var httpClient = new HttpClientService(app, SERVICE_NAME);

            if (httpClient.isActive(SERVICE_NAME)) {
                // La API va en la cabecera X-API-KEY, igual que en Autorizar
                service = httpClient.generator().createServiceWithApiKey(
                        KentService.class,
                        httpClient.getToken(),
                        AppLocal.APP_NAME + "/" + AppLocal.APP_VERSION);
            }
        } catch (BasicException | IllegalArgumentException ex) {
            log.error(this.getClass().getName() + " " + ex.getMessage());
        }
    }

    // Falso cuando el servicio Kent no existe, esta desactivado o su URL es invalida
    public boolean isActive() {
        return service != null;
    }

    public void send(String cashier, KentCustomer customer) {
        execute(service.sendMessage(new KentMessage(cashier, customer)));
    }

    public void clear(String cashier) {
        execute(service.clearMessage(new KentMessage(cashier, null)));
    }

    private void execute(Call<Void> call) {
        sender.submit(() -> {
            try {
                var response = call.execute();

                // 401 = la API de Mantenimiento > Servicios no es la del servidor
                if (!response.isSuccessful()) {
                    log.warn(this.getClass().getName() + " HTTP " + response.code());
                }
            } catch (IOException | RuntimeException ex) {
                log.warn(this.getClass().getName() + " " + ex.getMessage());
            }
        });
    }
}
