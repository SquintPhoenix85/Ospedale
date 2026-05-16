package packagee;

public interface UserService {

    User findByUsername(String username);

    boolean matchesPassword(User user, String password);
}
