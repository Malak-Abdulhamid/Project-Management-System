package ui;

import models.*;
import services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private MainFrame mainFrame;
    private User user;
    private UserService userService = new UserService();
    private ProjectService projectService = new ProjectService();

    private DefaultTableModel userTableModel;
    private DefaultTableModel projectTableModel;
    private String selectedUserId = null;

    public AdminPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_SIDEBAR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JLabel chip = new JLabel("  ADMIN  ");
        chip.setFont(UITheme.FONT_SMALL); chip.setOpaque(true);
        chip.setBackground(UITheme.ACCENT_RED); chip.setForeground(Color.WHITE);
        left.add(chip);
        left.add(UITheme.label("Welcome, " + user.getName(), UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY));
        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> { AuthService.logout(); mainFrame.showLogin(); });
        header.add(left, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_LABEL);
        tabs.addTab("👥  Manage Users", buildUsersTab());
        tabs.addTab("📊  All Projects", buildProjectsTab());
        return tabs;
    }

    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = UITheme.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = UITheme.styledTextField();
        JTextField userField = UITheme.styledTextField();
        JPasswordField passField = UITheme.styledPasswordField();
        JTextField emailField = UITheme.styledTextField();
        JTextField phoneField = UITheme.styledTextField();
        String[] roles = {"EMPLOYEE", "TEAM_LEADER", "PROJECT_MANAGER", "ADMIN"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setBackground(UITheme.INPUT_BG); roleCombo.setForeground(UITheme.TEXT_PRIMARY);

        g.gridx = 0; g.gridy = 0; g.weightx = 0; form.add(UITheme.label("Name:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(nameField, g);
        g.gridx = 0; g.gridy = 1; g.weightx = 0; form.add(UITheme.label("Username:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(userField, g);
        g.gridx = 0; g.gridy = 2; g.weightx = 0; form.add(UITheme.label("Password:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(passField, g);
        g.gridx = 0; g.gridy = 3; g.weightx = 0; form.add(UITheme.label("Role:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(roleCombo, g);
        g.gridx = 0; g.gridy = 4; g.weightx = 0; form.add(UITheme.label("Email:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(emailField, g);
        g.gridx = 0; g.gridy = 5; g.weightx = 0; form.add(UITheme.label("Phone:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(phoneField, g);

        g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton addBtn = UITheme.primaryButton("Add User");
        JButton updateBtn = UITheme.successButton("Update User");
        JButton clearBtn = UITheme.secondaryButton("Clear Form");
        
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(clearBtn);
        form.add(btnPanel, g);

        addBtn.addActionListener(e -> {
            String uname = userField.getText().trim();
            String name = nameField.getText().trim();
            String pass = new String(passField.getPassword());
            String em = emailField.getText().trim();
            String ph = phoneField.getText().trim();
            
            if (uname.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Name, Username, and Password are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!name.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(panel, "Error: The Name field must contain only letters and spaces (no numbers).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!ph.matches("\\d+")) {
                JOptionPane.showMessageDialog(panel, "Error: The Phone field must contain only digits (no letters or spaces).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!userService.isUsernameAvailable(uname)) {
                JOptionPane.showMessageDialog(panel, "Error: This username is already taken. Please choose another one.", "Username Taken", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String uid = FileStorageService.generateId("USR");
            User newUser;
            String role = roleCombo.getSelectedItem().toString();
            
            if (role.equals("ADMIN")) newUser = new Admin(uid, name, uname, pass, em, ph);
            else if (role.equals("PROJECT_MANAGER")) newUser = new ProjectManager(uid, name, uname, pass, em, ph);
            else if (role.equals("TEAM_LEADER")) newUser = new TeamLeader(uid, name, uname, pass, em, ph);
            else newUser = new Employee(uid, name, uname, pass, em, ph);

            userService.addUser(newUser);
            JOptionPane.showMessageDialog(panel, "✓ Success: The new user account has been created.", "User Added", JOptionPane.INFORMATION_MESSAGE);
            clearFields(nameField, userField, passField, emailField, phoneField, roleCombo);
            refreshUserTable();
        });

        updateBtn.addActionListener(e -> {
            if (selectedUserId == null) {
                JOptionPane.showMessageDialog(panel, "Error: Please select a user from the table to update.", "No User Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String uname = userField.getText().trim();
            String name = nameField.getText().trim();
            String pass = new String(passField.getPassword());
            String em = emailField.getText().trim();
            String ph = phoneField.getText().trim();

            if (uname.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Name, Username, and Password are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User updatedUser;
            String role = roleCombo.getSelectedItem().toString();
            if (role.equals("ADMIN")) updatedUser = new Admin(selectedUserId, name, uname, pass, em, ph);
            else if (role.equals("PROJECT_MANAGER")) updatedUser = new ProjectManager(selectedUserId, name, uname, pass, em, ph);
            else if (role.equals("TEAM_LEADER")) updatedUser = new TeamLeader(selectedUserId, name, uname, pass, em, ph);
            else updatedUser = new Employee(selectedUserId, name, uname, pass, em, ph);

            if (userService.updateUser(updatedUser)) {
                JOptionPane.showMessageDialog(panel, "✓ Success: The user data has been updated.", "User Updated", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(panel, "Error: Failed to update user.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearBtn.addActionListener(e -> {
            clearFields(nameField, userField, passField, emailField, phoneField, roleCombo);
            selectedUserId = null;
        });

        String[] cols = {"ID", "Name", "Username", "Role", "Email"};
        userTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(userTableModel);
        UITheme.styleTable(table);
        refreshUserTable();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String uid = userTableModel.getValueAt(row, 0).toString();
                    User u = userService.getUserById(uid);
                    if (u != null) {
                        selectedUserId = u.getId();
                        nameField.setText(u.getName());
                        userField.setText(u.getUsername());
                        passField.setText(u.getPassword());
                        emailField.setText(u.getEmail());
                        phoneField.setText(u.getPhone());
                        roleCombo.setSelectedItem(u.getRole());
                    }
                }
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton delBtn = UITheme.dangerButton("Delete Selected User");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a user."); return; }
            String uid = userTableModel.getValueAt(row, 0).toString();
            if (uid.equals(user.getId())) { JOptionPane.showMessageDialog(panel, "Cannot delete yourself!"); return; }
            if (JOptionPane.showConfirmDialog(panel, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                userService.deleteUser(uid);
                refreshUserTable();
            }
        });
        btnRow.add(delBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(btnRow, BorderLayout.NORTH);
        bottom.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);

        panel.add(form, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private void refreshUserTable() {
        if (userTableModel == null) return;
        userTableModel.setRowCount(0);
        userService.getAllUsers().forEach(u ->
            userTableModel.addRow(new Object[]{u.getId(), u.getName(), u.getUsername(), u.getRole(), u.getEmail()}));
    }

    private JPanel buildProjectsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "Name", "Description", "Manager", "Status", "Start", "End"};
        projectTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(projectTableModel);
        UITheme.styleTable(table);
        refreshProjectTable();

        panel.add(UITheme.label("All Projects in System", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshProjectTable() {
        if (projectTableModel == null) return;
        projectTableModel.setRowCount(0);
        projectService.getAllProjects().forEach(p -> {
            User m = userService.getUserById(p.getManagerId());
            projectTableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getDescription(),
                m != null ? m.getName() : p.getManagerId(),
                p.getStatus(), p.getStartDate(), p.getEndDate()
            });
        });
    }

    public void refresh() { refreshUserTable(); refreshProjectTable(); }

    private void clearFields(JTextField name, JTextField user, JPasswordField pass, JTextField email, JTextField phone, JComboBox<String> role) {
        name.setText("");
        user.setText("");
        pass.setText("");
        email.setText("");
        phone.setText("");
        role.setSelectedIndex(0);
    }
}
