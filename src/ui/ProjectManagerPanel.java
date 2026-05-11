package ui;

import models.*;
import services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProjectManagerPanel extends JPanel {

    private MainFrame mainFrame;
    private User user;
    private ProjectService projectService = new ProjectService();
    private TaskService taskService = new TaskService();
    private ReportService reportService = new ReportService();
    private UserService userService = new UserService();

    private DefaultTableModel projectTableModel;
    private DefaultTableModel reportTableModel;

    public ProjectManagerPanel(MainFrame mainFrame, User user) {
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
        JLabel chip = new JLabel("  PROJECT MANAGER  ");
        chip.setFont(UITheme.FONT_SMALL); chip.setOpaque(true);
        chip.setBackground(UITheme.ACCENT_PURPLE); chip.setForeground(Color.WHITE);
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
        tabs.addTab("📊  Project Progress", buildProjectTab());
        tabs.addTab("📝  Reports", buildReportsTab());
        tabs.addTab("➕  Add Project", buildAddProjectTab());
        return tabs;
    }

    private JPanel buildProjectTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Project ID", "Name", "Status", "Start Date", "End Date", "Completion %"};
        projectTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(projectTableModel);
        UITheme.styleTable(table);
        refreshProjectTable();

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton refreshBtn = UITheme.secondaryButton("↻ Refresh");
        refreshBtn.addActionListener(e -> refreshProjectTable());
        btnRow.add(refreshBtn);

        panel.add(UITheme.label("Project Completion Overview", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);
        panel.add(btnRow, BorderLayout.SOUTH);
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshProjectTable() {
        if (projectTableModel == null) return;
        projectTableModel.setRowCount(0);
        List<Project> projects = projectService.getAllProjects();
        for (Project p : projects) {
            double pct = taskService.getProjectCompletionPercentage(p.getId());
            projectTableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getStatus(),
                p.getStartDate(), p.getEndDate(),
                String.format("%.1f%%", pct)
            });
        }
    }

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = UITheme.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = UITheme.styledTextField();
        JTextArea contentArea = UITheme.styledTextArea();
        contentArea.setRows(4);
        JScrollPane contentScroll = UITheme.styledScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(300, 80));

        JComboBox<String> tlCombo = UITheme.styledComboBox();
        JComboBox<String> empCombo = UITheme.styledComboBox();
        refreshReportCombos(tlCombo, empCombo);

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        form.add(UITheme.label("Report Title:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1;
        form.add(titleField, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(UITheme.label("Send To (Team Leader):", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1;
        form.add(tlCombo, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        form.add(UITheme.label("About Employee:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1;
        form.add(empCombo, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        form.add(UITheme.label("Content:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1;
        form.add(contentScroll, g);

        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        JButton sendBtn = UITheme.primaryButton("Send Report to Team Leader");
        sendBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Report title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!title.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(panel, "Error: Report title must contain at least some letters (it must be a valid string).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Report content cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tlCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(panel, "Warning: No Team Leaders found in the system.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String tlId = tlCombo.getSelectedItem().toString().split(" \\| ")[0];
            String empId = empCombo.getItemCount() > 0 ? empCombo.getSelectedItem().toString().split(" \\| ")[0] : "";
            String date = java.time.LocalDate.now().toString();
            Report r = new Report(FileStorageService.generateId("RPT"), title, content, user.getId(), tlId, empId, date);
            reportService.addReport(r);
            JOptionPane.showMessageDialog(panel, "✓ Success: Your report has been sent to the Team Leader.", "Report Sent", JOptionPane.INFORMATION_MESSAGE);
            titleField.setText(""); contentArea.setText("");
            refreshReportTable();
        });
        form.add(sendBtn, g);

        String[] cols = {"ID", "Title", "Sent To (TL)", "About Employee", "Date"};
        reportTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(reportTableModel);
        UITheme.styleTable(table);
        refreshReportTable();

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton viewBtn = UITheme.secondaryButton("View Content");
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a report first."); return; }
            String rid = reportTableModel.getValueAt(row, 0).toString();
            Report r = reportService.getReportById(rid);
            if (r != null) {
                JTextArea ta = new JTextArea(r.getContent());
                ta.setEditable(false); ta.setBackground(UITheme.INPUT_BG);
                ta.setForeground(UITheme.TEXT_PRIMARY); ta.setFont(UITheme.FONT_BODY);
                JOptionPane.showMessageDialog(panel, new JScrollPane(ta), "Report: " + r.getTitle(), JOptionPane.PLAIN_MESSAGE);
            }
        });
        JButton delBtn = UITheme.dangerButton("Delete");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a report."); return; }
            String rid = reportTableModel.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(panel, "Delete report?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                reportService.deleteReport(rid);
                refreshReportTable();
            }
        });
        btnRow.add(viewBtn); btnRow.add(delBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(btnRow, BorderLayout.NORTH);
        bottom.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);

        panel.add(form, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private void refreshReportCombos(JComboBox<String> tlCombo, JComboBox<String> empCombo) {
        tlCombo.removeAllItems();
        userService.getUsersByRole("TEAM_LEADER").forEach(tl -> tlCombo.addItem(tl.getId() + " | " + tl.getName()));
        empCombo.removeAllItems();
        userService.getUsersByRole("EMPLOYEE").forEach(e -> empCombo.addItem(e.getId() + " | " + e.getName()));
    }

    private void refreshReportTable() {
        if (reportTableModel == null) return;
        reportTableModel.setRowCount(0);
        reportService.getReportsByManager(user.getId()).forEach(r -> {
            User tl = userService.getUserById(r.getTargetTeamLeaderId());
            User emp = userService.getUserById(r.getEmployeeId());
            reportTableModel.addRow(new Object[]{
                r.getId(), r.getTitle(),
                tl != null ? tl.getName() : r.getTargetTeamLeaderId(),
                emp != null ? emp.getName() : r.getEmployeeId(),
                r.getCreatedDate()
            });
        });
    }

    private JPanel buildAddProjectTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = UITheme.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = UITheme.styledTextField();
        JTextField descField = UITheme.styledTextField();
        JTextField startField = UITheme.styledTextField(); startField.setText("yyyy-MM-dd");
        JTextField endField = UITheme.styledTextField(); endField.setText("yyyy-MM-dd");
        String[] statuses = {"ACTIVE", "ON_HOLD", "COMPLETED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setBackground(UITheme.INPUT_BG);
        statusCombo.setForeground(UITheme.TEXT_PRIMARY);

        g.gridx = 0; g.gridy = 0; g.weightx = 0; form.add(UITheme.label("Project Name:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(nameField, g);
        g.gridx = 0; g.gridy = 1; g.weightx = 0; form.add(UITheme.label("Description:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(descField, g);
        g.gridx = 0; g.gridy = 2; g.weightx = 0; form.add(UITheme.label("Start Date:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(startField, g);
        g.gridx = 0; g.gridy = 3; g.weightx = 0; form.add(UITheme.label("End Date:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(endField, g);
        g.gridx = 0; g.gridy = 4; g.weightx = 0; form.add(UITheme.label("Status:", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY), g);
        g.gridx = 1; g.weightx = 1; form.add(statusCombo, g);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        JButton addBtn = UITheme.primaryButton("Create Project");
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            String start = startField.getText().trim();
            String end = endField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Project Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!name.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(panel, "Error: Project Name must be a valid string (containing at least some letters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Error: Description is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!desc.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(panel, "Error: Description must be a valid string (containing at least some letters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Date validation (yyyy-mm-dd)
            String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
            if (!start.matches(dateRegex)) {
                JOptionPane.showMessageDialog(panel, "Error: Start Date must be in YYYY-MM-DD format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!end.matches(dateRegex)) {
                JOptionPane.showMessageDialog(panel, "Error: End Date must be in YYYY-MM-DD format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Compare dates
            try {
                java.time.LocalDate startDate = java.time.LocalDate.parse(start);
                java.time.LocalDate endDate = java.time.LocalDate.parse(end);
                if (!endDate.isAfter(startDate)) {
                    JOptionPane.showMessageDialog(panel, "Error: Project End Date must be AFTER the Start Date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: Invalid date values provided.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String pid = FileStorageService.generateId("PROJ");
            Project proj = new Project(pid, name, desc,
                statusCombo.getSelectedItem().toString(), start, end, user.getId());
            projectService.addProject(proj);
            JOptionPane.showMessageDialog(panel, "✓ Success: The project has been created successfully!", "Project Created", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText(""); descField.setText("");
            startField.setText("yyyy-MM-dd"); endField.setText("yyyy-MM-dd");
            refreshProjectTable();
        });
        form.add(addBtn, g);

        panel.add(UITheme.label("Create New Project", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() { refreshProjectTable(); refreshReportTable(); }
}
