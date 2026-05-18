/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author edangulo
 * @author orarroyo
 */
public class Response {
    
    private String message;
    private int status;
    private HashMap<String, Object> data;
    private ArrayList<HashMap<String, Object>> dataList;

    public Response(String message, int status) {
        this.message = message;
        this.status = status;
    }
    
    public Response(String message, int status, HashMap<String, Object> data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
    
    public Response(String message, int status, ArrayList<HashMap<String, Object>> dataList) {
        this.message = message;
        this.status = status;
        this.dataList = dataList;
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

    public ArrayList<HashMap<String, Object>> getDataList() {
        return dataList;
    }
    
}

