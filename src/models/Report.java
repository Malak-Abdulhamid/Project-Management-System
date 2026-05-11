package models;

public class Report {
    private String id;
    private String title;
    private String content;
    private String createdByManagerId;
    private String targetTeamLeaderId;
    private String employeeId;
    private String createdDate;

    public Report(String id, String title, String content, String createdByManagerId,
                  String targetTeamLeaderId, String employeeId, String createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdByManagerId = createdByManagerId;
        this.targetTeamLeaderId = targetTeamLeaderId;
        this.employeeId = employeeId;
        this.createdDate = createdDate;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCreatedByManagerId() { return createdByManagerId; }
    public String getTargetTeamLeaderId() { return targetTeamLeaderId; }
    public String getEmployeeId() { return employeeId; }
    public String getCreatedDate() { return createdDate; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedByManagerId(String id) { this.createdByManagerId = id; }
    public void setTargetTeamLeaderId(String id) { this.targetTeamLeaderId = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String toFileString() {
        // Replace newlines in content with a placeholder to keep one record per line
        String safeContent = content.replace("\n", "<<NL>>").replace("|", "&#124;");
        return id + "|" + title + "|" + safeContent + "|" + createdByManagerId + "|" +
               targetTeamLeaderId + "|" + employeeId + "|" + createdDate;
    }

    public static Report fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) return null;
        String content = parts[2].replace("<<NL>>", "\n").replace("&#124;", "|");
        return new Report(parts[0], parts[1], content, parts[3], parts[4], parts[5], parts[6]);
    }

    @Override
    public String toString() {
        return "[" + createdDate + "] " + title;
    }
}
