/**
 * Copyright (C) Grzegorz Skorupa 2018.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.notification;

import java.util.HashMap;
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
/*
        StandardResult result = new StandardResult();
        Request r = new Request();
        r.properties.put("Content-Type", "application/x-www-form-urlencoded");
        r.method = "POST";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("token=").append(token).append("&");
            sb.append("user=").append(recipient).append("&");
            sb.append("message=").append(URLEncoder.encode(nodeName + ": " + message, "UTF-8"));
            r.setData(sb.toString());
            result = (StandardResult) send(r, false);
        } catch (AdapterException | UnsupportedEncodingException e) {
            return "ERROR: " + e.getMessage();
        }
        if (result.getCode() == 200) {
            return new String(result.getPayload());
        } else {
            return "ERROR: " + result.getCode() + " " + result.getPayload();
        }
*/
return null;
    }

    @Override
    public String getChatID(String recipent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
