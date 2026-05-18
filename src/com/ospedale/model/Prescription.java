/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.model;

import java.util.HashMap;

/**
 *
 * @author jjlora
 */
public class Prescription implements Serializable{
    private Appointment appointment;
    private String medicationName;
    private double dose;
    private String administrationRoute;
    private int treatmentDuration;
    private String additionalInstructions;
    private int frecuency;

    public Prescription(Appointment appointment, String medicationName, double dose, String administrationRoute, int treatmentDuration, String additionalInstructions, int frecuency) {
        this.appointment = appointment;
        appointment.addPrescription(this);
        this.medicationName = medicationName;
        this.dose = dose;
        this.administrationRoute = administrationRoute;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstructions = additionalInstructions;
        this.frecuency = frecuency;
    }
    
    @Override
    public HashMap<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        
        map.put("appointmentId", this.appointment != null ? this.appointment.getId() : null);
        map.put("medicationName", this.medicationName);
        map.put("dose", this.dose);
        map.put("administrationRoute", this.administrationRoute);
        map.put("treatmentDuration", this.treatmentDuration);
        map.put("additionalInstructions", this.additionalInstructions);
        map.put("frecuency", this.frecuency);
        
        return map;
    }
    
}
