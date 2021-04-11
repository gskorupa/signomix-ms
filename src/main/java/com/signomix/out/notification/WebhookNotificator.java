/**
 * Copyright (C) Grzegorz Skorupa 2020.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.notification;

import com.cedarsoftware.util.io.JsonWriter;
import com.signomix.util.HttpClientHelper;
import com.signomix.util.HttpClientHelperResponse;
import java.util.HashMap;
import java.util.Map;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookNotificator extends OutboundAdapter implements NotificationIface, Adapter {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookNotificator.class);
    protected HashMap<String, Object> statusMap = null;
    private String url;
    
    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        url = properties.getOrDefault("url", "");
        logger.info("\turl: {}", url);
    }
    
    @Override
    public String send(String userID, String recipient, String nodeName, String message) {
        return send(recipient, nodeName, message);
    }
    
    @Override
    public String send(String webhookUrl, String deviceEUI, String jsonObject) {
        Map args = new HashMap();
        args.put(JsonWriter.TYPE, false);
        HashMap<String, String> data = new HashMap<>();
        data.put("eui", deviceEUI);
        data.put("message", jsonObject.trim());
        String json = JsonWriter.objectToJson(data, args);
        HashMap<String, String> headers = new HashMap<>();
        
        HttpClientHelper helper = new HttpClientHelper("Signomix CE", 10);
        HttpClientHelperResponse response = helper.sendJson(url, headers, json);
        if (response.code != 200) {
            logger.warn("response code {}, {}", response.code, response.text);
            return "ERROR " + response.code + ": " + response.text;
        }
        return "OK";
    }
    
    @Override
    public void updateStatusItem(String key, String value) {
        statusMap.put(key, value);
    }
    
    @Override
    public Map<String, Object> getStatus(String name) {
        if (statusMap == null) {
            statusMap = new HashMap<>();
            statusMap.put("name", name);
            statusMap.put("class", getClass().getName());
        }
        return statusMap;
    }
    
    @Override
    public String getChatID(String recipent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
