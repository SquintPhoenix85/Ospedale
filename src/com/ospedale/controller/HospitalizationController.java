package com.ospedale.controller;

import com.ospedale.controller.utils.Response;
import com.ospedale.controller.utils.Status;
import com.ospedale.model.*;
import com.ospedale.model.storage.Storage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
public class HospitalizationController {

    // REQUEST HOSPITALIZATION
    public static Response createHospitalization(String patientId, String dateStr, String reason, String roomTypeStr) {
        try {
            Storage storage = Storage.getInstance();

            // Validate Patient
            if (patientId == null || patientId.trim().isEmpty()) {
                return new Response("Patient ID cannot be empty", Status.BAD_REQUEST);
            }
            Patient patient = storage.getPatient(patientId);
            if (patient == null) {
                return new Response("Patient not found", Status.NOT_FOUND);
            }

            // Validate Date (YYYY-MM-DD)
            LocalDate date;
            try {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateStr.trim(), df);
            } catch (DateTimeParseException ex) {
                return new Response("Invalid date format. Must be YYYY-MM-DD", Status.BAD_REQUEST);
            }

            // Validate RoomType
            RoomType roomType;
            try {
                roomType = RoomType.valueOf(roomTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return new Response("Invalid room type", Status.BAD_REQUEST);
            }

            // Generate Hospitalization ID: H-{patient_id}-NNNN
            String hospitalizationId = generateHospitalizationId(storage, patientId);

            // Create Hospitalization (starts as REQUESTED)
            Hospitalization hosp = new Hospitalization(hospitalizationId, patient, null, date, reason, roomType, "");
            
            if (!storage.addHospitalization(hosp)) {
                return new Response("Error saving the hospitalization", Status.BAD_REQUEST);
            }

            return new Response("Hospitalization requested successfully. ID: " + hospitalizationId, Status.CREATED, hosp);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // APPROVE HOSPITALIZATION (Doctor)
    public static Response approveHospitalization(String hospitalizationId, String doctorId) {
        try {
            Storage storage = Storage.getInstance();
            Hospitalization hosp = storage.getHospitalization(hospitalizationId);

            if (hosp == null) {
                return new Response("Hospitalization not found", Status.NOT_FOUND);
            }

            Doctor doctor = storage.getDoctor(doctorId);
            if (doctor == null) {
                return new Response("Doctor not found", Status.NOT_FOUND);
            }

            if (hosp.getStatus() != HospitalizationStatus.REQUESTED) {
                return new Response("Hospitalization must be in REQUESTED state to be approved", Status.BAD_REQUEST);
            }

            hosp.setDoctor(doctor);
            hosp.setStatus(HospitalizationStatus.ONGOING);
            doctor.addHospitalization(hosp);

            return new Response("Hospitalization approved. Status: ONGOING", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // DENY HOSPITALIZATION (Doctor)
    public static Response denyHospitalization(String hospitalizationId, String doctorId) {
        try {
            Storage storage = Storage.getInstance();
            Hospitalization hosp = storage.getHospitalization(hospitalizationId);

            if (hosp == null) {
                return new Response("Hospitalization not found", Status.NOT_FOUND);
            }

            Doctor doctor = storage.getDoctor(doctorId);
            if (doctor == null) {
                return new Response("Doctor not found", Status.NOT_FOUND);
            }

            if (hosp.getStatus() != HospitalizationStatus.REQUESTED) {
                return new Response("Only REQUESTED hospitalizations can be denied", Status.BAD_REQUEST);
            }

            hosp.setStatus(HospitalizationStatus.CANCELED);
            return new Response("Hospitalization denied. Status: CANCELED", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // SEND TO HOSPITALIZATION FROM APPOINTMENT
    // Doctor completes appointment and sends patient to hospitalization directly
    public static Response sendToHospitalizationFromAppointment(String appointmentId, String doctorId, String dateStr, String reason, String roomTypeStr) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appt = storage.getAppointment(appointmentId);

            if (appt == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }

            if (!String.valueOf(appt.getDoctor().getId()).equals(doctorId)) {
                return new Response("Only the assigned doctor can perform this action", Status.BAD_REQUEST);
            }

            if (appt.getStatus() == AppointmentStatus.COMPLETED || appt.getStatus() == AppointmentStatus.CANCELED) {
                return new Response("Cannot send completed or canceled appointment to hospitalization", Status.BAD_REQUEST);
            }

            // Validate Date
            LocalDate date;
            try {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateStr.trim(), df);
            } catch (DateTimeParseException ex) {
                return new Response("Invalid date format. Must be YYYY-MM-DD", Status.BAD_REQUEST);
            }

            // Validate RoomType
            RoomType roomType;
            try {
                roomType = RoomType.valueOf(roomTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return new Response("Invalid room type", Status.BAD_REQUEST);
            }

            // Complete the appointment
            appt.setStatus(AppointmentStatus.COMPLETED);

            // Create hospitalization directly as ONGOING (no REQUESTED state)
            String hospId = generateHospitalizationId(storage, String.valueOf(appt.getPatient().getId()));
            Hospitalization hosp = new Hospitalization(hospId, appt.getPatient(), appt.getDoctor(), date, reason, roomType, "", HospitalizationStatus.ONGOING);
            
            if (!storage.addHospitalization(hosp)) {
                return new Response("Error creating hospitalization", Status.BAD_REQUEST);
            }

            return new Response("Appointment completed and patient sent to hospitalization. Hosp ID: " + hospId, Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // GET HOSPITALIZATIONS BY PATIENT
    public static Response getHospitalizationsByPatient(String patientId) {
        try {
            Storage storage = Storage.getInstance();
            ArrayList<Hospitalization> hosps = new ArrayList<>(storage.getHospitalizationsByPatient(patientId));
            return new Response("Hospitalizations retrieved", Status.OK, hosps);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // HELPER: Generate Hospitalization ID
    private static String generateHospitalizationId(Storage storage, String patientId) {
        ArrayList<Hospitalization> allHosps = new ArrayList<>(storage.getHospitalizations());
        int count = 0;

        for (Hospitalization h : allHosps) {
            if (h.getPatient() != null && String.valueOf(h.getPatient().getId()).equals(patientId)) {
                count++;
            }
        }

        String formattedCount = String.format("%04d", count);
        return "H-" + patientId + "-" + formattedCount;
    }
}