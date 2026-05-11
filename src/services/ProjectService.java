package services;

import models.*;
import java.util.*;

/**
 * CRUD operations for Projects.
 */
public class ProjectService {

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        List<String> lines = FileStorageService.readLines(FileStorageService.PROJECTS_FILE);
        for (String line : lines) {
            Project p = Project.fromFileString(line);
            if (p != null) projects.add(p);
        }
        return projects;
    }

    public Project getProjectById(String id) {
        for (Project p : getAllProjects()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public List<Project> getProjectsByManager(String managerId) {
        List<Project> result = new ArrayList<>();
        for (Project p : getAllProjects()) {
            if (p.getManagerId().equals(managerId)) result.add(p);
        }
        return result;
    }

    public void addProject(Project project) {
        FileStorageService.appendLine(FileStorageService.PROJECTS_FILE, project.toFileString());
    }

    public boolean updateProject(Project updatedProject) {
        List<String> lines = FileStorageService.readLines(FileStorageService.PROJECTS_FILE);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            Project p = Project.fromFileString(line);
            if (p != null && p.getId().equals(updatedProject.getId())) {
                newLines.add(updatedProject.toFileString());
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.PROJECTS_FILE, newLines);
        return found;
    }

    public boolean deleteProject(String projectId) {
        List<String> lines = FileStorageService.readLines(FileStorageService.PROJECTS_FILE);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            Project p = Project.fromFileString(line);
            if (p != null && p.getId().equals(projectId)) {
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.PROJECTS_FILE, newLines);
        return found;
    }
}
