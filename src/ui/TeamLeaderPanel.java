package ui;

import models.*;
import services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Team Leader Dashboard: Assign tasks, view completed tasks, view employees.
 */
public class TeamLeaderPanel extends JPanel {

    private MainFrame mainFrame;
    private User user;
    private TaskService taskService = new TaskService();
    private UserService userService = new UserService();
    private ProjectService projectService = new ProjectService();

    private DefaultTableModel taskTableModel;
    private DefaultTableModel completedTableModel;

    public TeamLeaderPanel(MainFrame mainFrame, User user) {
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
        JLabel chip = new JLabel("  TEAM LEADER  ");
        chip.setFont(UITheme.FONT_SMALL); chip.setOpaque(true);
        chip.setBackground(UITheme.ACCENT_ORANGE); chip.setForeground(UITheme.BG_DARK);
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
        tabs.addTab("📋  Assign Tasks", buildAssignTab());
        tabs.addTab("✅  Completed Tasks", buildCompletedTab());
        tabs.addTab("👥  Employees", buildEmployeesTab());
        return tabs;
    }

    private JPanel buildAssignTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Form
        JPanel form = UITheme.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = UITheme.styledTextField();
        JTextField descField = UITheme.styledTextField();
        JTextField dueField = UITheme.styledTextField();
        dueField.setText("yyyy-MM-dd");
        JComboBox<String> empCombo = UITheme.styledComboBox();
        JComboBox<String> projCombo = UITheme.styledComboBox();
        refreshFormCombos(empCombo, projCombo);

        addFormRow(form, g, 0, "Task Title:", titleField);
        addFormRow(form, g, 1, "Description:", descField);
        addFormRow(form, g, 2, "Assign To (Employee):", empCombo);
        addFormRow(form, g, 3, "Project:", projCombo);
        addFormRow(form, g, 4, "Due Date:", dueField);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        JButton assignBtn = UITheme.primaryButton("Assign Task");
        assignBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descField.getText().trim();
            String due = dueField.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Task Title is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!title.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(panel, "Error: Task Title must be a valid string (containing at least some letters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Task Description is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!desc.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(panel, "Error: Task Description must be a valid string (containing at least some letters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Date validation
            String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
            if (!due.matches(dateRegex)) {
                JOptionPane.showMessageDialog(panel, "Error: Due Date must be in YYYY-MM-DD format (e.g., 2023-12-31).", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (empCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(panel, "Warning: No employees available to assign this task to.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (projCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(panel, "Warning: No projects available to link this task to.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String empId = empCombo.getSelectedItem().toString().split(" \\| ")[0];
            String projId = projCombo.getSelectedItem().toString().split(" \\| ")[0];
            Task task = new Task(FileStorageService.generateId("TASK"), title, desc, empId, projId, "PENDING", due, user.getUsername());
            taskService.addTask(task);
            JOptionPane.showMessageDialog(panel, "✓ Success: Task has been assigned successfully!", "Task Assigned", JOptionPane.INFORMATION_MESSAGE);
            titleField.setText(""); descField.setText(""); dueField.setText("yyyy-MM-dd");
            refreshTaskTable();
        });
        form.add(assignBtn, g);

        // All tasks table
        String[] cols = {"ID", "Title", "Assigned To", "Project", "Status", "Due Date"};
        taskTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable taskTable = new JTable(taskTableModel);
        UITheme.styleTable(taskTable);
        refreshTaskTable();

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton del = UITheme.dangerButton("Delete Selected");
        del.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a task."); return; }
            String tid = taskTableModel.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(panel, "Delete task?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                taskService.deleteTask(tid);
                refreshTaskTable();
            }
        });
        btnRow.add(del);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(btnRow, BorderLayout.NORTH);
        JScrollPane sp = UITheme.styledScrollPane(taskTable);
        sp.setPreferredSize(new Dimension(0, 250));
        bottom.add(sp, BorderLayout.CENTER);

        panel.add(form, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel form, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; g.weightx = 0;
        form.add(UITheme.label(label, UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1;
        form.add(field, g);
    }

    private void refreshFormCombos(JComboBox<String> empCombo, JComboBox<String> projCombo) {
        empCombo.removeAllItems();
        userService.getUsersByRole("EMPLOYEE").forEach(e -> empCombo.addItem(e.getId() + " | " + e.getName()));
        projCombo.removeAllItems();
        projectService.getAllProjects().forEach(p -> projCombo.addItem(p.getId() + " | " + p.getName()));
    }

    private void refreshTaskTable() {
        if (taskTableModel == null) return;
        taskTableModel.setRowCount(0);
        taskService.getAllTasks().forEach(t -> {
            User emp = userService.getUserById(t.getAssignedEmployeeId());
            Project proj = projectService.getProjectById(t.getProjectId());
            taskTableModel.addRow(new Object[]{
                t.getId(), t.getTitle(),
                emp != null ? emp.getName() : t.getAssignedEmployeeId(),
                proj != null ? proj.getName() : t.getProjectId(),
                t.getStatus(), t.getDueDate()
            });
        });
    }

    private JPanel buildCompletedTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Task ID", "Title", "Employee", "Project", "Due Date"};
        completedTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(completedTableModel);
        UITheme.styleTable(table);
        refreshCompletedTable();

        panel.add(UITheme.label("Completed Tasks", UITheme.FONT_SUBTITLE, UITheme.ACCENT_GREEN), BorderLayout.NORTH);
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshCompletedTable() {
        if (completedTableModel == null) return;
        completedTableModel.setRowCount(0);
        taskService.getAllCompletedTasks().forEach(t -> {
            User emp = userService.getUserById(t.getAssignedEmployeeId());
            Project proj = projectService.getProjectById(t.getProjectId());
            completedTableModel.addRow(new Object[]{
                t.getId(), t.getTitle(),
                emp != null ? emp.getName() : t.getAssignedEmployeeId(),
                proj != null ? proj.getName() : t.getProjectId(),
                t.getDueDate()
            });
        });
    }

    private JPanel buildEmployeesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "Name", "Username", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        userService.getUsersByRole("EMPLOYEE").forEach(e ->
            model.addRow(new Object[]{e.getId(), e.getName(), e.getUsername(), e.getEmail(), e.getPhone()}));

        panel.add(UITheme.label("All Employees", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void refresh() { refreshTaskTable(); refreshCompletedTable(); }
}
