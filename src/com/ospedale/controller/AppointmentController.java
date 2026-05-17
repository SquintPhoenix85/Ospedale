/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.controller;

/**
 *
 * @author orarroyo
 * @author marianaserrato
 */
import com.ospedale.controller.utils.Response;
import com.ospedale.controller.utils.Status;
import com.ospedale.model.Appointment;
import com.ospedale.model.AppointmentStatus;
import com.ospedale.model.Doctor;
import com.ospedale.model.Patient;
import com.ospedale.model.Prescription;
import com.ospedale.model.Specialty;
import com.ospedale.model.storage.Storage; // TODO: Implement storage with singleton pattern and in-memory data structures (HashMaps)
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AppointmentController {

    // REQUEST APPOINTMENT
    public static Response createAppointment(String patientId, String dateStr, String timeStr, String doctorId, String specialtyStr, String reason, boolean type) {
        try {
            LocalDate date;
            LocalTime time;
            Specialty specialty = null;
            Doctor assignedDoctor = null;
            Storage storage = Storage.getInstance();
            
            // Validate Patient
            if (patientId == null || patientId.trim().isEmpty()) {
                return new Response("patient ID cannot be empty", Status.BAD_REQUEST);
            }
            Patient patient = storage.getPatient(patientId);
            if (patient == null) {
                return new Response("patient not found", Status.NOT_FOUND);
            }

            // Validate Date (Format YYYY-MM-DD)
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateStr.trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                return new Response("Invalid date format. Must be YYYY-MM-DD", Status.BAD_REQUEST);
            }

            // Validate Time (Format HH:mm 24h and minutes 00, 15, 30, 45)
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                time = LocalTime.parse(timeStr.trim(), timeFormatter);
                int minutes = time.getMinute();
                
                if (minutes != 0 && minutes != 15 && minutes != 30 && minutes != 45) {
                    return new Response("Minutes must be only 00, 15, 30 or 45", Status.BAD_REQUEST);
                }
            } catch (DateTimeParseException ex) {
                return new Response("Invalid time format. Must be HH:mm", Status.BAD_REQUEST);
            }

            // Validate Specialty
            if (specialtyStr != null && !specialtyStr.trim().isEmpty()) {
                try {
                    specialty = Specialty.valueOf(specialtyStr.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    return new Response("Invalid specialty", Status.BAD_REQUEST);
                }
            }

            // Validate and Assign Doctor
            if (doctorId != null && !doctorId.trim().isEmpty()) {
                assignedDoctor = storage.getDoctor(doctorId);
                if (assignedDoctor == null) {
                    return new Response("Specified doctor does not exist", Status.NOT_FOUND);
                }
                
                if (specialty != null && assignedDoctor.getSpecialty() != specialty) {
                    return new Response("Doctor's specialty does not match the requested specialty", Status.BAD_REQUEST);
                }
                
                if (!isDoctorAvailable(storage, assignedDoctor, date, time)) {
                    return new Response("Doctor is not available at this time", Status.BAD_REQUEST);
                }
            } else if (specialty != null) {
                assignedDoctor = findAvailableDoctorBySpecialty(storage, specialty, date, time);
                if (assignedDoctor == null) {
                    return new Response("No doctors available for this specialty at this time", Status.NOT_FOUND);
                }
            } else {
                return new Response("You must specify either a doctor or a specialty", Status.BAD_REQUEST);
            }

            // Generate automatic Appointment ID: A-{patient_id}-NNNN
            String appointmentId = generateAppointmentId(storage, patientId);

            // Create Appointment model with initial state REQUESTED
            LocalDateTime datetime = LocalDateTime.of(date, time);
            Appointment appointment = new Appointment(appointmentId, patient, assignedDoctor, specialty, datetime, reason, type);

            // Save in storage
            if (!storage.addAppointment(appointment)) {
                return new Response("Error saving the appointment", Status.BAD_REQUEST);
            }

            return new Response("Appointment requested successfully. ID: " + appointmentId, Status.CREATED, appointment);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // ACCEPT APPOINTMENT
    public static Response acceptAppointment(String appointmentId, String doctorId) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appointment = storage.getAppointment(appointmentId);
            
            if (appointment == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }
            if (String.valueOf(appointment.getDoctor().getId()).equals(doctorId) == false) {
                return new Response("Only the assigned doctor can accept the appointment", Status.BAD_REQUEST);
            }
            if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
                return new Response("Appointment must be in REQUESTED state to be accepted", Status.BAD_REQUEST);
            }

            appointment.setStatus(AppointmentStatus.PENDING);
            return new Response("Appointment accepted. New state: PENDING", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // COMPLETE APPOINTMENT
    public static Response completeAppointment(String appointmentId, String doctorId) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appointment = storage.getAppointment(appointmentId);
            
            if (appointment == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }
            if (String.valueOf(appointment.getDoctor().getId()).equals(doctorId) == false) {
                return new Response("Only the assigned doctor can complete the appointment", Status.BAD_REQUEST);
            }
            if (appointment.getStatus() != AppointmentStatus.PENDING) {
                return new Response("Appointment must be in PENDING state to be completed", Status.BAD_REQUEST);
            }

            appointment.setStatus(AppointmentStatus.COMPLETED);
            return new Response("Appointment completed successfully", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // CANCEL APPOINTMENT
    public static Response cancelAppointment(String appointmentId, String patientId) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appointment = storage.getAppointment(appointmentId);
            
            if (appointment == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }
            if (String.valueOf(appointment.getPatient().getId()).equals(patientId) == false) {
                return new Response("Only the patient owner can cancel the appointment", Status.BAD_REQUEST);
            }
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                return new Response("Cannot cancel an appointment that is already completed", Status.BAD_REQUEST);
            }

            appointment.setStatus(AppointmentStatus.CANCELED);
            return new Response("Appointment canceled successfully", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // RESCHEDULE APPOINTMENT
    public static Response rescheduleAppointment(String appointmentId, String doctorId, String newTimeStr, String reason) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appointment = storage.getAppointment(appointmentId);
            
            if (appointment == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }
            if (String.valueOf(appointment.getDoctor().getId()).equals(doctorId) == false) {
                return new Response("Only the assigned doctor can reschedule the appointment", Status.BAD_REQUEST);
            }

            LocalTime newTime;
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                newTime = LocalTime.parse(newTimeStr.trim(), timeFormatter);
                int minutes = newTime.getMinute();
                
                if (minutes != 0 && minutes != 15 && minutes != 30 && minutes != 45) {
                    return new Response("Minutes must be only 00, 15, 30 or 45", Status.BAD_REQUEST);
                }
            } catch (DateTimeParseException ex) {
                return new Response("Invalid time format. Must be HH:mm", Status.BAD_REQUEST);
            }

            // Do not change the day, only the time
            LocalDate currentDay = appointment.getDatetime().toLocalDate();
            
            // Validate that the doctor is still available in the new time
            if (!isDoctorAvailable(storage, appointment.getDoctor(), currentDay, newTime)) {
                return new Response("Doctor is not available at this new time", Status.BAD_REQUEST);
            }

            appointment.setDatetime(LocalDateTime.of(currentDay, newTime));
            
            // Concatenate reason
            String currentReason = appointment.getReason() == null ? "" : appointment.getReason();
            appointment.setReason(currentReason + "\n[RESCHEDULED]: " + reason);

            return new Response("Appointment rescheduled successfully to the same day, new time: " + newTime, Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // PRESCRIBE MEDICATIONS
    public static Response prescribeMedications(String appointmentId, String doctorId, ArrayList<Prescription> prescriptions) {
        try {
            Storage storage = Storage.getInstance();
            Appointment appointment = storage.getAppointment(appointmentId);
            
            if (appointment == null) {
                return new Response("Appointment not found", Status.NOT_FOUND);
            }
            if (appointment.getStatus() != AppointmentStatus.PENDING) {
                return new Response("Appointment is NOT in PENDING state. (Current state: " + appointment.getStatus() + ")", Status.BAD_REQUEST);
            }
            if (String.valueOf(appointment.getDoctor().getId()).equals(doctorId) == false) {
                return new Response("Only the assigned doctor can prescribe medications", Status.BAD_REQUEST);
            }

            for (Prescription p : prescriptions) {
                appointment.addPrescription(p);
            }

            return new Response(prescriptions.size() + " medication(s) prescribed successfully.", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // GET APPOINTMENTS (VIEWS)

    public static Response getAppointmentsByPatient(String patientId) {
        try {
            Storage storage = Storage.getInstance();
            ArrayList<Appointment> all = storage.getAppointments();
            
            ArrayList<Appointment> patientAppts = all.stream()
                .filter(a -> String.valueOf(a.getPatient().getId()).equals(patientId))
                // Order descending by datetime
                .sorted(Comparator.comparing(Appointment::getDatetime).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
                
            return new Response("Appointments found", Status.OK, patientAppts);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getAppointmentsByDoctor(String doctorId, boolean onlyPending) {
        try {
            Storage storage = Storage.getInstance();
            ArrayList<Appointment> all = storage.getAppointments();
            
            ArrayList<Appointment> doctorAppts = all.stream()
                .filter(a -> String.valueOf(a.getDoctor().getId()).equals(doctorId))
                .filter(a -> !onlyPending || a.getStatus() == AppointmentStatus.PENDING)
                // Order descending by datetime
                .sorted(Comparator.comparing(Appointment::getDatetime).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
                
            return new Response("Appointments found", Status.OK, doctorAppts);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    // AUXILIARY METHODS 

    private static boolean isDoctorAvailable(Storage storage, Doctor doctor, LocalDate date, LocalTime time) {
        ArrayList<Appointment> allAppointments = storage.getAppointments();
        for (Appointment appt : allAppointments) {
            // Evaluate states different from CANCELED to track as "busy"
            if (appt.getStatus() != AppointmentStatus.CANCELED) {
                if (String.valueOf(appt.getDoctor().getId()).equals(String.valueOf(doctor.getId())) &&
                    appt.getDatetime().toLocalDate().equals(date) &&
                    appt.getDatetime().toLocalTime().equals(time)) {
                    return false; // Busy
                }
            }
        }
        return true;
    }

    private static Doctor findAvailableDoctorBySpecialty(Storage storage, Specialty specialty, LocalDate date, LocalTime time) {
        ArrayList<Doctor> allDoctors = storage.getDoctors();
        for (Doctor doctor : allDoctors) {
            if (doctor.getSpecialty() == specialty && isDoctorAvailable(storage, doctor, date, time)) {
                return doctor;
            }
        }
        return null;
    }

    private static String generateAppointmentId(Storage storage, String patientId) {
        ArrayList<Appointment> allAppointments = storage.getAppointments();
        int count = 0;
        
        for (Appointment appt : allAppointments) {
            if (String.valueOf(appt.getPatient().getId()).equals(patientId)) {
                count++;
            }
        }
        
        // Empieza en 0000
        int nextId = count;
        String formattedCount = String.format("%04d", nextId);
        
        return "A-" + patientId + "-" + formattedCount;
    }
}