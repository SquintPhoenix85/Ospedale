/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.model;

import java.util.HashMap;

/**
 *
 * @author edangulo
 */
public class Administrator extends User {
    
    public Administrator(long id, String username, String firstname, String lastname, String password) {
        super(id, username, firstname, lastname, password);
    }
    
    @Override
    public HashMap<String, Object> serialize() {
        HashMap<String, Object> map = super.serialize();
        return map;
    }
    
}
