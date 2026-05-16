/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import javax.swing.*;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class NotificationController {

    public void notifySuccess(String message, JFrame parent) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void notifyError(String message, JFrame parent) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void notifyInfo(String message, JFrame parent) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.PLAIN_MESSAGE);
    }

    // Optionally overload with custom result/status objects
}
