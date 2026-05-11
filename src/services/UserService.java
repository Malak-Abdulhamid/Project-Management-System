package services;

import models.*;
import java.util.*;

/**
 * CRUD operations for users (Admin module).
 */
public class UserService {

    /** Parse a CSV line into a User subclass based on role field. */
    public static User parseUser(String line) {
        // Format: id,name,username,password,role,email,phone
        String[] parts = line.split(",", -1);
        if (parts.length < 7) return null;
        String id = parts[0].trim();
        String name = parts[1].trim();
        String username = parts[2].trim();
        String password = parts[3].trim();
        String role = parts[4].trim();
        String email = parts[5].trim();
        String phone = parts[6].trim();

        switch (role) {
            case "ADMIN":         return new Admin(id, name, username, password, email, phone);
            case "PROJECT_MANAGER": return new ProjectManager(id, name, username, password, email, phone);
            case "TEAM_LEADER":   return new TeamLeader(id, name, username, password, email, phone);
            case "EMPLOYEE":      return new Employee(id, name, username, password, email, phone);
            default:              return null;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileStorageService.readLines(FileStorageService.USERS_FILE);
        for (String line : lines) {
            User u = parseUser(line);
            if (u != null) users.add(u);
        }
        return users;
    }

    public User getUserById(String id) {
        for (User u : getAllUsers()) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User u : getAllUsers()) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    /** Get all users with a specific role */
    public List<User> getUsersByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User u : getAllUsers()) {
            if (u.getRole().equals(role)) result.add(u);
        }
        return result;
    }

    /** Add a new user; returns false if username already exists */
    public boolean addUser(User user) {
        if (getUserByUsername(user.getUsername()) != null) return false;
        FileStorageService.appendLine(FileStorageService.USERS_FILE, user.toFileString());
        return true;
    }

    /** Update an existing user by ID */
    public boolean updateUser(User updatedUser) {
        List<String> lines = FileStorageService.readLines(FileStorageService.USERS_FILE);
        boolean found = false;
        List<String> newLines = new ArrayList<>();
        for (String line : lines) {
            User u = parseUser(line);
            if (u != null && u.getId().equals(updatedUser.getId())) {
                newLines.add(updatedUser.toFileString());
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.USERS_FILE, newLines);
        return found;
    }

    /** Delete a user by ID */
    public boolean deleteUser(String userId) {
        List<String> lines = FileStorageService.readLines(FileStorageService.USERS_FILE);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            User u = parseUser(line);
            if (u != null && u.getId().equals(userId)) {
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.USERS_FILE, newLines);
        return found;
    }

    /** Check if username is available */
    public boolean isUsernameAvailable(String username) {
        return getUserByUsername(username) == null;
    }
}
