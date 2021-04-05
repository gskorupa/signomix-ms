package com.signomix.out.connector;

import com.signomix.user.User;


/**
 *
 * @author greg
 */
public interface UserConnectorIface {
    
    public User getUser(String userID);
    
}
