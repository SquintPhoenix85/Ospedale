/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.ospedale.view.*;
import javax.swing.UIManager;

/**
 *
 * @author orarroyo
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("flatlaf.useNativeLibrary", "false");
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginRegistrationView().setVisible(true);
        });
    }
}
