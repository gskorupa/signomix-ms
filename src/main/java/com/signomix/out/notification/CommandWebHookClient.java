/**
 * Copyright (C) Grzegorz Skorupa 2020.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.notification;

import com.signomix.iot.Device;
import com.signomix.util.HttpClientHelper;
import com.signomix.util.HttpClientHelperResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author greg
 */
public class CommandWebHookClient extends OutboundAdapter implements CommandWebHookIface, Adapter {

    private static final Logger logger = LoggerFactory.getLogger(CommandWebHookClient.class);
    private boolean ready = false;
    private String endpointUrl = null;
    private boolean printResponse = false;

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        endpointUrl = properties.getOrDefault("url", "");
        this.properties.put("url", endpointUrl);
        logger.info("\turl: " + endpointUrl);
        printResponse = Boolean.parseBoolean(properties.getOrDefault("print-response", "false"));
        this.properties.put("print-response", "" + printResponse);
        logger.info("\tprint-response: " + printResponse);
        if (endpointUrl.isEmpty()) {
            ready = false;
        } else {
            ready = true;
        }
    }

    public boolean send(Device device, String payload, boolean hexRepresentation) {
        boolean isGlobalWebhook = false;
        String deviceEUI = device.getEUI();
        String url = device.getDownlink();
        String deviceKey = device.getKey();

        if (null == url || url.isBlank()) {
            url = endpointUrl;

        }

        if (printResponse) {
            if (isGlobalWebhook) {
                System.out.println("SENDING TO DEVICE WEBHOOK: " + deviceEUI + " " + url);
            } else {
                System.out.println("SENDING TO GLOBAL WEBHOOK: " + deviceEUI + " " + url);
            }
            System.out.println("REQUEST PAYLOAD:" + payload);
            System.out.println("HEX: " + hexRepresentation);
        }
        if (null == url || url.isBlank()) {
            return false;

        }
        String data;
        if (hexRepresentation) {
            data = payload.trim();
        } else {
            data = new String(Base64.getDecoder().decode(payload)).trim();
        }
        if (data.startsWith("{")) {
            return sendJson(url, deviceEUI, deviceKey, data);
        } else {
            return sendForm(url, deviceEUI, deviceKey, data);
        }
    }

    private boolean sendForm(String downlink, String deviceEUI, String deviceKey, String payload) {
        if (!ready) {
            logger.warn("not configured");
            return false;
        }

        HashMap<Object, Object> parameters = new HashMap<>();
        parameters.put("payload", payload);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-device-eui", deviceEUI);
        headers.put("Authorization", deviceKey);
        
        HttpClientHelper helper = new HttpClientHelper("Signomix CE", 10);
        HttpClientHelperResponse response = helper.sendForm(endpointUrl, headers, parameters);
        if(response.code!=200){
            logger.warn("response code {}, {}",response.code, response.text);
        }
        return response.code==200;
    }

    private boolean sendJson(String downlink, String deviceEUI, String deviceKey, String json) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-device-eui", deviceEUI);
        headers.put("Authorization", deviceKey);
        
        HttpClientHelper helper = new HttpClientHelper("Signomix CE", 10);
        HttpClientHelperResponse response = helper.sendJson(endpointUrl, headers, json);
        if(response.code!=200){
            logger.warn("response code {}, {}",response.code, response.text);
        }
        return response.code==200;
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

}
