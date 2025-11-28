package dev.joguenco.http.client;

import com.unicenta.basic.BasicException;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.util.AltEncrypter;
import dev.joguenco.pos.subscription.DataLogicSubscription;
import dev.joguenco.pos.subscription.SubscriptionInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientSubscription {

    private final DataLogicSubscription dlSubscription;
    private SubscriptionInfo subscription;
    private final String key = "cypherkey";

    public HttpClientSubscription(AppView app, String serviceName)  throws BasicException {
        dlSubscription = (DataLogicSubscription) app.getBean("dev.joguenco.pos.subscription.DataLogicSubscription");        
        this.subscription = dlSubscription.getSubscriptionInfoByName(serviceName);
    }

    public ServiceGenerator generator() {        
        return new ServiceGenerator(subscription.getUrl(), subscription.getTimeout());
    }

    public String getToken() {
        return subscription.getToken();
    }
    
    public String getUsername() {
        return subscription.getUsername();
    }

    public String getPassword() {
        AltEncrypter cypher = new AltEncrypter(key);
        return cypher.decrypt(subscription.getPassword());
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
