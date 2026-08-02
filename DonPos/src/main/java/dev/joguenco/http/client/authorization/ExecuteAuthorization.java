package dev.joguenco.http.client.authorization;

import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.ticket.TicketInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <Jorge Luis from https://resolvedor.dev>
 */
@Slf4j
public class ExecuteAuthorization extends Thread {

    private AppView app;
    private TicketInfo ticket;

    public ExecuteAuthorization(AppView app, TicketInfo ticket) {
        this.app = app;
        this.ticket = ticket;
    }

    @Override
    public void run() {
        int attempts = 1;

        do {
            if (send()) {
                break;
            }
            try {
                Thread.sleep(60000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        } while (attempts++ < 3);
    }

    public Boolean send() {
        try {
            AuthorizeClient a = new AuthorizeClient(app);
            final var response = a.post(ticket);
            log.info(ticket.getCode() + " " + ticket.getSerieNumber() + " -> " + response.getStatus());

            if ("ERROR".equalsIgnoreCase(response.getStatus())) {
                return false;
            }

            return true;
        } catch (Exception ex) {
            log.error(ExecuteAuthorization.class.getName() + " " + ex.getMessage());
            return false;
        }
    }
}
