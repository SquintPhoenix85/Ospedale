/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.ospedale.model.storage.Storage;
import com.ospedale.model.User;
import com.ospedale.model.Patient;
import com.ospedale.model.Doctor;
import com.ospedale.model.Appointment;
import com.ospedale.model.Hospitalization;
import com.ospedale.model.Specialty;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class ViewDataController {

    public List<UserDTO> getAllPatients() {
        Storage storage = Storage.getInstance();
        return storage.getPatients().stream().map(p -> {
            UserDTO dto = new UserDTO();
            dto.id = String.valueOf(p.getId());
            dto.username = p.getUsername();
            dto.firstName = p.getFirstname();
            dto.lastName = p.getLastname();
            dto.role = "PATIENT";
            return dto;
        }).collect(Collectors.toList());
    }

    public List<UserDTO> getAllDoctors() {
        Storage storage = Storage.getInstance();
        return storage.getDoctors().stream().map(d -> {
            UserDTO dto = new UserDTO();
            dto.id = String.valueOf(d.getId());
            dto.username = d.getUsername();
            dto.firstName = d.getFirstname();
            dto.lastName = d.getLastname();
            dto.role = "DOCTOR";
            return dto;
        }).collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForUser(String userId) {
        Storage storage = Storage.getInstance();
        User user = storage.getUserById(Long.parseLong(userId)).orElse(null);
        if (user == null) return List.of();
        
        List<Appointment> appointments = new ArrayList<>();
        if (user instanceof Patient) {
            appointments = ((Patient) user).getAppointments();
        } else if (user instanceof Doctor) {
            appointments = ((Doctor) user).getAppointments();
        }
        
        if (appointments == null) return List.of();
        
        return appointments.stream()
            .sorted((a, b) -> b.getDatetime().compareTo(a.getDatetime()))
            .map(a -> {
                AppointmentDTO dto = new AppointmentDTO();
                dto.id = a.getId();
                dto.date = a.getDatetime() != null ? a.getDatetime().toLocalDate().toString() : "N/A";
                dto.time = a.getDatetime() != null ? a.getDatetime().toLocalTime().toString() : "N/A";
                dto.doctor = a.getDoctor() != null ? a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname() : "N/A";
                dto.patient = a.getPatient() != null ? a.getPatient().getFirstname() + " " + a.getPatient().getLastname() : "N/A";
                dto.specialty = a.getSpecialty() != null ? a.getSpecialty().name() : "N/A";
                dto.status = a.getStatus() != null ? a.getStatus().name() : "N/A";
                return dto;
            }).collect(Collectors.toList());
    }

    public List<HospitalizationDTO> getHospitalizationsForUser(String userId) {
        Storage storage = Storage.getInstance();
        User user = storage.getUserById(Long.parseLong(userId)).orElse(null);
        if (user == null) return List.of();
        
        List<Hospitalization> hospitalizations = new ArrayList<>();
        if (user instanceof Patient) {
            Hospitalization h = ((Patient) user).getHospitalization();
            if (h != null) hospitalizations.add(h);
        }
        
        return hospitalizations.stream().map(h -> {
            HospitalizationDTO dto = new HospitalizationDTO();
            dto.id = h.getId();
            dto.date = h.getDate() != null ? h.getDate().toString() : "N/A";
            dto.status = h.getStatus() != null ? h.getStatus().name() : "N/A";
            dto.reason = h.serialize().get("reason") != null ? h.serialize().get("reason").toString() : "";
            dto.patient = h.getPatient() != null ? h.getPatient().getFirstname() + " " + h.getPatient().getLastname() : "N/A";
            return dto;
        }).collect(Collectors.toList());
    }

    public List<String> getSpecialties() {
        return Arrays.stream(Specialty.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }

    // DTO inner class examples; move to dto package.
    public static class UserDTO {
        public String id, username, firstName, lastName, role;
    }
    public static class AppointmentDTO {
        public String id, date, time, doctor, patient, specialty, status;
    }
    public static class HospitalizationDTO {
        public String id, date, status, reason, patient;
    }
}

