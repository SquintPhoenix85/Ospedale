package packagee;

public final class AuthPayload {

    private final long id;
    private final String username;
    private final String fullName;
    private final String role;

    private AuthPayload(long id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public static AuthPayload from(User user) {
        String fullName = (user.getFirstname() + " " + user.getLastname()).trim();
        String role = resolveRole(user);
        return new AuthPayload(user.getId(), user.getUsername(), fullName, role);
    }

    private static String resolveRole(User user) {
        if (user instanceof Administrator) {
            return "ADMIN";
        }
        if (user instanceof Doctor) {
            return "DOCTOR";
        }
        return "PATIENT";
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
