package models;

public class Project {
    private String id;
    private String name;
    private String description;
    private String status; // ACTIVE, COMPLETED, ON_HOLD
    private String startDate;
    private String endDate;
    private String managerId;

    public Project(String id, String name, String description, String status,
                   String startDate, String endDate, String managerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.managerId = managerId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getManagerId() { return managerId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String toFileString() {
        return id + "|" + name + "|" + description + "|" + status + "|" + startDate + "|" + endDate + "|" + managerId;
    }

    public static Project fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) return null;
        return new Project(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }

    @Override
    public String toString() {
        return "[" + status + "] " + name;
    }
}
