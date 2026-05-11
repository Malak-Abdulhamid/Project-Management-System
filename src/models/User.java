package models;

public abstract class User {
    private String id;
    private String name;
    private String username;
    private String password;
    private String role;
    private String email;
    private String phone;

    public User(String id, String name, String username, String password, String role, String email, String phone) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    // Abstract method for polymorphism
    public abstract String getDashboardTitle();

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    // Serialize to CSV line
    public String toFileString() {
        return id + "," + name + "," + username + "," + password + "," + role + "," + email + "," + phone;
    }

    @Override
    public String toString() {
        return "[" + role + "] " + name + " (" + username + ")";
    }
}
