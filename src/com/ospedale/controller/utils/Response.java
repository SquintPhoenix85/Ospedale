package com.ospedale.controller.utils;

import java.util.HashMap;

public class Response {
    
    private String message;
    private int status;
    private HashMap<String, Object> data;

    public Response(String message, int status) {
        this.message = message;
        this.status = status;
    }
    
    public Response(String message, int status, HashMap<String, Object> data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
    
}