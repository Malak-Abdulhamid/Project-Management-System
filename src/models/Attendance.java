package models;

public class Attendance {
    private String id;
    private String employeeId;
    private String date;
    private String signInTime;
    private String signOutTime;
    private boolean isLeave;
    private String leaveReason;
    private double penaltyAmount;
    private String penaltyReason;

    public Attendance(String id, String employeeId, String date, String signInTime,
                      String signOutTime, boolean isLeave, String leaveReason,
                      double penaltyAmount, String penaltyReason) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.signInTime = signInTime;
        this.signOutTime = signOutTime;
        this.isLeave = isLeave;
        this.leaveReason = leaveReason;
        this.penaltyAmount = penaltyAmount;
        this.penaltyReason = penaltyReason;
    }

    // Getters
    public String getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public String getSignInTime() { return signInTime; }
    public String getSignOutTime() { return signOutTime; }
    public boolean isLeave() { return isLeave; }
    public String getLeaveReason() { return leaveReason; }
    public double getPenaltyAmount() { return penaltyAmount; }
    public String getPenaltyReason() { return penaltyReason; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setDate(String date) { this.date = date; }
    public void setSignInTime(String signInTime) { this.signInTime = signInTime; }
    public void setSignOutTime(String signOutTime) { this.signOutTime = signOutTime; }
    public void setLeave(boolean leave) { isLeave = leave; }
    public void setLeaveReason(String leaveReason) { this.leaveReason = leaveReason; }
    public void setPenaltyAmount(double penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public void setPenaltyReason(String penaltyReason) { this.penaltyReason = penaltyReason; }

    /**
     * Calculate working hours for this attendance record.
     * Returns 0 if it's a leave day or times are missing.
     */
    public double calculateWorkingHours() {
        if (isLeave || signInTime == null || signInTime.isEmpty()
                || signOutTime == null || signOutTime.isEmpty()) {
            return 0;
        }
        try {
            String[] inParts = signInTime.split(":");
            String[] outParts = signOutTime.split(":");
            int inHour = Integer.parseInt(inParts[0]);
            int inMin = Integer.parseInt(inParts[1]);
            int outHour = Integer.parseInt(outParts[0]);
            int outMin = Integer.parseInt(outParts[1]);
            double inDecimal = inHour + inMin / 60.0;
            double outDecimal = outHour + outMin / 60.0;
            return Math.max(0, outDecimal - inDecimal);
        } catch (Exception e) {
            return 0;
        }
    }

    public String toFileString() {
        return id + "|" + employeeId + "|" + date + "|" + signInTime + "|" + signOutTime + "|" +
               isLeave + "|" + leaveReason + "|" + penaltyAmount + "|" + penaltyReason;
    }

    public static Attendance fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) return null;
        return new Attendance(
            parts[0], parts[1], parts[2], parts[3], parts[4],
            Boolean.parseBoolean(parts[5]), parts[6],
            Double.parseDouble(parts[7]), parts[8]
        );
    }

    @Override
    public String toString() {
        if (isLeave) return date + " | LEAVE - " + leaveReason;
        return date + " | In: " + signInTime + " | Out: " + signOutTime +
               " | Hours: " + String.format("%.1f", calculateWorkingHours());
    }
}
