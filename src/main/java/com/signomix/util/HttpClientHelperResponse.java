package com.signomix.util;

/**
 *
 * @author greg
 */
public class HttpClientHelperResponse {
    
    public final static int NOT_INITIALIZED = 0;
    public final static int NOT_CONFIGURED = 1;
    public final static int IO_EXCEPTION = 601;
    public final static int INTERRUPTED_EXCEPTION = 602;
    public final static int ENCODING_EXCEPTION = 603;

    public int code=NOT_INITIALIZED;
    public String text="";
    
    public HttpClientHelperResponse(){
    }

    public HttpClientHelperResponse(int code, String text) {
        this.code = code;
        this.text = text;
    }

}
