package services;

import models.*;
import java.util.*;

/**
 * Report creation and retrieval (Project Manager module).
 */
public class ReportService {

    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        List<String> lines = FileStorageService.readLines(FileStorageService.REPORTS_FILE);
        for (String line : lines) {
            Report r = Report.fromFileString(line);
            if (r != null) reports.add(r);
        }
        return reports;
    }

    public Report getReportById(String id) {
        for (Report r : getAllReports()) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public List<Report> getReportsByManager(String managerId) {
        List<Report> result = new ArrayList<>();
        for (Report r : getAllReports()) {
            if (r.getCreatedByManagerId().equals(managerId)) result.add(r);
        }
        return result;
    }

    /** Reports sent to a specific Team Leader */
    public List<Report> getReportsByTeamLeader(String teamLeaderId) {
        List<Report> result = new ArrayList<>();
        for (Report r : getAllReports()) {
            if (r.getTargetTeamLeaderId().equals(teamLeaderId)) result.add(r);
        }
        return result;
    }

    public void addReport(Report report) {
        FileStorageService.appendLine(FileStorageService.REPORTS_FILE, report.toFileString());
    }

    public boolean deleteReport(String reportId) {
        List<String> lines = FileStorageService.readLines(FileStorageService.REPORTS_FILE);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            Report r = Report.fromFileString(line);
            if (r != null && r.getId().equals(reportId)) {
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.REPORTS_FILE, newLines);
        return found;
    }
}
