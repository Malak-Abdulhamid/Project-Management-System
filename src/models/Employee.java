package models;

public class Employee extends User {

    public Employee(String id, String name, String username, String password, String email, String phone) {
        super(id, name, username, password, "EMPLOYEE", email, phone);
    }

    @Override
    public String getDashboardTitle() {
        return "Employee Dashboard - " + getName();
    }
}
