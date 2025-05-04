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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints; // Added
import java.awt.GridBagLayout;   // Added
import java.awt.Image;
import java.awt.Insets;           // Added
import java.awt.RenderingHints; // Added
import java.awt.Rectangle;      // Added
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage; // Added
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;      // Added
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;        // Added
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListCellRenderer; // Added
import javax.swing.plaf.basic.BasicComboBoxUI; // Added


    public class RegisterForm extends JFrame {
        private final JTextField txtEmail;
        private final JPasswordField txtPassword;
        private final JPasswordField txtConfirmPassword;
        private final JTextField txtFullName;
        private final JTextField txtPhone;
        // Removed private final JTextField txtAddress;
        private final JTextField txtStreetBlock; // Added
        private final JComboBox<String> cmbBarangay; // Added

        private final JButton btnRegister;
        private final JButton btnBackToLogin;

        // List of barangays in Nasugbu, Batangas (Replicated for simplicity)
        private static final String[] NASUGBU_BARANGAYS = {
            "Aga", "Balaytigue", "Bilaran", "Bucana", "Bulihan", "Bungahan", "Calayo",
            "Catandaan", "Cogunan", "Daykitin", "Looc", "Lumbangan", "Malapad Na Bato",
            "Natipuan", "Pantalan", "Papaya", "Kayrilaw", "Kaylaway", "Latag",
            "Maugat", "Palo", "Putat", "Reparo", "Tabilao", "Tumalim", "Wawa" // Added Wawa, adjusted order
        };

        // Define colors similar to CartManager for consistency
        private final Color DARKER_BG = new Color(40, 40, 40);
        private final Color INPUT_BG = new Color(60, 60, 60);
        private final Color TEXT_COLOR = Color.WHITE;
        private final Color CARET_COLOR = Color.WHITE;
        private final Color BORDER_COLOR = new Color(80, 80, 80);
        private final Color ACCENT_COLOR_GOLD = new Color(218, 165, 32);


        public RegisterForm() {
        setTitle("But First, Coffee - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Make the frame full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // <-- ADDED THIS LINE
        setUndecorated(false); // Keep standard window decorations (title bar, borders) <-- ADDED THIS LINE
        // setSize(800, 900); // <-- REMOVED THIS LINE
        // setLocationRelativeTo(null); // <-- REMOVED THIS LINE


        // Main panel with dark theme
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30)); // Dark background

        // Add the header panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create central content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(30, 30, 30));
        // Adjusted horizontal padding slightly as fields will have a max width
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));

        // Form panel (Scrollable content area)
        JPanel formContentArea = new JPanel(); // Use a separate panel for content to be scrolled
        formContentArea.setLayout(new BoxLayout(formContentArea, BoxLayout.Y_AXIS));
        formContentArea.setBackground(DARKER_BG); // Match background
        formContentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(40, 50, 50, 50) // Inner padding inside the border
        ));


        // Form Title
        JLabel lblTitle = new JLabel("Create your account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ACCENT_COLOR_GOLD); // Gold text
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title
        formContentArea.add(lblTitle);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- Input Field Components ---
        // Define a preferred/maximum width for input fields and address panel
        int fieldWidth = 400; // Adjust as needed
        int fieldHeight = 40;
        int addressPanelWidth = 450; // Slightly wider to accommodate labels and fields

        // Email field
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setForeground(TEXT_COLOR);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        formContentArea.add(lblEmail);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBackground(INPUT_BG);
        txtEmail.setForeground(TEXT_COLOR);
        txtEmail.setCaretColor(CARET_COLOR);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtEmail.setMaximumSize(new Dimension(fieldWidth, fieldHeight)); // Set max size
        txtEmail.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the field
        formContentArea.add(txtEmail);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setForeground(TEXT_COLOR);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        formContentArea.add(lblPassword);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBackground(INPUT_BG);
        txtPassword.setForeground(TEXT_COLOR);
        txtPassword.setCaretColor(CARET_COLOR);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtPassword.setMaximumSize(new Dimension(fieldWidth, fieldHeight)); // Set max size
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the field
        formContentArea.add(txtPassword);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirm Password field
        JLabel lblConfirmPassword = new JLabel("Confirm Password");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblConfirmPassword.setForeground(TEXT_COLOR);
        lblConfirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        formContentArea.add(lblConfirmPassword);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmPassword.setBackground(INPUT_BG);
        txtConfirmPassword.setForeground(TEXT_COLOR);
        txtConfirmPassword.setCaretColor(CARET_COLOR);
        txtConfirmPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtConfirmPassword.setMaximumSize(new Dimension(fieldWidth, fieldHeight)); // Set max size
        txtConfirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the field
        formContentArea.add(txtConfirmPassword);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // Full Name field
        JLabel lblFullName = new JLabel("Full Name");
        lblFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFullName.setForeground(TEXT_COLOR);
        lblFullName.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        formContentArea.add(lblFullName);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        txtFullName = new JTextField();
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFullName.setBackground(INPUT_BG);
        txtFullName.setForeground(TEXT_COLOR);
        txtFullName.setCaretColor(CARET_COLOR);
        txtFullName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtFullName.setMaximumSize(new Dimension(fieldWidth, fieldHeight)); // Set max size
        txtFullName.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the field
        formContentArea.add(txtFullName);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // Phone field
        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPhone.setForeground(TEXT_COLOR);
        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        formContentArea.add(lblPhone);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        txtPhone = new JTextField();
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPhone.setBackground(INPUT_BG);
        txtPhone.setForeground(TEXT_COLOR);
        txtPhone.setCaretColor(CARET_COLOR);
        txtPhone.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtPhone.setMaximumSize(new Dimension(fieldWidth, fieldHeight)); // Set max size
        txtPhone.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the field
        formContentArea.add(txtPhone);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Start of New Address Fields ---
        JLabel lblAddress = new JLabel("Primary Address (Nasugbu, Batangas)"); // Updated label text
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAddress.setForeground(TEXT_COLOR);
        lblAddress.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        formContentArea.add(lblAddress);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        // Panel to hold Street/Block and Barangay using GridBagLayout
        JPanel addressInputPanel = new JPanel(new GridBagLayout());
        addressInputPanel.setBackground(DARKER_BG); // Match parent background
        addressInputPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the panel itself
        addressInputPanel.setMaximumSize(new Dimension(addressPanelWidth, 80)); // Set max size for the panel


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 10); // Top, left, bottom, right padding within this panel
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0; // Labels don't stretch

        // Street/Block Row
        JLabel lblStreetBlock = new JLabel("Street/Block:");
        lblStreetBlock.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Smaller label font
        lblStreetBlock.setForeground(new Color(180, 180, 180)); // Lighter color
        gbc.gridx = 0;
        gbc.gridy = 0;
        addressInputPanel.add(lblStreetBlock, gbc);

        txtStreetBlock = new JTextField(); // Let GridBagLayout manage size within the panel
        txtStreetBlock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStreetBlock.setBackground(INPUT_BG);
        txtStreetBlock.setForeground(TEXT_COLOR);
        txtStreetBlock.setCaretColor(CARET_COLOR);
        txtStreetBlock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Field takes remaining horizontal space
        addressInputPanel.add(txtStreetBlock, gbc);

        // Barangay Row
        JLabel lblBarangay = new JLabel("Barangay:");
        lblBarangay.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Smaller label font
        lblBarangay.setForeground(new Color(180, 180, 180)); // Lighter color
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        addressInputPanel.add(lblBarangay, gbc);

        cmbBarangay = new JComboBox<>(NASUGBU_BARANGAYS);
        cmbBarangay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbBarangay.setBackground(INPUT_BG);
        cmbBarangay.setForeground(TEXT_COLOR);
         // Adjusted border and padding to make it look like a text field
        cmbBarangay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Dropdown takes remaining horizontal space
        addressInputPanel.add(cmbBarangay, gbc);

        // Add the address input panel to the main form content area
        formContentArea.add(addressInputPanel);
        // --- End of New Address Fields ---


        formContentArea.add(Box.createRigidArea(new Dimension(0, 25))); // Space before button

        // Register button
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setBackground(Color.WHITE); // Initial background - maybe accent?
        btnRegister.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR_GOLD, 1), // Gold border
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        btnRegister.setMaximumSize(new Dimension(fieldWidth, 45)); // Set max size to match fields


         // Add hover effect to Register button
         btnRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegister.setBackground(ACCENT_COLOR_GOLD); // Gold background on hover
                btnRegister.setForeground(Color.WHITE); // White text on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                 btnRegister.setBackground(Color.WHITE); // Back to initial white
                 btnRegister.setForeground(Color.BLACK); // Back to initial black
            }
        });


        formContentArea.add(btnRegister);
        formContentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // Back to Login button
        btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBackToLogin.setForeground(ACCENT_COLOR_GOLD); // Gold text
        btnBackToLogin.setBackground(DARKER_BG); // Match panel background
        btnBackToLogin.setBorder(null); // No border
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBackToLogin.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        btnBackToLogin.setContentAreaFilled(false); // Transparent background

        // Add hover effect to Back button
        btnBackToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                 btnBackToLogin.setForeground(new Color(255, 215, 0)); // Brighter gold on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                 btnBackToLogin.setForeground(ACCENT_COLOR_GOLD); // Back to normal gold
            }
        });


        formContentArea.add(btnBackToLogin);

        // Add vertical glue to push content up if window is taller than required
        formContentArea.add(Box.createVerticalGlue());

        // Create scroll pane for the form content
        JScrollPane scrollPane = new JScrollPane(formContentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(DARKER_BG); // Match background
        scrollPane.getViewport().setBackground(DARKER_BG); // Match viewport background
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add form panel to content panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add status bar at bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(30, 30, 30));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusBar.setPreferredSize(new Dimension(550, 25)); // Keep preferred height

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

        // Apply custom renderer and UI to the barangay combo box
         cmbBarangay.setRenderer(new DefaultListCellRenderer() {
             @Override
             public Component getListCellRendererComponent(JList<?> list, Object value,
                     int index, boolean isSelected, boolean cellHasFocus) {
                 super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 setBackground(isSelected ? new Color(80, 80, 80) : INPUT_BG); // Slightly different selection color
                 setForeground(TEXT_COLOR);
                 return this;
             }
         });
          // Style the editor component within the combobox
          JTextField dropdownEditor = (JTextField) cmbBarangay.getEditor().getEditorComponent();
          dropdownEditor.setBackground(INPUT_BG);
          dropdownEditor.setForeground(TEXT_COLOR);
          dropdownEditor.setCaretColor(CARET_COLOR);
          dropdownEditor.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8)); // Match padding with text field


         // Apply custom UI for styling the button and background
         cmbBarangay.setUI(new BasicComboBoxUI() {
             @Override
             protected JButton createArrowButton() {
                 JButton button = new JButton();
                 button.setIcon(new ImageIcon(createColoredArrowIcon())); // Use the new method
                 button.setBackground(INPUT_BG); // Match background
                 button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR)); // Match border
                 button.setContentAreaFilled(false);
                 button.setFocusPainted(false);
                 return button;
             }
             // Override paintCurrentValueBackground to draw background correctly
             @Override
             public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                 g.setColor(INPUT_BG);
                 g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
             }
         });
    }

     // Helper method to create the colored arrow icon for the combobox
    private Image createColoredArrowIcon() {
        BufferedImage image = new BufferedImage(10, 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(TEXT_COLOR); // Use white color for the arrow
        g2.fillPolygon(new int[] {0, 5, 10}, new int[] {0, 5, 0}, 3);
        g2.dispose();
        return image;
    }


    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        // panel.setPreferredSize(new Dimension(550, 200)); // <-- REMOVED FIXED SIZE - Height is now dynamic

        // Logo panel (NORTH)
        // Changed FlowLayout vertical gap to match LoginForm's implicit gap better
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Adjusted vertical gap
        logoPanel.setBackground(Color.BLACK);
        // Added top padding to the logo panel itself, reduced bottom padding slightly
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 5, 0)); // Add 30px top, 5px bottom padding

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
            coffeeIcon.setForeground(ACCENT_COLOR_GOLD); // Gold color
            logoPanel.add(coffeeIcon);
        }

        // Header Title (CENTER)
        JLabel titleLabel = new JLabel("BUT FIRST, COFFEE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30)); // Increased font size
        titleLabel.setForeground(ACCENT_COLOR_GOLD); // Gold text
        // Adjusted border - removed top padding, keep bottom padding - Reduced bottom padding
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Adjusted bottom padding


        panel.add(logoPanel, BorderLayout.NORTH); // Logo is NORTH
        panel.add(titleLabel, BorderLayout.CENTER); // Title is CENTER

        return panel;
    }

    private void registerAction(ActionEvent e) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        // --- Modified Address Input ---
        String streetBlock = txtStreetBlock.getText().trim();
        String selectedBarangay = (String) cmbBarangay.getSelectedItem();
        String fullAddress = "";

        // Basic validation for address components
        if (streetBlock.isEmpty() || selectedBarangay == null || selectedBarangay.isEmpty()) {
             JOptionPane.showMessageDialog(this,
                 "Please enter street/block and select a barangay.",
                 "Error", JOptionPane.ERROR_MESSAGE);
             return; // Stop the registration process
        } else {
            // Construct the full address string
            fullAddress = streetBlock + ", " + selectedBarangay + ", Nasugbu, Batangas";
        }
        // --- End Modified Address Input ---


        // Validation for other fields
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

        // Email validation (basic format check)
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
             JOptionPane.showMessageDialog(this,
                 "Please enter a valid email address.",
                 "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

         // Phone number validation (basic check for digits and reasonable length)
         if (!phone.matches("\\d{7,15}")) { // Allows 7 to 15 digits
              JOptionPane.showMessageDialog(this,
                  "Please enter a valid phone number (digits only, 7-15 length).",
                  "Error", JOptionPane.ERROR_MESSAGE);
              return;
         }


        // Assuming UserDAO and User classes exist and are correctly implemented
        UserDAO userDAO = new UserDAO();
        if (userDAO.isEmailTaken(email)) {
            JOptionPane.showMessageDialog(this,
                "Email already registered",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new user
        User newUser = new User();
        newUser.setPassword(password); // Password hashing should ideally happen in DAO
        newUser.setEmail(email); // Using email as the username for login consistency
        newUser.setUsername(email); // IMPORTANT: Set username to email for Login form compatibility
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setAddress(fullAddress); // Set the constructed full address
        newUser.setAdmin(false); // Users registering are not admins

        if (userDAO.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this,
                "Registration successful! Please login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            // Redirect to login form
            new LoginForm().setVisible(true);
            dispose(); // Close register form
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