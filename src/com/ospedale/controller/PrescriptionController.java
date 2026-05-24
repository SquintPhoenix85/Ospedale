package com.ospedale.controller;

import com.ospedale.controller.utils.Response;
import com.ospedale.controller.utils.Status;
import com.ospedale.model.Appointment;
import com.ospedale.model.AppointmentStatus;
import com.ospedale.model.Prescription;
import com.ospedale.model.storage.Storage;

public class PrescriptionController {

    public static Response addPrescriptionToAppointment(String appointmentId, String doctorId, String medicationName, String doseStr, String administrationRoute, String durationStr, String additionalInfo, String frequencyStr) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appointment = storage.getAppointment(appointmentId);
            
            if (appointment == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }
            if (appointment.getStatus() != AppointmentStatus.PENDING) {
                return new Response("Appointment is NOT in PENDING state. (Current state: " + appointment.getStatus() + ")", Status.BAD_REQUEST);
            }
            if (!String.valueOf(appointment.getDoctor().getId()).equals(doctorId)) {
                return new Response("Only the assigned doctor can prescribe medications", Status.BAD_REQUEST);
            }

            double dose;
            int duration;
            int frequency;

            try {
                dose = Double.parseDouble(doseStr);
                duration = Integer.parseInt(durationStr);
                frequency = Integer.parseInt(frequencyStr);
            } catch (NumberFormatException e) {
                return new Response("Dose, Duration, and Frequency fields only admit numeric values.", Status.BAD_REQUEST);
            }

            Prescription p = new Prescription(appointment, medicationName, dose, administrationRoute, duration, additionalInfo, frequency);
            appointment.addPrescription(p);

            return new Response("Medication prescribed successfully.", Status.OK, p);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }
}