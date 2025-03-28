package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkNotRobot;
    private JButton btnSignIn;
    private JButton btnRegisterNow;
    private JLabel lblForgotPassword;

    public LoginForm() {
        setTitle("But First, Coffee - Log In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Title label
        JLabel lblTitle = new JLabel("Log in to your account");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        
        // Email field
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 12));
        txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        // Password field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        // Not a robot checkbox
        chkNotRobot = new JCheckBox("I'm not a robot");
        chkNotRobot.setFont(new Font("Arial", Font.PLAIN, 12));
        chkNotRobot.setBackground(Color.WHITE);
        
        // Forgot password link
        lblForgotPassword = new JLabel("Forgot your password?");
        lblForgotPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        lblForgotPassword.setForeground(new Color(0, 102, 204));
        lblForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Log in button
        btnSignIn = new JButton("Log In");
        btnSignIn.setBackground(new Color(0, 102, 204));
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setFocusPainted(false);
        
        // Register Now button
        btnRegisterNow = new JButton("Register Now");
        btnRegisterNow.setBackground(Color.WHITE);
        btnRegisterNow.setForeground(new Color(0, 102, 204));
        btnRegisterNow.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)));
        btnRegisterNow.setFocusPainted(false);
        
        // Button panel to hold both buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow);
        
        // Add components to form panel with spacing
        formPanel.add(lblEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(chkNotRobot);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(lblForgotPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonPanel);
        
        // Add panels to main frame
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        
        // Add action listeners
        btnSignIn.addActionListener(this::loginAction);
        btnRegisterNow.addActionListener(e -> {
            new RegisterForm().setVisible(true);
            dispose();
        });
        lblForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginForm.this,
                    "Password reset functionality will be implemented here",
                    "Forgot Password",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void loginAction(ActionEvent e) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both email and password", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!chkNotRobot.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Please verify you're not a robot",
                "Verification Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticateUserByEmail(email, password);
        
        if (user != null) {
            if (user.isAdmin()) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid email or password", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}