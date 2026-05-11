package models;

public class Admin extends User {

    public Admin(String id, String name, String username, String password, String email, String phone) {
        super(id, name, username, password, "ADMIN", email, phone);
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard - " + getName();
    }
}
