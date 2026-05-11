import ui.MainFrame;
import services.FileStorageService;
import models.Admin;
import services.UserService;

import javax.swing.*;

public class PMSApplication {
    public static void main(String[] args) {
        // Ensure data directory and files exist
        FileStorageService.initializeDataDirectory();

        // Create default admin if users file is empty
        UserService userService = new UserService();
        if (userService.getAllUsers().isEmpty()) {
            Admin defaultAdmin = new Admin(
                "USR_DEFAULT_ADMIN",
                "System Administrator",
                "admin",
                "admin123",
                "admin@pms.local",
                "0000000000"
            );
            userService.addUser(defaultAdmin);
            System.out.println("Default admin created: admin / admin123");
        }

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            // Use cross-platform look and feel to ensure theme consistency
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
