package packagee;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

public class InMemoryUserService implements UserService {

    private final List<User> users;

    public InMemoryUserService(List<User> users) {
        this.users = Objects.requireNonNull(users, "users");
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }

        for (User user : users) {
            if (Objects.equals(user.getUsername(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean matchesPassword(User user, String password) {
        if (user == null || user.getPassword() == null || password == null) {
            return false;
        }

        byte[] expected = user.getPassword().getBytes(StandardCharsets.UTF_8);
        byte[] candidate = password.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, candidate);
    }
}
