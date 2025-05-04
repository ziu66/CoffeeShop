/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter; // Import MouseAdapter for button hover
import java.awt.event.MouseEvent; // Import MouseEvent for button hover
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// Assuming User and UserDAO classes exist
// import coffeeshop.User;
// import coffeeshop.UserDAO;


public class LoginForm extends JFrame {
    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final JButton btnSignIn;
    private final JButton btnRegisterNow;
    // Removed: private final JLabel lblForgotPassword; // REMOVED

    // Define the gold accent color
    private final Color GOLD_ACCENT = new Color(218, 165, 32);

    public LoginForm() {
        setTitle("But First, Coffee - Log In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 800); // Maintained overall form size
        setLocationRelativeTo(null);

        // Main panel with  dark theme
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30)); // Dark background

        // Add the header panel with reduced size
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create central content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(30, 30, 30));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 70, 40, 70)); // Increased padding

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(40, 50, 50, 50) // Increased internal padding
        ));

        // Form Title
        JLabel lblTitle = new JLabel("Log in to your account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Increased font size
        lblTitle.setForeground(GOLD_ACCENT); // Gold text
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email field label (center-aligned)
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email text field
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBackground(new Color(60, 60, 60));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setCaretColor(Color.WHITE);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password field label (center-aligned)
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password text field
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBackground(new Color(60, 60, 60));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Log in button - STYLING MODIFIED HERE
        btnSignIn = new JButton("Log In");
        btnSignIn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Slightly increased button font size
        btnSignIn.setForeground(Color.BLACK); // Text color is black
        btnSignIn.setBackground(GOLD_ACCENT); // Background is gold
        btnSignIn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 1), // Lighter gold border for hover
            BorderFactory.createEmptyBorder(10, 30, 10, 30) // Increased horizontal padding for button width
        ));
        btnSignIn.setFocusPainted(false);
        btnSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSignIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSignIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Increased max height for button
        btnSignIn.setContentAreaFilled(true); // Ensure background is painted
        btnSignIn.setOpaque(true); // Ensure background is opaque

        // Add hover effect similar to AdminDashboard buttons
        btnSignIn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnSignIn.setBackground(new Color(255, 215, 0)); // Brighter gold on hover
            }
            public void mouseExited(MouseEvent evt) {
                btnSignIn.setBackground(GOLD_ACCENT); // Back to normal gold
            }
        });


        // Register Now section
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBackground(new Color(40, 40, 40));
        registerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // "No account?" text
        JLabel lblNoAccount = new JLabel("No account?");
        lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNoAccount.setForeground(Color.WHITE);
        lblNoAccount.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Register Now button (unchanged styling - intentionally different)
        btnRegisterNow = new JButton("Register Now");
        btnRegisterNow.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegisterNow.setForeground(GOLD_ACCENT); // Gold text
        btnRegisterNow.setBackground(new Color(40, 40, 40));
        btnRegisterNow.setBorder(null);
        btnRegisterNow.setFocusPainted(false);
        btnRegisterNow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegisterNow.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegisterNow.setContentAreaFilled(false);

        // Hover effect for Register Now button (optional, matching other links/text buttons)
         btnRegisterNow.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnRegisterNow.setForeground(new Color(255, 215, 0)); // Brighter gold on hover
            }
            public void mouseExited(MouseEvent evt) {
                btnRegisterNow.setForeground(GOLD_ACCENT); // Back to normal gold
            }
        });


        registerPanel.add(lblNoAccount);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        registerPanel.add(btnRegisterNow);

        // Add components to form panel with spacing - ADJUSTED SPACING
        formPanel.add(lblTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        formPanel.add(lblEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(lblPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 38))); // Increased space between password field and Sign In button (was 30 + 8)
        formPanel.add(btnSignIn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(registerPanel);

        // Add form panel to content panel
        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Add status bar at bottom - REMOVED fixed preferred size for better layout
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(30, 30, 30));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Removed: statusBar.setPreferredSize(new Dimension(550, 25)); // REMOVED fixed preferred size

        JLabel statusLabel = new JLabel("System Status: Online | " +
                                  java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(150, 150, 150));
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Add panels to main frame
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Add action listeners
        btnSignIn.addActionListener(this::loginAction);
        btnRegisterNow.addActionListener(e -> {
            new RegisterForm().setVisible(true); // Assuming RegisterForm exists
            dispose();
        });
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(550, 140)); // Still keeping a fixed height for the header for layout stability

        // Header Title
        JLabel titleLabel = new JLabel("BUT FIRST, COFFEE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Slightly reduced font size from 30 to 28
        titleLabel.setForeground(GOLD_ACCENT); // Gold text
        // Adjusted border to add spacing *below* the title when it's in the center
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        // panel.add(titleLabel, BorderLayout.NORTH); // REMOVED - moved to CENTER

        // Logo panel - MOVED TO NORTH
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.BLACK);

        // Try to load the logo
        URL logoUrl = getClass().getResource("/images/logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image scaledLogo = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Reduced from 50x50 to 40x40
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoPanel.add(logoLabel);
        } else {
            // Create a simple coffee icon as placeholder
            JLabel coffeeIcon = new JLabel("☕");
            coffeeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32)); // Reduced from 36 to 32
            coffeeIcon.setForeground(GOLD_ACCENT); // Gold color
            logoPanel.add(coffeeIcon);
        }

        panel.add(logoPanel, BorderLayout.NORTH); // <-- ADDED logoPanel to NORTH
        panel.add(titleLabel, BorderLayout.CENTER); // <-- ADDED titleLabel to CENTER

        return panel;
    }

private void loginAction(ActionEvent e) {
    String email = txtEmail.getText().trim();
    String password = new String(txtPassword.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter both email and password",
            "Error", JOptionPane.ERROR_MESSAGE);
        return; // Stop here if fields are empty
    }

    // Assuming UserDAO and User classes exist and are correctly implemented
    UserDAO userDAO = new UserDAO();
    User user = userDAO.authenticateUserByEmail(email, password);

    if (user != null) {
        // Authentication successful
        if (user.isAdmin()) {
            // Open Admin Dashboard
            new AdminDashboard(user).setVisible(true);
        } else {
            new UserDashboard(user).setVisible(true); // <-- UNCOMMENTED THIS LINE
        }

        this.dispose(); 
    } else {
        // Authentication failed
        JOptionPane.showMessageDialog(this,
            "Invalid email or password",
            "Error", JOptionPane.ERROR_MESSAGE);
        // Do NOT dispose() the window on failed login
    }
}


    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}