package packagee;

import java.util.ArrayList;
import javax.swing.JFrame;

public class AuthenticationController {

    private final UserService userService;
    private AuthPayload currentUser;
    private User currentUserModel;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    public Response<AuthPayload> authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Response.badRequest("Username and password are required");
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.notFound("User not found");
        }

        if (!userService.matchesPassword(user, password)) {
            return Response.unauthorized("Invalid credentials");
        }

        AuthPayload payload = AuthPayload.from(user);
        this.currentUser = payload;
        this.currentUserModel = user;
        return Response.ok("Login successful", payload);
    }

    public Response<Void> logout() {
        this.currentUser = null;
        this.currentUserModel = null;
        return Response.ok("Logged out", null);
    }

    public AuthPayload getCurrentUser() {
        return currentUser;
    }

    public Response<JFrame> resolveHomeView(String role, ArrayList<User> users, ArrayList<Hospitalization> hospitalizations, ArrayList<Appointment> appointments) {
        if (currentUser == null || currentUserModel == null) {
            return Response.unauthorized("No authenticated user in session");
        }

        return switch (role) {
            case "ADMIN" -> Response.ok("Navigation ready", new NewJFrame11(currentUserModel, users, hospitalizations, appointments));
            case "DOCTOR" -> {
                if (!(currentUserModel instanceof Doctor doctorUser)) {
                    yield Response.badRequest("Authenticated user role mismatch");
                }
                yield Response.ok("Navigation ready", new NewJFrame111(currentUserModel, doctorUser, users, hospitalizations, appointments));
            }
            case "PATIENT" -> {
                if (!(currentUserModel instanceof Patient patientUser)) {
                    yield Response.badRequest("Authenticated user role mismatch");
                }
                yield Response.ok("Navigation ready", new NewJFrame1(currentUserModel, patientUser, users, appointments, hospitalizations));
            }
            default -> Response.badRequest("Unsupported user role");
        };
    }
}
