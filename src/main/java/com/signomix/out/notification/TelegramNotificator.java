/**
 * Copyright (C) Grzegorz Skorupa 2019.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.notification;

import com.signomix.util.HttpClientHelper;
import com.signomix.util.HttpClientHelperResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author greg
 */
public class TelegramNotificator extends OutboundAdapter implements NotificationIface, Adapter {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificator.class);
    private String token;
    private String url;
    private boolean ready = false;
    
    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        token = properties.getOrDefault("token", ""); // application token
        url = properties.getOrDefault("url", "");
        if (url.isEmpty() || token.isEmpty()) {
            ready = false;
        } else {
            ready = true;
        }
        logger.info("\turl: {}", url);
        logger.info("\ttoken: {}", token);
    }
    
    @Override
    public String send(String userID, String recipient, String nodeName, String message) {
        return send(recipient, nodeName, message);
    }
    
    @Override
    public String send(String recipient, String nodeName, String message) {
        if (!ready) {
            logger.warn("not configured");
            return "ERROR: not configured";
        }
        String chatID = recipient.substring(recipient.indexOf("@") + 1);
        String text;
        try {
            text = URLEncoder.encode("" + message, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage());
            return "ERROR: " + ex.getMessage();
        }
        HashMap<Object, Object> parameters = new HashMap<>();
        parameters.put("chat_id", chatID);
        parameters.put("text", nodeName + " " + text);

        HashMap<String, String> headers = new HashMap<>();
        String endpointUrl=url+"bot" + token + "/sendMessage";
        HttpClientHelper helper = new HttpClientHelper("Signomix CE", 10);
        HttpClientHelperResponse response = helper.getData(endpointUrl, headers, parameters);
        if(response.code!=200){
            logger.warn("response code {}, {}",response.code, response.text);
            return "ERROR " + response.code + ": " + response.text;
        }
        String json = response.text;
        return "OK";
    }
    
    public String getChatID(String recipent) {
        HashMap<Object, Object> parameters = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        String endpointUrl=url+"bot" + token + "/getUpdates";
        HttpClientHelper helper = new HttpClientHelper("Signomix CE", 10);
        HttpClientHelperResponse response = helper.getData(endpointUrl, headers, parameters);
        if(response.code!=200){
            logger.warn("response code {}, {}",response.code, response.text);
            return "ERROR " + response.code + ": " + response.text;
        }
        String json = response.text;
        JSONObject obj = new JSONObject(json);
        JSONArray arr = obj.getJSONArray("result");
        String chat_id = null;
        JSONObject tmp;
        if (!arr.isEmpty()) {
            for (int i = 0; i < arr.length(); i++) {
                tmp = arr.getJSONObject(i).getJSONObject("message").getJSONObject("chat");
                if (recipent.equals(tmp.getString("username"))) {
                    chat_id = "" + tmp.getLong("id");
                    break;
                }
            }
        }
        return chat_id;
    }
    
}
