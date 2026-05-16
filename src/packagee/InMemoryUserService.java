package packagee;

import java.util.List;
import java.util.Objects;

public class InMemoryUserService implements UserService {

    private final List<User> users;

    public InMemoryUserService(List<User> users) {
        this.users = Objects.requireNonNull(users, "users");
    }

    @Override
    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean matchesPassword(User user, String password) {
        return user != null && user.getPassword().equals(password);
    }
}
