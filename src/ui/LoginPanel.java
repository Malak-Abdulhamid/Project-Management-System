package ui;

import services.AuthService;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * The login screen. Uses CardLayout to switch into role dashboards.
 */
public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_DARK);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        // Outer wrapper to center the card
        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(40, 48, 40, 48)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 480));

        // Logo / icon area
        JLabel icon = new JLabel("📋", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = UITheme.label("Project Management System", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = UITheme.label("Sign in to your account", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Username
        JLabel userLabel = UITheme.label("Username", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY);
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        usernameField = UITheme.styledTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);

        // Password
        JLabel passLabel = UITheme.label("Password", UITheme.FONT_LABEL, UITheme.TEXT_SECONDARY);
        passLabel.setAlignmentX(LEFT_ALIGNMENT);
        passwordField = UITheme.styledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);

        // Error label
        errorLabel = UITheme.label("", UITheme.FONT_SMALL, UITheme.ACCENT_RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Login button
        JButton loginBtn = UITheme.primaryButton("Sign In  →");
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> attemptLogin());

        // Allow pressing Enter to login
        passwordField.addActionListener(e -> attemptLogin());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());

        // Demo credentials hint
        JLabel hint = UITheme.label("Default admin: admin / admin123", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        hint.setAlignmentX(CENTER_ALIGNMENT);

        // Assemble card
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(sep);
        card.add(Box.createVerticalStrut(24));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(hint);

        add(card);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("⚠ Please enter both username and password.");
            return;
        }

        User user = AuthService.login(username, password);
        if (user == null) {
            errorLabel.setText("✗ Invalid username or password.");
            passwordField.setText("");
            return;
        }

        errorLabel.setText("");
        mainFrame.showDashboard(user);
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText("");
        usernameField.requestFocusInWindow();
    }
}
