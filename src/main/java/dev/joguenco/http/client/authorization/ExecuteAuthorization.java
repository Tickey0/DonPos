package dev.joguenco.http.client.authorization;

import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.ticket.TicketInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <Jorge Luis from resolvedor.dev>
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
        send();
    }

    public Boolean send() {
        try {
            AuthorizeClient a = new AuthorizeClient();
            a.post(app, ticket);
            return true;
        } catch (Exception ex) {
            log.error(ExecuteAuthorization.class.getName() + " " + ex.getMessage());
            return false;
        }
    }
}
