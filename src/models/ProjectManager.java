package models;

public class ProjectManager extends User {

    public ProjectManager(String id, String name, String username, String password, String email, String phone) {
        super(id, name, username, password, "PROJECT_MANAGER", email, phone);
    }

    @Override
    public String getDashboardTitle() {
        return "Project Manager Dashboard - " + getName();
    }
}
