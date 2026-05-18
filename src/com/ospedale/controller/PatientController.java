/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import com.ospedale.controller.utils.Status;
import com.ospedale.model.User;
import com.ospedale.controller.utils.Response;
import com.ospedale.model.Doctor;
import com.ospedale.model.Patient;
import com.ospedale.model.storage.Storage;
/**
 *
 * @author orarroyo
 * @author marianaserrato
 */

public class PatientController {
    public static Response registerPatient(String idStr, String username, String firstname, String lastname, String password, String confirmPassword, String email, String birthdateStr, boolean gender, String phoneStr, String address) {
        try {
            Storage storage = Storage.getInstance();
            
            // 1. Validar ID: Numérico, mayor que 0, de 12 dígitos
            long id;
            try {
                id = Long.parseLong(idStr.trim());
                if (id <= 0 || idStr.trim().length() != 12) {
                    return new Response("Patient ID must be greater than 0 and have exactly 12 digits", Status.BAD_REQUEST);
                }
                if (storage.getPatient(String.valueOf(id)) != null || isIdInUse(storage, id)) {
                    return new Response("ID is already strictly in use", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Patient ID must be numeric", Status.BAD_REQUEST);
            }

            // 2. Validar Username (único en pacientes y doctores y admins)
            if (isUsernameInUse(storage, username, -1)) {
                return new Response("Username is already taken", Status.BAD_REQUEST);
            }

            // 3. Validar Passwords coinciden
            if (!password.equals(confirmPassword)) {
                return new Response("Passwords do not match", Status.BAD_REQUEST);
            }

            // 4. Validar Teléfono: exactamente 10 dígitos numéricos
            long phone;
            try {
                phone = Long.parseLong(phoneStr.trim());
                if (phoneStr.trim().length() != 10) {
                    return new Response("Phone number must have exactly 10 digits", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException e) {
                return new Response("Phone must be numeric", Status.BAD_REQUEST);
            }

            // 5. Validar Email: formato XXXXX@XXXXX.com
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$")) {
                return new Response("Invalid email format. Must end with .com", Status.BAD_REQUEST);
            }

            // 6. Validar Fecha Nacimiento: AAAA-MM-DD
            LocalDate birthdate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                birthdate = LocalDate.parse(birthdateStr.trim(), formatter);
            } catch (DateTimeParseException ex) {
                return new Response("Invalid birthdate format. Must be YYYY-MM-DD", Status.BAD_REQUEST);
            }

            // Crear y Guardar
            Patient patient = new Patient(id, username, firstname, lastname, password, email, birthdate, gender, phone, address);
            
            if (storage.addPatient(patient)) {
                return new Response("Patient registered successfully", Status.CREATED, patient.serialize());
            } else {
                return new Response("Could not register patient", Status.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response updatePatient(String idStr, String newUsername, String newFirstname, String newLastname, String newPassword, String confirmNewPassword, String newEmail, String newBirthdateStr, boolean newGender, String newPhoneStr, String newAddress) {
        try {
            Storage storage = Storage.getInstance();
            
            long id;
            try {
                id = Long.parseLong(idStr.trim());
            } catch (Exception e) {
                return new Response("Invalid ID", Status.BAD_REQUEST);
            }

            Patient patient = storage.getPatient(String.valueOf(id));
            if (patient == null) {
                return new Response("Patient not found", Status.NOT_FOUND);
            }

            // Validar Username (ignorando el del propio paciente)
            if (isUsernameInUse(storage, newUsername, id)) {
                return new Response("Username is already taken", Status.BAD_REQUEST);
            }

            // Validar Passwords
            if (!newPassword.equals(confirmNewPassword)) {
                return new Response("Passwords do not match", Status.BAD_REQUEST);
            }

            // Validar Teléfono
            long phone;
            try {
                phone = Long.parseLong(newPhoneStr.trim());
                if (newPhoneStr.trim().length() != 10) {
                    return new Response("Phone number must have exactly 10 digits", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException e) {
                return new Response("Phone must be numeric", Status.BAD_REQUEST);
            }

            // Validar Email
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$")) {
                return new Response("Invalid email format. Must end with .com", Status.BAD_REQUEST);
            }

            // Validar Fecha Nacimiento
            LocalDate birthdate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                birthdate = LocalDate.parse(newBirthdateStr.trim(), formatter);
            } catch (DateTimeParseException ex) {
                return new Response("Invalid birthdate format. Must be YYYY-MM-DD", Status.BAD_REQUEST);
            }

            // Actualizar Modelo
            patient.setUsername(newUsername);
            patient.setFirstname(newFirstname);
            patient.setLastname(newLastname);
            patient.setPassword(newPassword);
            patient.setEmail(newEmail);
            patient.setBirthdate(birthdate);
            patient.setGender(newGender);
            patient.setPhone(phone);
            patient.setAddress(newAddress);

            return new Response("Patient info updated successfully", Status.OK, patient.serialize());

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // -- Métodos auxiliares universales --
    protected static boolean isUsernameInUse(Storage storage, String username, long excludeId) {
        ArrayList<Patient> patients = storage.getPatients() != null ? storage.getPatients() : new ArrayList<>();
        ArrayList<Doctor> doctors = storage.getDoctors(); 
        
        for (Patient p : patients) {
            if (p.getUsername().equals(username) && p.getId() != excludeId) return true;
        }
        for (Doctor d : doctors) {
            if (d.getUsername().equals(username) && d.getId() != excludeId) return true;
        }
        
        // Comprobar también el Admin para evitar conflictos a nivel global
        ArrayList<User> allUsers = storage.getAllUsers();
        for (User u : allUsers) {
             if (u.getUsername().equals(username) && u.getId() != excludeId) return true;
        }
        return false;
    }

    protected static boolean isIdInUse(Storage storage, long id) {
        return storage.getUserById(id).isPresent();
    }
}
