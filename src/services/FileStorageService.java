package services;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Central file I/O service. All reads/writes go through here.
 * Data format uses '|' as delimiter within records, one record per line.
 */
public class FileStorageService {

    public static final String DATA_DIR = "data";
    public static final String USERS_FILE = DATA_DIR + "/users.txt";
    public static final String TASKS_FILE = DATA_DIR + "/tasks.txt";
    public static final String ATTENDANCE_FILE = DATA_DIR + "/attendance.txt";
    public static final String REPORTS_FILE = DATA_DIR + "/reports.txt";
    public static final String PROJECTS_FILE = DATA_DIR + "/projects.txt";

    static {
        // Ensure data directory exists on first use
        new File(DATA_DIR).mkdirs();
    }

    /** Read all lines from a file. Returns empty list if file doesn't exist. */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " -> " + e.getMessage());
        }
        return lines;
    }

    /** Overwrite a file with the given list of lines. */
    public static void writeLines(String filePath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + filePath + " -> " + e.getMessage());
        }
    }

    /** Append a single line to a file. */
    public static void appendLine(String filePath, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath + " -> " + e.getMessage());
        }
    }

    /** Generate a unique ID using timestamp + random suffix. */
    public static String generateId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /** Check if data directory and default files exist, create if not. */
    public static void initializeDataDirectory() {
        new File(DATA_DIR).mkdirs();
        createIfNotExists(USERS_FILE);
        createIfNotExists(TASKS_FILE);
        createIfNotExists(ATTENDANCE_FILE);
        createIfNotExists(REPORTS_FILE);
        createIfNotExists(PROJECTS_FILE);
    }

    private static void createIfNotExists(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create file: " + filePath);
            }
        }
    }
}
