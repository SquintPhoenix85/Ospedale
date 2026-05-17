/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import com.ospedale.controller.utils.*;
import com.ospedale.model.Administrator;
import com.ospedale.model.Doctor; 
import com.ospedale.model.User;
import java.util.ArrayList;
import java.util.HashMap;
        
/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class AuthenticationController{

    public static Response tryAuthenticate(String userName, String passwd, ArrayList<User> users) {
        for (User user : users) {
            if (userName.equals(user.getUsername())) {
                if (user.getPassword().equals(passwd)) {
                    
                    String role;
                    if (user instanceof Administrator) role = "ADMIN";
                    else if (user instanceof Doctor) role = "DOCTOR";
                    else role = "PATIENT";

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("user", user);
                    data.put("role", role);

                    return new Response("Authentication successful.", Status.OK, data);
                } else {
                    return new Response("Incorrect password.", Status.BAD_REQUEST);
                }
            }
        }
        return new Response("User not found.", Status.NOT_FOUND);
    }
    
    public Response logout() {
        return new Response("Logout successful.", Status.OK);
    }
}
