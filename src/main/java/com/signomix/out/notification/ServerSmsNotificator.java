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
public class ServerSmsNotificator extends OutboundAdapter implements NotificationIface, Adapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerSmsNotificator.class);
    private String url;
    private String login;
    private String password;
    boolean ready = false;

    public void loadProperties(HashMap<String, String> properties, String adapterName) {

        super.loadProperties(properties, adapterName);
        url = properties.getOrDefault("url", "");
        login = properties.getOrDefault("login", ""); //
        password = properties.getOrDefault("password", ""); //

        if (url.isEmpty() || login.isEmpty() || password.isEmpty()) {
            ready = false;
        } else {
            ready = true;
        }
    }

    /*
    public String send2(String recipient, String nodeName, String message) {
        try {
            String nName = (null == nodeName || nodeName.isEmpty() || "_".equals(nodeName)) ? "" : nodeName + ": ";
            StringBuilder sb = new StringBuilder(endpointURL).append("?")
                    .append("login=").append(login).append("&")
                    .append("haslo=").append(password).append("&")
                    .append("akcja=wyslij_sms").append("&")
                    .append("numer=").append(URLEncoder.encode(recipient, "UTF-8")).append("&")
                    .append("wiadomosc=").append(URLEncoder.encode(nName + message, "UTF-8"));
            HttpClient client = HttpClient.newHttpClient();
            //HttpClient client = HttpClient.newBuilder().sslContext(SSLContext.getDefault()).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sb.toString()))
                    .build();
            HttpResponse<String> response
                    = client.send(request, BodyHandlers.ofString());

            System.out.println(response.body());
            if (response.statusCode() == 200) {
                Result smsResult = null;
                smsResult = (Result) JsonReader.jsonToJava(response.body());
                if (!smsResult.success) {
                    return "ERROR: " + smsResult.items.get(0).text;
                }
                return "OK";
            } else {
                return "ERROR: " + response.statusCode() + " " + response.body();
            }
        } catch (Exception e) {
            Kernel.getInstance().dispatchEvent(Event.logSevere(this.getClass().getSimpleName(), e.getMessage()));
            return "ERROR: " + e.getMessage();
        }
    }
     */
    public String send(String userID, String recipient, String nodeName, String message) {

        if (!ready) {
            logger.warn("not configured");
            return "ERROR: not configured";
        }
        setCertificateCheck(false);
        StandardResult result = new StandardResult();
        String nName = (null == nodeName || nodeName.isEmpty() || "_".equals(nodeName)) ? "" : nodeName + ": ";
        try {
            StringBuilder sb = new StringBuilder("")
                    .append("username=").append(login).append("&")
                    .append("password=").append(password).append("&")
                    .append("phone=").append(URLEncoder.encode(recipient, "UTF-8")).append("&")
                    .append("sender=Signomix&")
                    .append("text=").append(URLEncoder.encode(nName + message, "UTF-8"));
            Request req = new Request()
                    .setMethod("GET")
                    .setUrl(endpointURL + "?" + sb.toString());

            System.out.println("SENDING SMS USING " + sb.toString());
            result = (StandardResult) send(req, false);
        } catch (AdapterException | UnsupportedEncodingException e) {
            return "ERROR: " + e.getMessage();
        }
        String response = new String(result.getPayload());
        if (result.getCode() == 200) {
            try {
                //ERROR:
                //{"error":{"code":1001,"type":"InvalidUser","message":"Nieprawid\u0142owy login lub has\u0142o"}}
                //OK:
                //{"success":true,"queued":1,"unsent":0}
                JsonObject o = (JsonObject) JsonReader.jsonToJava(response);
                if (o.containsKey("success")) {
                    boolean success = (Boolean) o.get("success");
                    if (!success) {
                        return "ERROR: " + "";//TODO: przyczyna
                    }
                } else if (o.containsKey("error")) {
                    return "ERROR: ";
                } else {
                    return "ERROR: unknown";
                }
            } catch (Exception e) {
                logger.error("deserialization problem: " + e.getMessage());
            }
            Kernel.getInstance().dispatch(new SmsSent(userID));
            return "OK";
        } else {
            return "ERROR: " + result.getCode() + " " + result.getPayload();
        }
    }

    public String send3(String recipient, String nodeName, String message) {

        if (!ready) {
            logger.warn("not configured");
            return "ERROR: not configured";
        }
        setCertificateCheck(false);
        StandardResult result = new StandardResult();
        String nName = (null == nodeName || nodeName.isEmpty() || "_".equals(nodeName)) ? "" : nodeName + ": ";
        try {
            StringBuilder sb = new StringBuilder("")
                    .append("login=").append(login).append("&")
                    .append("haslo=").append(password).append("&")
                    .append("akcja=wyslij_sms").append("&")
                    .append("numer=").append(URLEncoder.encode(recipient, "UTF-8")).append("&")
                    .append("wiadomosc=").append(URLEncoder.encode(nName + message, "UTF-8"));
            Request req = new Request()
                    .setMethod("POST")
                    .setProperty("Content-type", "application/x-www-form-urlencoded")
                    .setData(sb.toString()) //data must be added to POST or PUT requests//
                    .setUrl(endpointURL);

            System.out.println("SENDING SMS USING " + sb.toString());
            result = (StandardResult) send(req);
        } catch (AdapterException | UnsupportedEncodingException e) {
            return "ERROR: " + e.getMessage();
        }
        String response = new String(result.getPayload());
        //TODO: deserializacja i analiza
        System.out.println(response);

        if (result.getCode() == 200) {
            //Result smsResult = null;
            try {
                //smsResult = (Result) JsonReader.jsonToJava(response);
                //if (!smsResult.success) {
                //   return "ERROR: " + smsResult.items.get(0).text;
                //}
            } catch (Exception e) {
                Kernel.getInstance().dispatchEvent(Event.logSevere(this.getClass().getSimpleName(), "deserialization problem: incompatible format " + response));
                //TODO: send warning to the service admin about deserialization error
            }
            return "OK";
        } else {
            return "ERROR: " + result.getCode() + " " + result.getPayload();
        }
    }

    @Override
    public String send(String recipient, String nodeName, String message) {
        throw new UnsupportedOperationException("Not available");
    }

    @Override
    public String getChatID(String recipent) {
        throw new UnsupportedOperationException("Not available");
    }

}
