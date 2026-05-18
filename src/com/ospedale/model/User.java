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
public abstract class User implements Serializable {
    
    protected final long id;
    protected String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    protected String firstname;
    protected String lastname;
    protected String password;

    public User(long id, String username, String firstname, String lastname, String password) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }
    
    @Override
    public HashMap<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        
        map.put("id", this.id);
        map.put("username", this.username);
        map.put("firstname", this.firstname);
        map.put("lastname", this.lastname);
        map.put("password", this.password);
        
        return map;
    }
    
}
