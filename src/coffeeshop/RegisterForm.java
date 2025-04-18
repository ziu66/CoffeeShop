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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class RegisterForm extends JFrame {
    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JTextField txtFullName;
    private final JTextField txtPhone;
    private final JTextField txtAddress;
    private final JButton btnRegister;
    private final JButton btnBackToLogin;

    public RegisterForm() {
        setTitle("But First, Coffee - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        
        // Main panel with dark theme
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30)); // Dark background
        
        // Add the header panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Create central content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(30, 30, 30));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 70, 40, 70));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(40, 50, 50, 50)
        ));
        
        // Form Title
        JLabel lblTitle = new JLabel("Create your account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(218, 165, 32)); // Gold text
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Email field
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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
        
        // Password field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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
        
        // Confirm Password field
        JLabel lblConfirmPassword = new JLabel("Confirm Password");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblConfirmPassword.setForeground(Color.WHITE);
        lblConfirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmPassword.setBackground(new Color(60, 60, 60));
        txtConfirmPassword.setForeground(Color.WHITE);
        txtConfirmPassword.setCaretColor(Color.WHITE);
        txtConfirmPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtConfirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Full Name field
        JLabel lblFullName = new JLabel("Full Name");
        lblFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFullName.setForeground(Color.WHITE);
        lblFullName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtFullName = new JTextField();
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFullName.setBackground(new Color(60, 60, 60));
        txtFullName.setForeground(Color.WHITE);
        txtFullName.setCaretColor(Color.WHITE);
        txtFullName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtFullName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtFullName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Phone field
        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPhone.setForeground(Color.WHITE);
        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtPhone = new JTextField();
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPhone.setBackground(new Color(60, 60, 60));
        txtPhone.setForeground(Color.WHITE);
        txtPhone.setCaretColor(Color.WHITE);
        txtPhone.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Address field
        JLabel lblAddress = new JLabel("Address");
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAddress.setForeground(Color.WHITE);
        lblAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtAddress = new JTextField();
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAddress.setBackground(new Color(60, 60, 60));
        txtAddress.setForeground(Color.WHITE);
        txtAddress.setCaretColor(Color.WHITE);
        txtAddress.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Register button
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setBackground(Color.WHITE);
        btnRegister.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 165, 32), 1), // Gold border
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Back to Login button
        btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBackToLogin.setForeground(new Color(218, 165, 32)); // Gold text
        btnBackToLogin.setBackground(new Color(40, 40, 40));
        btnBackToLogin.setBorder(null);
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBackToLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBackToLogin.setContentAreaFilled(false);
        
        // Add components to form panel with spacing
        formPanel.add(lblTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(lblEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblConfirmPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtConfirmPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblFullName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtFullName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblPhone);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtPhone);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblAddress);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtAddress);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(btnRegister);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(btnBackToLogin);
        
        // Create scroll pane for the form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(40, 40, 40));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add form panel to content panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add status bar at bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(30, 30, 30));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusBar.setPreferredSize(new Dimension(550, 25));

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
        btnRegister.addActionListener(this::registerAction);
        btnBackToLogin.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(550, 200)); // Increased width and height

        // Header Title
        JLabel titleLabel = new JLabel("BUT FIRST, COFFEE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30)); // Increased font size
        titleLabel.setForeground(new Color(218, 165, 32)); // Gold text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Logo panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.BLACK);
        
        // Try to load the logo
        URL logoUrl = getClass().getResource("/images/logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoPanel.add(logoLabel);
        } else {
            // Create a simple coffee icon as placeholder
            JLabel coffeeIcon = new JLabel("☕");
            coffeeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
            coffeeIcon.setForeground(new Color(218, 165, 32)); // Gold color
            logoPanel.add(coffeeIcon);
        }
        
        panel.add(logoPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void registerAction(ActionEvent e) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty() || 
            fullName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.isEmailTaken(email)) {
            JOptionPane.showMessageDialog(this,
                "Email already registered",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new user
        User newUser = new User();
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setAdmin(false);

        if (userDAO.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this,
                "Registration successful! Please login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginForm().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Registration failed. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Use the system's native look and feel for the title bar
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new RegisterForm().setVisible(true);
        });
    }
}