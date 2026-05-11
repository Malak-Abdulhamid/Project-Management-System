package models;

public class TeamLeader extends User {

    public TeamLeader(String id, String name, String username, String password, String email, String phone) {
        super(id, name, username, password, "TEAM_LEADER", email, phone);
    }

    @Override
    public String getDashboardTitle() {
        return "Team Leader Dashboard - " + getName();
    }
}
