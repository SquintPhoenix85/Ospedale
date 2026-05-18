/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.model;

import java.time.LocalDate;
import java.util.HashMap;

/**
 *
 * @author edangulo
 */
public class Hospitalization implements Serializable {
    
    private final String id;
    private Patient patient;
    private Doctor doctor;
    private LocalDate date;

    public String getId() {
        return id;
    }
    private String reason;
    private RoomType roomType;
    private String observations;
    private HospitalizationStatus status;

    public void setStatus(HospitalizationStatus status) {
        this.status = status;
    }

    public Hospitalization(String id, Patient patient, Doctor doctor, LocalDate date, String reason, RoomType roomType, String observations) {
        this.id = id;
        this.patient = patient;
        patient.setHospitalization(this);
        this.doctor = doctor;
        doctor.addHospitalization(this);
        this.date = date;
        this.reason = reason;
        this.roomType = roomType;
        this.observations = observations;
        this.status = HospitalizationStatus.REQUESTED;
    }
    public Hospitalization(String id, Patient patient, Doctor doctor, LocalDate date, String reason, RoomType roomType, String observations, HospitalizationStatus hopsS) {
        this.id = id;
        this.patient = patient;
        patient.setHospitalization(this);
        this.doctor = doctor;
        doctor.addHospitalization(this);
        this.date = date;
        this.reason = reason;
        this.roomType = roomType;
        this.observations = observations;
        this.status = hopsS;
    }

    public Patient getPatient() { 
        return patient; 
    }
    
    public Doctor getDoctor() { 
        return doctor; 
    }
    
    public LocalDate getDate() { 
        return date; 
    }
    
    public HospitalizationStatus getStatus() { 
        return status; 
    }
    
    public void setDoctor(Doctor doctor) { 
        this.doctor = doctor; 
    }
    
    @Override
    public HashMap<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        
        map.put("id", this.id);
        map.put("date", this.date != null ? this.date.toString() : null);
        map.put("reason", this.reason);
        map.put("roomtype", this.roomType != null ? this.roomType.name() : null); // Enum to String
        map.put("status", this.status != null ? this.status.name() : null); // Enum to String
        map.put("observations", this.observations);
        
        // Flatten constraints
        map.put("patientid", this.patient != null ? this.patient.getId() : null);
        map.put("doctorid", this.doctor != null ? this.doctor.getId() : null);
        
        return map;
    }
}
