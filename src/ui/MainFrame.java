package ui;

import models.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private LoginPanel loginPanel;
    private EmployeePanel employeePanel;
    private TeamLeaderPanel teamLeaderPanel;
    private ProjectManagerPanel projectManagerPanel;
    private AdminPanel adminPanel;

    public MainFrame() {
        setTitle("Project Management System");
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UITheme.applyGlobalTheme();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(UITheme.BG_DARK);

        loginPanel = new LoginPanel(this);
        mainContainer.add(loginPanel, "LOGIN");

        add(mainContainer);
        showLogin();
    }

    public void showLogin() {
        // Clear old dashboards
        if (employeePanel != null) { mainContainer.remove(employeePanel); employeePanel = null; }
        if (teamLeaderPanel != null) { mainContainer.remove(teamLeaderPanel); teamLeaderPanel = null; }
        if (projectManagerPanel != null) { mainContainer.remove(projectManagerPanel); projectManagerPanel = null; }
        if (adminPanel != null) { mainContainer.remove(adminPanel); adminPanel = null; }

        loginPanel.reset();
        cardLayout.show(mainContainer, "LOGIN");
    }

    public void showDashboard(User user) {
        String role = user.getRole();
        switch (role) {
            case "ADMIN":
                adminPanel = new AdminPanel(this, user);
                mainContainer.add(adminPanel, "DASH_ADMIN");
                cardLayout.show(mainContainer, "DASH_ADMIN");
                break;
            case "PROJECT_MANAGER":
                projectManagerPanel = new ProjectManagerPanel(this, user);
                mainContainer.add(projectManagerPanel, "DASH_PM");
                cardLayout.show(mainContainer, "DASH_PM");
                break;
            case "TEAM_LEADER":
                teamLeaderPanel = new TeamLeaderPanel(this, user);
                mainContainer.add(teamLeaderPanel, "DASH_TL");
                cardLayout.show(mainContainer, "DASH_TL");
                break;
            case "EMPLOYEE":
                employeePanel = new EmployeePanel(this, user);
                mainContainer.add(employeePanel, "DASH_EMP");
                cardLayout.show(mainContainer, "DASH_EMP");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown role: " + role);
                showLogin();
        }
    }
}
