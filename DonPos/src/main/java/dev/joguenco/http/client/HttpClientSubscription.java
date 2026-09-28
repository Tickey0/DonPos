package dev.joguenco.http.client;

import com.unicenta.basic.BasicException;
import com.unicenta.pos.forms.AppView;
import dev.joguenco.pos.subscription.DataLogicSubscription;
import dev.joguenco.pos.subscription.SubscriptionInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <Jorge Luis from https://resolvedor.dev>
 */
@Slf4j
public class HttpClientSubscription {

    private final DataLogicSubscription dlSubscription;
    private SubscriptionInfo subscription;

    public HttpClientSubscription(AppView app, String serviceName)  throws BasicException {
        dlSubscription = (DataLogicSubscription) app.getBean("dev.joguenco.pos.subscription.DataLogicSubscription");        
        this.subscription = dlSubscription.getSubscriptionInfoByName(serviceName);
    }

    public ServiceGenerator generator() {        
        return new ServiceGenerator(subscription.getUrl(), subscription.getTimeout());
    }

    // Dice como se autentica este servicio: Token lo usa ReIdi, y X-API-KEY
    // manda la clave en la cabecera para autorizar documentos.
    public String getAuthenticationMethod() {
        return subscription.getAuthenticationMethod();
    }

    public String getToken() {
        return subscription.getToken();
    }

    public Boolean isActive(String serviceName) {
        try {
            var isActive = dlSubscription.getSubscriptionStatusByName(serviceName);
            
            if (isActive == null)
                return false;
            
            return isActive;
            
        } catch (BasicException ex) {
            log.error(this.getClass().getName() + " " + ex.getMessage());
            return false;
        }
    }
}
