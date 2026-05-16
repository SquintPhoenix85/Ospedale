/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import java.util.List;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class ViewDataController {

    public List<UserDTO> getAllPatients() {
        // TODO: Fetch and map patient model objects to UserDTO
        return List.of(); // Java 9+, replace with actual logic
    }

    public List<UserDTO> getAllDoctors() {
        // TODO: Fetch and map doctor model objects to UserDTO
        return List.of();
    }

    public List<AppointmentDTO> getAppointmentsForUser(String userId) {
        // TODO: Fetch and serialize appointments for a user
        return List.of();
    }

    public List<HospitalizationDTO> getHospitalizationsForUser(String userId) {
        // TODO: Fetch and serialize hospitalizations for a user
        return List.of();
    }

    public List<String> getSpecialties() {
        // TODO: Fetch available specialties for ComboBox, etc.
        return List.of();
    }

    // DTO inner class examples; move to dto package.
    public static class UserDTO {
        public String id, username, firstName, lastName, role;
        // Add other fields needed for the view
    }
    public static class AppointmentDTO {
        public String id, date, time, doctor, specialty, status;
        // Add other fields as needed
    }
    public static class HospitalizationDTO {
        public String id, date, status, reason;
        // Add other fields as needed
    }
}

