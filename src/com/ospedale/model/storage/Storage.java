/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ospedale.model.storage;

import com.ospedale.model.*;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author orarroyo
 */
public class Storage {
    private static Storage instance;

    private final Map<Long, User> usersById;
    private final Map<String, Appointment> appointmentsById;

    private Storage() {
        usersById = new HashMap<>();
        appointmentsById = new HashMap<>();
        loadUsersFromJson();
    }

    public static synchronized Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    private void loadUsersFromJson() {
        try {
            File f = new File("json/users.json");
            if (!f.exists()) return;
            JSONTokener tok = new JSONTokener(new FileInputStream(f));
            JSONObject root = new JSONObject(tok);
            JSONArray arr = root.getJSONArray("users");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject jo = arr.getJSONObject(i);
                String type = jo.getString("type");
                long id = jo.getLong("id");
                String username = jo.getString("username");
                String firstname = jo.optString("firstname", "");
                String lastname = jo.optString("lastname", "");
                String password = jo.optString("password", "");

                switch (type.toLowerCase()) {
                    case "admin": {
                        Administrator admin = new Administrator(id, username, firstname, lastname, password);
                        usersById.put(id, admin);
                        break;
                    }
                    case "patient": {
                        String email = jo.optString("email", "");
                        String birthdate = jo.optString("birthdate", null);
                        boolean gender = jo.optBoolean("gender", true);
                        long phone = jo.optLong("phone", 0L);
                        String address = jo.optString("address", "");
                        LocalDate bd = (birthdate == null || birthdate.isEmpty()) ? null : LocalDate.parse(birthdate, df);
                        Patient p = new Patient(id, username, firstname, lastname, password, email, bd, gender, phone, address);
                        usersById.put(id, p);
                        break;
                    }
                    case "doctor": {
                        String specialty = jo.optString("specialty", "GENERAL");
                        String licence = jo.optString("licenceNumber", "");
                        String office = jo.optString("assignedOffice", "");
                        Specialty spec = Specialty.valueOf(specialty.toUpperCase());
                        Doctor d = new Doctor(id, username, firstname, lastname, password, spec, licence, office);
                        usersById.put(id, d);
                        break;
                    }
                    default:

                }
            }
        } catch (Exception ex) {
            System.err.println("Storage: error loading users.json -> " + ex.getMessage());
        }
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Patient getPatient(String idStr) {
        try {
            long id = Long.parseLong(idStr);
            User u = usersById.get(id);
            return (u instanceof Patient) ? (Patient) u : null;
        } catch (Exception e) {
            return null;
        }
    }

    public Doctor getDoctor(String idStr) {
        try {
            long id = Long.parseLong(idStr);
            User u = usersById.get(id);
            return (u instanceof Doctor) ? (Doctor) u : null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Doctor> getDoctors() {
        ArrayList<Doctor> out = new ArrayList<>();
        for (User u : usersById.values()) if (u instanceof Doctor) out.add((Doctor) u);
        return out;
    }

    public List<Patient> getPatients() {
        ArrayList<Patient> out = new ArrayList<>();
        for (User u : usersById.values()) if (u instanceof Patient) out.add((Patient) u);
        return out;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    public Optional<User> findUserByUsername(String username) {
        return usersById.values().stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public boolean addUser(User u) {
        if (u == null) return false;
        if (usersById.containsKey(u.getId())) return false;
        boolean usernameTaken = usersById.values().stream().anyMatch(x -> x.getUsername().equals(u.getUsername()));
        if (usernameTaken) return false;
        usersById.put(u.getId(), u);
        return true;
    }

    public boolean addAppointment(Appointment a) {
        if (a == null) return false;
        if (appointmentsById.containsKey(a.getId())) return false;
        appointmentsById.put(a.getId(), a);

        if (a.getPatient() != null) {
            try {
                a.getPatient().addAppointment(a);
            } catch (Exception ignored) {}
        }
        if (a.getDoctor() != null) {
            try {
                a.getDoctor().getAppointments().add(a);
            } catch (Exception ignored) {}
        }
        return true;
    }

    public Appointment getAppointment(String id) {
        return appointmentsById.get(id);
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointmentsById.values());
    }
}

