package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Shared UI theme constants and factory methods.
 * All panels use this for a consistent dark-mode look.
 */
public class UITheme {

    // Color palette (Light Theme)
    public static final Color BG_DARK = new Color(248, 250, 252);     // Slate 50
    public static final Color BG_CARD = new Color(255, 255, 255);     // White
    public static final Color BG_SIDEBAR = new Color(241, 245, 249);  // Slate 100
    public static final Color ACCENT_BLUE = new Color(37, 99, 235);   // Blue 600
    public static final Color ACCENT_PURPLE = new Color(124, 58, 237); // Violet 600
    public static final Color ACCENT_GREEN = new Color(22, 163, 74);  // Green 600
    public static final Color ACCENT_RED = new Color(220, 38, 38);    // Red 600
    public static final Color ACCENT_ORANGE = new Color(234, 88, 12); // Orange 600
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);   // Slate 900
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105); // Slate 600
    public static final Color TEXT_MUTED = new Color(148, 163, 184);  // Slate 400
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Slate 200
    public static final Color INPUT_BG = new Color(255, 255, 255);    // White

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);

    /** Create a styled primary button */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_LABEL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    /** Create a styled danger button (red) */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(ACCENT_RED);
        return btn;
    }

    /** Create a styled success button (green) */
    public static JButton successButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(ACCENT_GREEN);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    /** Create a styled secondary button */
    public static JButton secondaryButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(9, 19, 9, 19)));
        btn.setBorderPainted(true);
        return btn;
    }

    /** Style a text field */
    public static JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    /** Style a password field */
    public static JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(INPUT_BG);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return pf;
    }

    /** Style a text area */
    public static JTextArea styledTextArea() {
        JTextArea ta = new JTextArea();
        ta.setBackground(INPUT_BG);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return ta;
    }

    /** Style a combo box */
    public static <T> JComboBox<T> styledComboBox() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setBackground(INPUT_BG);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        ((JComponent) cb.getRenderer()).setOpaque(true);
        return cb;
    }

    /** Create a styled label */
    public static JLabel label(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    /** Create a card panel with rounded border feel */
    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    /** Style a JTable */
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setSelectionBackground(new Color(239, 246, 255)); // Light Blue
        table.setSelectionForeground(ACCENT_BLUE);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.getTableHeader().setBackground(BG_SIDEBAR);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        table.setFillsViewportHeight(true);
    }

    /** Style a JScrollPane */
    public static JScrollPane styledScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getVerticalScrollBar().setBackground(BG_SIDEBAR);
        sp.getHorizontalScrollBar().setBackground(BG_SIDEBAR);
        return sp;
    }

    /** Set global Swing defaults */
    public static void applyGlobalTheme() {
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("Panel.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        
        // Button styling for popups (High visibility dark text on light buttons)
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("Button.font", FONT_LABEL);
        
        // Explicitly fix OptionPane buttons
        UIManager.put("OptionPane.buttonFont", FONT_LABEL);
        UIManager.put("OptionPane.buttonBackground", Color.WHITE);
        UIManager.put("OptionPane.buttonForeground", TEXT_PRIMARY);
        UIManager.put("OptionPane.okButtonText", "  OK  ");
        
        UIManager.put("TextField.background", INPUT_BG);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.background", INPUT_BG);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", INPUT_BG);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.background", BG_DARK);
        UIManager.put("TabbedPane.foreground", TEXT_SECONDARY);
        UIManager.put("TabbedPane.selected", BG_CARD);
    }

    /** Role to accent color mapping */
    public static Color getRoleColor(String role) {
        switch (role) {
            case "ADMIN":
                return ACCENT_RED;
            case "PROJECT_MANAGER":
                return ACCENT_PURPLE;
            case "TEAM_LEADER":
                return ACCENT_ORANGE;
            case "EMPLOYEE":
                return ACCENT_GREEN;
            default:
                return ACCENT_BLUE;
        }
    }

    /** Role to display name */
    public static String getRoleDisplayName(String role) {
        switch (role) {
            case "ADMIN":
                return "Admin";
            case "PROJECT_MANAGER":
                return "Project Manager";
            case "TEAM_LEADER":
                return "Team Leader";
            case "EMPLOYEE":
                return "Employee";
            default:
                return role;
        }
    }
}
