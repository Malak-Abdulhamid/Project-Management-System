package models;

public class Task {
    private String id;
    private String title;
    private String description;
    private String assignedEmployeeId;
    private String projectId;
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private String dueDate;
    private String assignedBy; // TL username
    private int progress; // 0 to 100

    public Task(String id, String title, String description, String assignedEmployeeId,
                String projectId, String status, String dueDate, String assignedBy) {
        this(id, title, description, assignedEmployeeId, projectId, status, dueDate, assignedBy, 0);
    }

    public Task(String id, String title, String description, String assignedEmployeeId,
                String projectId, String status, String dueDate, String assignedBy, int progress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedEmployeeId = assignedEmployeeId;
        this.projectId = projectId;
        this.status = status;
        this.dueDate = dueDate;
        this.assignedBy = assignedBy;
        this.progress = progress;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignedEmployeeId() { return assignedEmployeeId; }
    public String getProjectId() { return projectId; }
    public String getStatus() { return status; }
    public String getDueDate() { return dueDate; }
    public String getAssignedBy() { return assignedBy; }
    public int getProgress() { return progress; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAssignedEmployeeId(String id) { this.assignedEmployeeId = id; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public void setStatus(String status) { this.status = status; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public void setProgress(int progress) { this.progress = progress; }

    public String toFileString() {
        return id + "|" + title + "|" + description + "|" + assignedEmployeeId + "|" +
               projectId + "|" + status + "|" + dueDate + "|" + assignedBy + "|" + progress;
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) return null;
        int progress = 0;
        if (parts.length >= 9) {
            try { progress = Integer.parseInt(parts[8]); } catch (Exception e) {}
        }
        return new Task(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], progress);
    }

    @Override
    public String toString() {
        return "[" + status + " " + progress + "%] " + title + " (Due: " + dueDate + ")";
    }
}
