package ui;

import models.*;
import services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Employee Dashboard: Sign in/out, leave request, view tasks, view penalties, mark task done.
 */
public class EmployeePanel extends JPanel {

    private MainFrame mainFrame;
    private User user;
    private AttendanceService attendanceService = new AttendanceService();
    private TaskService taskService = new TaskService();

    private JLabel statusLabel;
    private JTable taskTable;
    private DefaultTableModel taskTableModel;
    private JTable attendanceTable;
    private DefaultTableModel attendanceTableModel;
    private JTable penaltyTable;
    private DefaultTableModel penaltyTableModel;

    public EmployeePanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
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
        JLabel roleChip = new JLabel("  EMPLOYEE  ");
        roleChip.setFont(UITheme.FONT_SMALL);
        roleChip.setOpaque(true);
        roleChip.setBackground(UITheme.ACCENT_GREEN);
        roleChip.setForeground(UITheme.BG_DARK);
        left.add(roleChip);
        left.add(UITheme.label("Welcome back, " + user.getName(), UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        statusLabel = UITheme.label("", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        right.add(statusLabel);

        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            AuthService.logout();
            mainFrame.showLogin();
        });
        right.add(logoutBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildContent() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_LABEL);

        tabs.addTab("⏰  Attendance", buildAttendanceTab());
        tabs.addTab("📋  My Tasks", buildTaskTab());
        tabs.addTab("⚠  Penalties", buildPenaltyTab());

        return tabs;
    }

    // ─────────────── ATTENDANCE TAB ───────────────
    private JPanel buildAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Action buttons row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton signInBtn = UITheme.successButton("✓  Sign In");
        signInBtn.addActionListener(e -> handleSignIn());

        JButton signOutBtn = UITheme.dangerButton("✗  Sign Out");
        signOutBtn.addActionListener(e -> handleSignOut());

        JButton leaveBtn = UITheme.secondaryButton("📅  Request Leave");
        leaveBtn.addActionListener(e -> handleLeave());

        // Monthly hours label
        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        double hours = attendanceService.getMonthlyWorkingHours(user.getId(), thisMonth);
        JLabel monthlyHours = UITheme.label(
            "  Monthly Hours (" + thisMonth + "): " + String.format("%.1f", hours) + "h",
            UITheme.FONT_LABEL, UITheme.ACCENT_BLUE
        );

        actions.add(signInBtn);
        actions.add(signOutBtn);
        actions.add(leaveBtn);
        actions.add(Box.createHorizontalStrut(20));
        actions.add(monthlyHours);

        // Attendance table
        String[] cols = {"Date", "Sign In", "Sign Out", "Hours", "Leave", "Leave Reason"};
        attendanceTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        attendanceTable = new JTable(attendanceTableModel);
        UITheme.styleTable(attendanceTable);
        JScrollPane sp = UITheme.styledScrollPane(attendanceTable);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        refreshAttendanceTable();
        return panel;
    }

    private void handleSignIn() {
        boolean ok = attendanceService.signIn(user.getId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "✓ Signed in successfully!", "Sign In", JOptionPane.INFORMATION_MESSAGE);
            refreshAttendanceTable();
            updateStatusLabel();
        } else {
            JOptionPane.showMessageDialog(this, "You have already signed in today.", "Sign In", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSignOut() {
        boolean ok = attendanceService.signOut(user.getId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "✓ Signed out successfully!", "Sign Out", JOptionPane.INFORMATION_MESSAGE);
            refreshAttendanceTable();
            updateStatusLabel();
        } else {
            JOptionPane.showMessageDialog(this, "You haven't signed in today, or already signed out.", "Sign Out", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleLeave() {
        String reason = JOptionPane.showInputDialog(this, "Enter leave reason:", "Request Leave", JOptionPane.QUESTION_MESSAGE);
        if (reason != null && !reason.trim().isEmpty()) {
            boolean ok = attendanceService.requestLeave(user.getId(), reason.trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "✓ Leave request recorded for today.", "Leave", JOptionPane.INFORMATION_MESSAGE);
                refreshAttendanceTable();
            } else {
                JOptionPane.showMessageDialog(this, "Attendance already recorded for today.", "Leave", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void refreshAttendanceTable() {
        attendanceTableModel.setRowCount(0);
        List<Attendance> records = attendanceService.getAttendanceByEmployee(user.getId());
        for (Attendance a : records) {
            attendanceTableModel.addRow(new Object[]{
                a.getDate(), a.getSignInTime(), a.getSignOutTime(),
                a.isLeave() ? "-" : String.format("%.1f", a.calculateWorkingHours()),
                a.isLeave() ? "Yes" : "No", a.getLeaveReason()
            });
        }
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        Attendance today = attendanceService.getTodayAttendance(user.getId());
        if (today == null) statusLabel.setText("Today: Not checked in");
        else if (today.isLeave()) statusLabel.setText("Today: On Leave");
        else if (today.getSignOutTime().isEmpty()) statusLabel.setText("Today: Checked in @ " + today.getSignInTime());
        else statusLabel.setText("Today: " + String.format("%.1f", today.calculateWorkingHours()) + "h worked");
    }

    // ─────────────── TASKS TAB ───────────────
    private JPanel buildTaskTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton markDoneBtn = UITheme.successButton("✓  Mark Completed");
        markDoneBtn.addActionListener(e -> handleMarkCompleted());

        JButton updateProgressBtn = UITheme.primaryButton("📈 Update Progress");
        updateProgressBtn.addActionListener(e -> handleUpdateProgress());

        actions.add(markDoneBtn);
        actions.add(updateProgressBtn);

        String[] cols = {"ID", "Title", "Project", "Progress", "Status", "Due Date", "Assigned By"};
        taskTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        taskTable = new JTable(taskTableModel);
        UITheme.styleTable(taskTable);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(UITheme.styledScrollPane(taskTable), BorderLayout.CENTER);

        refreshTaskTable();
        return panel;
    }

    private void handleMarkCompleted() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task from the list.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String taskId = taskTableModel.getValueAt(row, 0).toString();
        String status = taskTableModel.getValueAt(row, 4).toString();
        if ("COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this, "This task is already marked as completed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to mark this task as 100% completed?", "Confirm Completion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = taskService.markCompleted(taskId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "✓ Success: Task marked as completed!", "Updated", JOptionPane.INFORMATION_MESSAGE);
                refreshTaskTable();
            }
        }
    }

    private void handleUpdateProgress() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String taskId = taskTableModel.getValueAt(row, 0).toString();
        String status = taskTableModel.getValueAt(row, 4).toString();

        if ("COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Completed tasks cannot be updated.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = {"30% Done", "50% Done", "Completed Fully (100%)"};
        int choice = JOptionPane.showOptionDialog(this,
            "How much progress have you made on this task?",
            "Update Progress",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice == 0) updateTaskProgress(taskId, 30, "IN_PROGRESS");
        else if (choice == 1) updateTaskProgress(taskId, 50, "IN_PROGRESS");
        else if (choice == 2) handleMarkCompleted();
    }

    private void updateTaskProgress(String taskId, int pct, String status) {
        Task t = taskService.getTaskById(taskId);
        if (t != null) {
            t.setProgress(pct);
            t.setStatus(status);
            taskService.updateTask(t);
            JOptionPane.showMessageDialog(this, "✓ Progress updated to " + pct + "%.");
            refreshTaskTable();
        }
    }

    private void refreshTaskTable() {
        taskTableModel.setRowCount(0);
        ProjectService ps = new ProjectService();
        List<Task> tasks = taskService.getTasksByEmployee(user.getId());
        for (Task t : tasks) {
            Project proj = ps.getProjectById(t.getProjectId());
            taskTableModel.addRow(new Object[]{
                t.getId(), t.getTitle(),
                proj != null ? proj.getName() : t.getProjectId(),
                t.getProgress() + "%",
                t.getStatus(), t.getDueDate(), t.getAssignedBy()
            });
        }
    }

    // ─────────────── PENALTIES TAB ───────────────
    private JPanel buildPenaltyTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Date", "Amount ($)", "Reason"};
        penaltyTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        penaltyTable = new JTable(penaltyTableModel);
        UITheme.styleTable(penaltyTable);

        refreshPenaltyTable();

        panel.add(UITheme.label("Your Penalties", UITheme.FONT_SUBTITLE, UITheme.ACCENT_RED), BorderLayout.NORTH);
        panel.add(UITheme.styledScrollPane(penaltyTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshPenaltyTable() {
        penaltyTableModel.setRowCount(0);
        List<Attendance> penalties = attendanceService.getPenaltiesByEmployee(user.getId());
        double total = 0;
        for (Attendance a : penalties) {
            penaltyTableModel.addRow(new Object[]{a.getDate(), a.getPenaltyAmount(), a.getPenaltyReason()});
            total += a.getPenaltyAmount();
        }
        // Add total row if any
        if (!penalties.isEmpty()) {
            penaltyTableModel.addRow(new Object[]{"TOTAL", total, ""});
        }
    }

    public void refresh() {
        refreshAttendanceTable();
        refreshTaskTable();
        refreshPenaltyTable();
    }
}
