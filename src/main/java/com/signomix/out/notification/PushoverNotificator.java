/**
 * Copyright (C) Grzegorz Skorupa 2018.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.notification;

import com.signomix.util.HttpClientHelper;
import com.signomix.util.HttpClientHelperResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Level;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author greg
 */
public class PushoverNotificator extends OutboundAdapter implements NotificationIface, Adapter {
    private static final Logger logger = LoggerFactory.getLogger(PushoverNotificator.class);
    private String url;
    private String token;
    private boolean ignoreCertificateCheck=true;
    boolean ready = false;

    public void loadProperties(HashMap<String, String> properties, String adapterName) {

        super.loadProperties(properties, adapterName);
        url = properties.getOrDefault("url", "");  //https://api.pushover.net/1/messages.json
        token = properties.getOrDefault("token", ""); // application token
        ignoreCertificateCheck=Boolean.parseBoolean(properties.getOrDefault("ignore-certificate-check", ""));
        if (url.isEmpty() || token.isEmpty()) {
            ready = false;
        } else {
            ready = true;
        }
        logger.info("\ttoken: " + token);
    }

    @Override
    public String send(String userID, String recipient, String nodeName, String message) {
        return send(recipient, nodeName, message);
    }

    public String send(String recipient, String nodeName, String message) {
        if (!ready) {
            logger.warn("not configured");
            return "ERROR: not configured";
        }
        HashMap<Object, Object> parameters = new HashMap<>();
        parameters.put("token", token);
        parameters.put("user", recipient);
        try {
            parameters.put("message", URLEncoder.encode(nodeName + ": " + message, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            return "ERROR: " +ex.getMessage();
        }
        HashMap<String, String> headers = new HashMap<>();
        
        HttpClientHelper helper = new HttpClientHelper("Signomix CE", 10);
        HttpClientHelperResponse response = helper.sendForm(url, headers, parameters);
        if(response.code!=200){
            logger.warn("response code {}, {}",response.code, response.text);
            return "ERROR: " + response.code + " " + response.text;
        }
        return response.text;
    }

    @Override
    public String getChatID(String recipent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
