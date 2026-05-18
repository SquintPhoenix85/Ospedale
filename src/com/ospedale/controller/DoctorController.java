/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import com.ospedale.controller.utils.Response;
import com.ospedale.controller.utils.Status;
import com.ospedale.model.storage.Storage;
import com.ospedale.model.Doctor;
import com.ospedale.model.Specialty;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class DoctorController {
     public static Response registerDoctor(String creatorId, String idStr, String username, String firstname, String lastname, String password, String confirmPassword, String specialtyStr, String licenceNumber, String office) {
        try {
            Storage storage = Storage.getInstance();
            
            // 0. Validar permisos (Solo Administrador)
            // if (!storage.isAdmin(creatorId)) {
            //    return new Response("Only the administrator can register doctors", Status.BAD_REQUEST);
            // }

            // 1. Validar ID: Numérico, > 0, 12 dígitos
            long id;
            try {
                id = Long.parseLong(idStr.trim());
                if (id <= 0 || idStr.trim().length() != 12) {
                    return new Response("Doctor ID must be greater than 0 and have exactly 12 digits", Status.BAD_REQUEST);
                }
                if (storage.getDoctor(String.valueOf(id)) != null || PatientController.isIdInUse(storage, id)) {
                    return new Response("ID is already in use", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Doctor ID must be numeric", Status.BAD_REQUEST);
            }

            // 2. Validar Username (único)
            if (PatientController.isUsernameInUse(storage, username, -1)) {
                return new Response("Username is already taken", Status.BAD_REQUEST);
            }

            // 3. Validar Contraseñas
            if (!password.equals(confirmPassword)) {
                return new Response("Passwords do not match", Status.BAD_REQUEST);
            }

            // 4. Validar Licencia: L-XXXXXXXXXX MTL
            if (!licenceNumber.matches("^L-\\d{10} MTL$")) {
                return new Response("Invalid Licence format. Must be L-XXXXXXXXXX MTL", Status.BAD_REQUEST);
            }

            // 5. Validar Oficina: O-XXX
            if (!office.matches("^O-\\d{3}$")) {
                return new Response("Invalid Office format. Must be O-XXX", Status.BAD_REQUEST);
            }

            // 6. Especialidad
            Specialty specialty;
            try {
                specialty = Specialty.valueOf(specialtyStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return new Response("Invalid Specialty", Status.BAD_REQUEST);
            }

            // Crear y Guardar
            Doctor doctor = new Doctor(id, username, firstname, lastname, password, specialty, licenceNumber, office);
            
            if (storage.addDoctor(doctor)) {
                return new Response("Doctor registered successfully", Status.CREATED, doctor.serialize());
            } else {
                return new Response("Could not register doctor", Status.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response updateDoctor(String requesterId, String idStr, String newUsername, String newFirstname, String newLastname, String newPassword, String confirmNewPassword, String newSpecialtyStr, String newLicence, String newOffice) {
        try {
            Storage storage = Storage.getInstance();
            
            long id;
            try {
                id = Long.parseLong(idStr.trim());
            } catch (Exception e) {
                return new Response("Invalid ID", Status.BAD_REQUEST);
            }

            Doctor doctor = storage.getDoctor(String.valueOf(id));
            if (doctor == null) {
                return new Response("Doctor not found", Status.NOT_FOUND);
            }

            // Validar Username
            if (PatientController.isUsernameInUse(storage, newUsername, id)) {
                return new Response("Username is already taken", Status.BAD_REQUEST);
            }

            // Validar Passwords
            if (!newPassword.equals(confirmNewPassword)) {
                return new Response("Passwords do not match", Status.BAD_REQUEST);
            }

            // Validar Licencia
            if (!newLicence.matches("^L-\\d{10} MTL$")) {
                return new Response("Invalid Licence format. Must be L-XXXXXXXXXX MTL", Status.BAD_REQUEST);
            }

            // Validar Oficina
            if (!newOffice.matches("^O-\\d{3}$")) {
                return new Response("Invalid Office format. Must be O-XXX", Status.BAD_REQUEST);
            }

            // Validar Especialidad
            Specialty specialty;
            try {
                specialty = Specialty.valueOf(newSpecialtyStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return new Response("Invalid Specialty", Status.BAD_REQUEST);
            }

            // Actualizar Modelo
            doctor.setUsername(newUsername);
            doctor.setFirstname(newFirstname);
            doctor.setLastname(newLastname);
            doctor.setPassword(newPassword);
            doctor.setSpecialty(specialty);
            doctor.setLicenceNumber(newLicence);
            doctor.setAssignedOffice(newOffice);

            return new Response("Doctor info updated successfully", Status.OK, doctor.serialize());

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
