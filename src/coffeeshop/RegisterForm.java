/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterForm extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JButton btnRegister;
    private JButton btnBackToLogin;

    public RegisterForm() {
        setTitle("But First, Coffee - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title label
        JLabel lblTitle = new JLabel("Create your account");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form panel with scroll
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // Form fields (removed username)
        txtEmail = createFormField("Email Address", formPanel);
        txtPassword = createPasswordField("Password", formPanel);
        txtConfirmPassword = createPasswordField("Confirm Password", formPanel);
        txtFullName = createFormField("Full Name", formPanel);
        txtPhone = createFormField("Phone Number", formPanel);
        txtAddress = createFormField("Address", formPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setBackground(Color.WHITE);
        btnBackToLogin.setForeground(new Color(0, 102, 204));
        btnBackToLogin.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)));
        btnBackToLogin.setFocusPainted(false);

        btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(0, 102, 204));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);

        buttonPanel.add(btnBackToLogin);
        buttonPanel.add(btnRegister);

        // Add components to form panel
        formPanel.add(buttonPanel);

        // Add panels to main frame
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        // Wrap formPanel in JScrollPane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Action listeners
        btnRegister.addActionListener(this::registerAction);
        btnBackToLogin.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
    }

    private JTextField createFormField(String labelText, JPanel parentPanel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        parentPanel.add(label);
        parentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        parentPanel.add(textField);
        parentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        return textField;
    }

    private JPasswordField createPasswordField(String labelText, JPanel parentPanel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        parentPanel.add(label);
        parentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        parentPanel.add(passwordField);
        parentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        return passwordField;
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
        
        // Generate username from email (before the @)
        String username = email.split("@")[0];
        newUser.setUsername(username);
        
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
}