package com.signomix.out.connector;

import com.signomix.out.notification.CommandWebHookClient;
import com.signomix.user.User;
import com.signomix.util.HttpClientHelper;
import com.signomix.util.HttpClientHelperResponse;
import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.cricketmsf.util.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author greg
 */
public class UserServiceConnector extends OutboundAdapter implements UserConnectorIface, Adapter {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceConnector.class);
    
    private String endpoint;

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        endpoint=properties.getOrDefault("url", "");
    }

    @Override
    public User getUser(String userID) {
        if(endpoint.isEmpty()){
            logger.error("Service endpoint not configured");
            return null;
        }
        HttpClientHelper helper = new HttpClientHelper("Signomix MS", 10);
        HashMap<String, String> headers = new HashMap<>();
        HashMap<Object, Object> parameters = new HashMap<>();
        parameters.put("uid", userID);
        HttpClientHelperResponse response=response = helper.getData(endpoint, properties, parameters);
        if (response.code != 200) {
            logger.error("Problem getting user {} data. {} {}",userID, response.code, response.text);
            return null;
        } else {
            User user=null;
            try{
                user=(User)JsonReader.jsonToJava(response.text);
            }catch(ClassCastException ex){
                logger.error("Problem getting user {} data. {}",userID, ex.getMessage());
            }
            return user;
        }
    }
}
