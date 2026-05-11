package services;

import models.*;
import java.util.*;

/**
 * CRUD operations for Tasks (Team Leader + Employee module).
 */
public class TaskService {

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        List<String> lines = FileStorageService.readLines(FileStorageService.TASKS_FILE);
        for (String line : lines) {
            Task t = Task.fromFileString(line);
            if (t != null) tasks.add(t);
        }
        return tasks;
    }

    public Task getTaskById(String id) {
        for (Task t : getAllTasks()) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    /** Get tasks assigned to a specific employee */
    public List<Task> getTasksByEmployee(String employeeId) {
        List<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if (t.getAssignedEmployeeId().equals(employeeId)) result.add(t);
        }
        return result;
    }

    /** Get tasks for a specific project */
    public List<Task> getTasksByProject(String projectId) {
        List<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if (t.getProjectId().equals(projectId)) result.add(t);
        }
        return result;
    }

    /** Get completed tasks for a specific employee */
    public List<Task> getCompletedTasksByEmployee(String employeeId) {
        List<Task> result = new ArrayList<>();
        for (Task t : getTasksByEmployee(employeeId)) {
            if ("COMPLETED".equals(t.getStatus())) result.add(t);
        }
        return result;
    }

    /** Get all completed tasks (for TL view) */
    public List<Task> getAllCompletedTasks() {
        List<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if ("COMPLETED".equals(t.getStatus())) result.add(t);
        }
        return result;
    }

    public void addTask(Task task) {
        FileStorageService.appendLine(FileStorageService.TASKS_FILE, task.toFileString());
    }

    public boolean updateTask(Task updatedTask) {
        List<String> lines = FileStorageService.readLines(FileStorageService.TASKS_FILE);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            Task t = Task.fromFileString(line);
            if (t != null && t.getId().equals(updatedTask.getId())) {
                newLines.add(updatedTask.toFileString());
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.TASKS_FILE, newLines);
        return found;
    }

    public boolean deleteTask(String taskId) {
        List<String> lines = FileStorageService.readLines(FileStorageService.TASKS_FILE);
        List<String> newLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            Task t = Task.fromFileString(line);
            if (t != null && t.getId().equals(taskId)) {
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileStorageService.writeLines(FileStorageService.TASKS_FILE, newLines);
        return found;
    }

    /** Mark a task as completed */
    public boolean markCompleted(String taskId) {
        Task task = getTaskById(taskId);
        if (task == null) return false;
        task.setStatus("COMPLETED");
        task.setProgress(100);
        return updateTask(task);
    }

    /**
     * Calculate completion percentage for a project.
     * = (Sum of task progress percentages) / totalTasks
     */
    public double getProjectCompletionPercentage(String projectId) {
        List<Task> projectTasks = getTasksByProject(projectId);
        if (projectTasks.isEmpty()) return 0;
        double totalProgress = projectTasks.stream().mapToDouble(Task::getProgress).sum();
        return totalProgress / projectTasks.size();
    }
}
