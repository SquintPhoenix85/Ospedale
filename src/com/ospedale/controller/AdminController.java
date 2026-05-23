/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import com.ospedale.model.Administrator;
import com.ospedale.model.Doctor;
import com.ospedale.model.Patient;
import com.ospedale.model.User;
import com.ospedale.model.storage.Storage;
import com.ospedale.view.AdminDashboardView;
import com.ospedale.view.DoctorDashboardView;
import com.ospedale.view.PatientDashboardView;
import java.util.ArrayList;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class AdminController {

    public static void launchDoctorDashboard(javax.swing.JFrame currentView, User adminUser, String targetDoctorId) {
        if (!(adminUser instanceof Administrator)) return;
        
        Storage storage = Storage.getInstance();
        Doctor doctor = storage.getDoctor(targetDoctorId);
        
        if (doctor != null) {
            currentView.setVisible(false);
            new DoctorDashboardView(adminUser, doctor, 
                                    new ArrayList<>(storage.getAllUsers()), 
                                    new ArrayList<>(), // Ideally filter this via DataController
                                    new ArrayList<>(storage.getAppointments())).setVisible(true);
        }
    }

    public static void launchPatientDashboard(javax.swing.JFrame currentView, User adminUser, String targetPatientId) {
        if (!(adminUser instanceof Administrator)) return;
        
        Storage storage = Storage.getInstance();
        Patient patient = storage.getPatient(targetPatientId);
        
        if (patient != null) {
            currentView.setVisible(false);
            new PatientDashboardView(adminUser, patient, 
                                     new ArrayList<>(storage.getAllUsers()), 
                                     new ArrayList<>(storage.getAppointments()), 
                                     new ArrayList<>()).setVisible(true);
        }
    }

    public static void navigateBackToAdmin(javax.swing.JFrame currentView, User adminUser) {
        if (adminUser instanceof Administrator) {
            currentView.setVisible(false);
            new AdminDashboardView(adminUser).setVisible(true);
        }
    }
}
