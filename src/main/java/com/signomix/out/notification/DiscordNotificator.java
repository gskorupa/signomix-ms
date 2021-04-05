/**
 * Copyright (C) Grzegorz Skorupa 2020.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.notification;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;

public class DiscordNotificator extends OutboundAdapter implements NotificationIface, Adapter {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    protected HashMap<String, Object> statusMap = null;

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
    }

    @Override
    public String send(String userID, String recipient, String nodeName, String message) {
        return send(recipient, nodeName, message);
    }

    @Override
    public String send(String webhookUrl, String nodeName, String message) {

        String data = "{\"content\":\"" + message + "\",\"username\":\"" + nodeName + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(URI.create(webhookUrl))
                .setHeader("User-Agent", "Signomix") // add request header
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return "OK";
            } else {
                return "ERROR " + response.statusCode() + ": " + response.body();
            }
        } catch (IOException|InterruptedException ex) {
            return "ERROR connection error: " + ex.getMessage();
        }
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
