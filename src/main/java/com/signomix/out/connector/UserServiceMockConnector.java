package com.signomix.out.connector;

import com.signomix.user.User;
import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;

/**
 *
 * @author greg
 */
public class UserServiceMockConnector extends OutboundAdapter implements UserConnectorIface, Adapter {
    
    HashMap<String,User> users=new HashMap<>();
    
    @Override
    public void loadProperties(HashMap<String,String> properties, String adapterName){
        super.loadProperties(properties, adapterName);
    }
    
    @Override
    public User getUser(String userID){
        return users.get(userID);
    }
    
    public void addUser(User u){
        users.put(u.getUid(), u);
    }
}
