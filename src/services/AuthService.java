package services;

import models.*;
import java.util.List;

/**
 * Handles user authentication and session management.
 */
public class AuthService {

    private static User currentUser = null;

    /**
     * Attempt to login with username and password.
     * Returns the matched User object or null.
     */
    public static User login(String username, String password) {
        UserService userService = new UserService();
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
