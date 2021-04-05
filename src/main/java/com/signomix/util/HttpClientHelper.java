package com.signomix.util;

import static com.signomix.out.notification.CommandWebHookClient.ofFormData;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author greg
 */
public class HttpClientHelper {

    private java.net.http.HttpClient httpClient;
    private String agentName;

    public HttpClientHelper() {
        this("HttpClientHelper", 10);
    }

    public HttpClientHelper(String agentName, int timeout) {
        this.agentName = this.getClass().getSimpleName();
        httpClient = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
    }

    public HttpClientHelperResponse getData(String endpoint, HashMap<String, String> headers, HashMap<Object, Object> parameters) {
        HttpClientHelperResponse response = new HttpClientHelperResponse();
        StringBuilder sb=new StringBuilder(endpoint);
        if(parameters.size()>0){
            sb.append("?");
            Iterator it=parameters.keySet().iterator();
            String key;
            int paramNumber=0;
            while(it.hasNext()){
                paramNumber++;
                key=(String)it.next();
                sb.append(key).append("=");
                try {
                    sb.append(URLEncoder.encode((String)parameters.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    response.code=HttpClientHelperResponse.ENCODING_EXCEPTION;
                    response.text=ex.getMessage();
                    return response;
                }
                if(paramNumber<parameters.size()){
                    sb.append("&");
                }
            }
        }
        Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(sb.toString()))
                .setHeader("User-Agent", agentName);
        Iterator it = headers.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            builder.setHeader(key, headers.get(key));
        }
        HttpRequest request = builder.build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            response.code = httpResponse.statusCode();
            response.text = httpResponse.body();
        } catch (IOException ex) {
            response.code = HttpClientHelperResponse.IO_EXCEPTION;
            response.text = ex.getMessage();
        } catch (InterruptedException ex) {
            response.code = HttpClientHelperResponse.INTERRUPTED_EXCEPTION;
            response.text = ex.getMessage();
        }
        return response;
    }

    public HttpClientHelperResponse sendForm(String endpoint, HashMap<String, String> headers, HashMap<Object, Object> parameters) {
        HttpClientHelperResponse response = new HttpClientHelperResponse();
        Builder builder = HttpRequest.newBuilder()
                .POST(ofFormData(parameters))
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("User-Agent", agentName);
        Iterator it = headers.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            builder.setHeader(key, headers.get(key));
        }
        HttpRequest request = builder.build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            response.code = httpResponse.statusCode();
            response.text = httpResponse.body();
        } catch (IOException ex) {
            response.code = HttpClientHelperResponse.IO_EXCEPTION;
            response.text = ex.getMessage();
        } catch (InterruptedException ex) {
            response.code = HttpClientHelperResponse.INTERRUPTED_EXCEPTION;
            response.text = ex.getMessage();
        }
        return response;
    }

    public HttpClientHelperResponse sendJson(String endpoint, HashMap<String, String> headers, String json) {
        HttpClientHelperResponse response = new HttpClientHelperResponse();
        Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .setHeader("User-Agent", agentName);
        Iterator it = headers.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            builder.setHeader(key, headers.get(key));
        }
        HttpRequest request = builder.build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            response.code = httpResponse.statusCode();
            response.text = httpResponse.body();
        } catch (IOException ex) {
            response.code = HttpClientHelperResponse.IO_EXCEPTION;
            response.text = ex.getMessage();
        } catch (InterruptedException ex) {
            response.code = HttpClientHelperResponse.INTERRUPTED_EXCEPTION;
            response.text = ex.getMessage();
        }
        return response;
    }

}
