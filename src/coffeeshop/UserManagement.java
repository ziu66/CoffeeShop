/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagement extends JFrame {
    private JPanel mainPanel;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnBack;
    private JTextField searchField;
    private JButton btnSearch;
    private JComboBox<String> filterComboBox;
    
    private User currentAdmin;
    private List<User> userList;

    public UserManagement(User admin) {
        this.currentAdmin = admin;
        this.userList = new ArrayList<>();
        
        // Set up the frame
        setTitle("But First, Coffee - User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // <-- ADD THIS LINE
        setUndecorated(false); 
        
        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create content
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set the content pane
        setContentPane(mainPanel);
        
        // Load users
        loadUsers();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(218, 165, 32)); // Gold color
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(30, 30, 30));
        
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        
        filterComboBox = new JComboBox<>(new String[]{"All Users", "Admins Only", "Regular Users"});
        filterComboBox.setPreferredSize(new Dimension(120, 30));
        filterComboBox.setBackground(new Color(60, 60, 60));
        filterComboBox.setForeground(Color.WHITE);
        
        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(60, 60, 60));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> searchUsers());
        
        searchPanel.add(new JLabel("Filter: "));
        searchPanel.add(filterComboBox);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 40));

        // Create table model with columns
        String[] columns = {"ID", "Email", "Full Name", "Phone", "Admin Status", "Creation Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        // Create table
        userTable = new JTable(tableModel);
        userTable.setBackground(new Color(50, 50, 50));
        userTable.setForeground(Color.WHITE);
        userTable.setGridColor(new Color(70, 70, 70));
        userTable.setSelectionBackground(new Color(100, 100, 100));
        userTable.setSelectionForeground(Color.WHITE);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        userTable.setRowHeight(25);
        userTable.setShowVerticalLines(true);

        // Set column widths - FIXED to match our 6 columns
        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID
        columnModel.getColumn(1).setPreferredWidth(150);  // Email
        columnModel.getColumn(2).setPreferredWidth(150);  // Full Name
        columnModel.getColumn(3).setPreferredWidth(100);  // Phone
        columnModel.getColumn(4).setPreferredWidth(100);  // Admin Status
        columnModel.getColumn(5).setPreferredWidth(150);  // Creation Date

        // Style the header
        JTableHeader header = userTable.getTableHeader();
        header.setBackground(new Color(30, 30, 30));
        header.setForeground(new Color(218, 165, 32));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(50, 50, 50));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        btnAdd = createStyledButton("Add User", new Color(70, 130, 70));
        btnEdit = createStyledButton("Edit User", new Color(70, 70, 130));
        btnDelete = createStyledButton("Delete User", new Color(130, 70, 70));
        btnBack = createStyledButton("Back to Dashboard", new Color(218, 165, 32));
        
        // Add action listeners
        btnAdd.addActionListener(e -> showAddUserDialog());
        btnEdit.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                editUser(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a user to edit.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnDelete.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                deleteUser(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a user to delete.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnBack.addActionListener(e -> {
            dispose();
        });
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnBack);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(bgColor.getRed() + 20, bgColor.getGreen() + 20, bgColor.getBlue() + 20), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                    Math.min(bgColor.getRed() + 20, 255),
                    Math.min(bgColor.getGreen() + 20, 255),
                    Math.min(bgColor.getBlue() + 20, 255)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadUsers() {
        // Clear existing data
        tableModel.setRowCount(0);
        userList.clear();

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM users ORDER BY user_id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                boolean isAdmin = rs.getBoolean("is_admin");
                String createdAt = rs.getTimestamp("created_at").toString();

                // Create user object - remove username, use email as identifier
                User user = new User(userId, email, password, email, fullName, phone, address, isAdmin);
                userList.add(user);

                // Update the table row data - use email instead of username
                Object[] row = {
                userId,
                email,
                fullName,
                phone,
                isAdmin ? "Admin" : "Regular User",
                createdAt
            };
            tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchUsers() {
        String searchText = searchField.getText().toLowerCase().trim();
        String filter = (String) filterComboBox.getSelectedItem();

        tableModel.setRowCount(0);

        for (User user : userList) {
            // Apply filter
            if (filter.equals("Admins Only") && !user.isAdmin()) {
                continue;
            } else if (filter.equals("Regular Users") && user.isAdmin()) {
                continue;
            }

            // Apply search - use email instead of username
            if (searchText.isEmpty() || 
                user.getEmail().toLowerCase().contains(searchText) || 
                user.getFullName().toLowerCase().contains(searchText)) {

                Object[] row = {
                    user.getUserId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    user.isAdmin() ? "Admin" : "Regular User",
                    "N/A" // Placeholder for created_at
                };
                tableModel.addRow(row);
            }
        }
    }

    
    private void showAddUserDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(40, 40, 40));

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form fields - remove username field and update labels
        JTextField emailField = createTextField();
        JPasswordField passwordField = createPasswordField();
        JTextField fullNameField = createTextField();
        JTextField phoneField = createTextField();
        JTextArea addressArea = createTextArea();
        JCheckBox adminCheckBox = new JCheckBox("Admin User");
        adminCheckBox.setForeground(Color.WHITE);
        adminCheckBox.setBackground(new Color(40, 40, 40));

        // Add form elements - remove username, use email as identifier
        formPanel.add(createFieldPanel("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Full Name:", fullNameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Phone:", phoneField));
        formPanel.add(Box.createVerticalStrut(10));
        
        // Address panel with scroll
        JPanel addressPanel = new JPanel(new BorderLayout());
        addressPanel.setBackground(new Color(40, 40, 40));
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(Color.WHITE);
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 80));
        addressPanel.add(addressScroll, BorderLayout.CENTER);
        
        formPanel.add(addressPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(adminCheckBox);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        JButton saveButton = createStyledButton("Save", new Color(70, 130, 70));
        JButton cancelButton = createStyledButton("Cancel", new Color(130, 70, 70));
        
        saveButton.addActionListener(e -> {
            // Validate input - remove username validation
            if (emailField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0 ||
                fullNameField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, 
                    "Please fill all required fields.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Create new user - use email for both email and username fields
                User newUser = new User();
                newUser.setUsername(emailField.getText().trim()); // Set email as username
                newUser.setPassword(new String(passwordField.getPassword()));
                newUser.setEmail(emailField.getText().trim());
                newUser.setFullName(fullNameField.getText().trim());
                newUser.setPhone(phoneField.getText().trim());
                newUser.setAddress(addressArea.getText().trim());
                newUser.setAdmin(adminCheckBox.isSelected());

                // Save to database
                saveUserToDatabase(newUser);

                // Refresh table
                loadUsers();

                // Close dialog
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error saving user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    private void editUser(int selectedRow) {
        int userId = (int) userTable.getValueAt(selectedRow, 0);
        User selectedUser = null;
        
        // Find the user in our list
        for (User user : userList) {
            if (user.getUserId() == userId) {
                selectedUser = user;
                break;
            }
        }
        
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, 
                "User not found.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Prevent admin from editing themselves
        if (selectedUser.getUserId() == currentAdmin.getUserId()) {
            JOptionPane.showMessageDialog(this, 
                "You cannot edit your own account here.\nUse the profile settings instead.", 
                "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create dialog
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(40, 40, 40));
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields - pre-populate with user data
        JTextField usernameField = createTextField();
        usernameField.setText(selectedUser.getUsername());
        
        JPasswordField passwordField = createPasswordField();
        passwordField.setText(selectedUser.getPassword());
        
        JTextField emailField = createTextField();
        emailField.setText(selectedUser.getEmail());
        
        JTextField fullNameField = createTextField();
        fullNameField.setText(selectedUser.getFullName());
        
        JTextField phoneField = createTextField();
        phoneField.setText(selectedUser.getPhone());
        
        JTextArea addressArea = createTextArea();
        addressArea.setText(selectedUser.getAddress());
        
        JCheckBox adminCheckBox = new JCheckBox("Admin User");
        adminCheckBox.setForeground(Color.WHITE);
        adminCheckBox.setBackground(new Color(40, 40, 40));
        adminCheckBox.setSelected(selectedUser.isAdmin());
        
        // Add form elements
        formPanel.add(createFieldPanel("Username:", usernameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Full Name:", fullNameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Phone:", phoneField));
        formPanel.add(Box.createVerticalStrut(10));
        
        // Address panel with scroll
        JPanel addressPanel = new JPanel(new BorderLayout());
        addressPanel.setBackground(new Color(40, 40, 40));
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(Color.WHITE);
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 80));
        addressPanel.add(addressScroll, BorderLayout.CENTER);
        
        formPanel.add(addressPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(adminCheckBox);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        User finalSelectedUser = selectedUser; // For lambda
        
        JButton saveButton = createStyledButton("Update", new Color(70, 130, 70));
        JButton cancelButton = createStyledButton("Cancel", new Color(130, 70, 70));
        
        saveButton.addActionListener(e -> {
            // Validate input
            if (usernameField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0 ||
                emailField.getText().trim().isEmpty() ||
                fullNameField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(dialog, 
                    "Please fill all required fields.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // Update user
                finalSelectedUser.setUsername(usernameField.getText().trim());
                finalSelectedUser.setPassword(new String(passwordField.getPassword()));
                finalSelectedUser.setEmail(emailField.getText().trim());
                finalSelectedUser.setFullName(fullNameField.getText().trim());
                finalSelectedUser.setPhone(phoneField.getText().trim());
                finalSelectedUser.setAddress(addressArea.getText().trim());
                finalSelectedUser.setAdmin(adminCheckBox.isSelected());
                
                // Update in database
                updateUserInDatabase(finalSelectedUser);
                
                // Refresh table
                loadUsers();
                
                // Close dialog
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    private void deleteUser(int selectedRow) {
        int userId = (int) userTable.getValueAt(selectedRow, 0);
        String username = (String) userTable.getValueAt(selectedRow, 1);
        
        // Prevent admin from deleting themselves
        if (userId == currentAdmin.getUserId()) {
            JOptionPane.showMessageDialog(this, 
                "You cannot delete your own account!", 
                "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + username + "?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                // Delete from database
                deleteUserFromDatabase(userId);
                
                // Refresh table
                loadUsers();
                
                JOptionPane.showMessageDialog(this,
                    "User deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void saveUserToDatabase(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            // Update SQL query to remove username column
            String query = "INSERT INTO users (email, password, full_name, phone, address, is_admin) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(query);
            // Update parameter indices
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setBoolean(6, user.isAdmin());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    private void updateUserInDatabase(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            // Update SQL query to remove username column
            String query = "UPDATE users SET email = ?, password = ?, " +
                           "full_name = ?, phone = ?, address = ?, is_admin = ? " +
                           "WHERE user_id = ?";

            stmt = conn.prepareStatement(query);
            // Update parameter indices
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setBoolean(6, user.isAdmin());
            stmt.setInt(7, user.getUserId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    private void deleteUserFromDatabase(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Begin transaction
            conn.setAutoCommit(false);
            
            // First delete all related records (orders, etc.) if necessary
            // This depends on your database structure and foreign key constraints
            
            // Then delete the user
            String query = "DELETE FROM users WHERE user_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
            
            // Commit transaction
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(40, 40, 40));
        
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(100, 25));
        
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }
    
    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setBackground(new Color(60, 60, 60));
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return area;
    }
}