package services;

import models.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Attendance tracking service (Employee module).
 * Handles sign-in/out, leave requests, penalties.
 */
public class AttendanceService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        List<String> lines = FileStorageService.readLines(FileStorageService.ATTENDANCE_FILE);
        for (String line : lines) {
            Attendance a = Attendance.fromFileString(line);
            if (a != null) list.add(a);
        }
        return list;
    }

    public List<Attendance> getAttendanceByEmployee(String employeeId) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : getAllAttendance()) {
            if (a.getEmployeeId().equals(employeeId)) result.add(a);
        }
        return result;
    }

    /** Get today's attendance record for an employee, or null if none */
    public Attendance getTodayAttendance(String employeeId) {
        String today = LocalDate.now().format(DATE_FMT);
        for (Attendance a : getAttendanceByEmployee(employeeId)) {
            if (a.getDate().equals(today)) return a;
        }
        return null;
    }

    /**
     * Sign in the employee for today.
     * Returns false if already signed in today.
     */
    public boolean signIn(String employeeId) {
        if (getTodayAttendance(employeeId) != null) return false;
        String today = LocalDate.now().format(DATE_FMT);
        String now = LocalTime.now().format(TIME_FMT);
        String id = FileStorageService.generateId("ATT");
        Attendance a = new Attendance(id, employeeId, today, now, "", false, "", 0, "");
        FileStorageService.appendLine(FileStorageService.ATTENDANCE_FILE, a.toFileString());
        return true;
    }

    /**
     * Sign out the employee for today.
     * Returns false if not signed in today, or already signed out.
     */
    public boolean signOut(String employeeId) {
        Attendance today = getTodayAttendance(employeeId);
        if (today == null || !today.getSignOutTime().isEmpty()) return false;
        String now = LocalTime.now().format(TIME_FMT);
        today.setSignOutTime(now);

        // Apply penalty if working hours < 8
        double hours = today.calculateWorkingHours();
        if (hours < 8 && hours > 0) {
            double shortage = 8 - hours;
            today.setPenaltyAmount(shortage * 10); // $10 per missing hour
            today.setPenaltyReason("Early departure: " + String.format("%.1f", shortage) + "h short");
        }

        return updateAttendance(today);
    }

    /**
     * Record a leave request for today.
     * Returns false if attendance already exists for today.
     */
    public boolean requestLeave(String employeeId, String reason) {
        if (getTodayAttendance(employeeId) != null) return false;
        String today = LocalDate.now().format(DATE_FMT);
        String id = FileStorageService.generateId("ATT");
        Attendance a = new Attendance(id, employeeId, today, "", "", true, reason, 0, "");
        FileStorageService.appendLine(FileStorageService.ATTENDANCE_FILE, a.toFileString());
        return true;
    }

    /** Add a penalty to an employee for a specific date */
    public boolean addPenalty(String employeeId, String date, double amount, String reason) {
        List<Attendance> all = getAllAttendance();
        boolean found = false;
        List<String> newLines = new ArrayList<>();
        for (Attendance a : all) {
            if (a.getEmployeeId().equals(employeeId) && a.getDate().equals(date)) {
                a.setPenaltyAmount(a.getPenaltyAmount() + amount);
                a.setPenaltyReason(reason);
                newLines.add(a.toFileString());
                found = true;
            } else {
                newLines.add(a.toFileString());
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.ATTENDANCE_FILE, newLines);
        return found;
    }

    /** Get all penalties for an employee */
    public List<Attendance> getPenaltiesByEmployee(String employeeId) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : getAttendanceByEmployee(employeeId)) {
            if (a.getPenaltyAmount() > 0) result.add(a);
        }
        return result;
    }

    /**
     * Calculate total working hours for an employee in a given month (yyyy-MM).
     */
    public double getMonthlyWorkingHours(String employeeId, String yearMonth) {
        double total = 0;
        for (Attendance a : getAttendanceByEmployee(employeeId)) {
            if (a.getDate().startsWith(yearMonth)) {
                total += a.calculateWorkingHours();
            }
        }
        return total;
    }

    private boolean updateAttendance(Attendance updated) {
        List<Attendance> all = getAllAttendance();
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (Attendance a : all) {
            if (a.getId().equals(updated.getId())) {
                newLines.add(updated.toFileString());
                found = true;
            } else {
                newLines.add(a.toFileString());
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.ATTENDANCE_FILE, newLines);
        return found;
    }
}
