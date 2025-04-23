/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.DecimalFormat;

public class ProductManagement extends JFrame {
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnBack;
    private JComboBox<String> filterCategory;
    private JTextField searchField;
    private User currentUser;
    
    // Store UI colors as constants for consistency
    private final Color DARK_BG = new Color(40, 40, 40);
    private final Color DARKER_BG = new Color(30, 30, 30);
    private final Color INPUT_BG = new Color(60, 60, 60);
    private final Color TABLE_BG = new Color(50, 50, 50);
    private final Color GOLD_ACCENT = new Color(218, 165, 32);
    private final Color BORDER_COLOR = new Color(70, 70, 70);
    private final Color BTN_SUCCESS = new Color(0, 128, 0);
    private final Color BTN_INFO = new Color(0, 102, 204);
    private final Color BTN_DANGER = new Color(204, 0, 0);
    private final Color BTN_DEFAULT = new Color(70, 70, 70);
    private final Color SELECTION_BG = new Color(70, 130, 180);
    
    public ProductManagement(User user) {
        this.currentUser = user;
        
        // Set up the frame
        setTitle("But First, Coffee - Product Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(DARK_BG);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARKER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(GOLD_ACCENT);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filter and Search Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(DARKER_BG);
        
        JLabel filterLabel = new JLabel("Category:");
        filterLabel.setForeground(Color.WHITE);
        filterPanel.add(filterLabel);
        
        filterCategory = new JComboBox<>(new String[]{"All", "DRINK", "MEAL", "MERCHANDISE"});
        styleComboBox(filterCategory);
        filterCategory.addActionListener(e -> loadProductData());
        filterPanel.add(filterCategory);
        
        filterPanel.add(Box.createHorizontalStrut(15));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        filterPanel.add(searchLabel);
        
        searchField = new JTextField(15);
        searchField.setBackground(INPUT_BG);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.addActionListener(e -> loadProductData());
        filterPanel.add(searchField);
        
        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, GOLD_ACCENT, Color.BLACK);
        btnSearch.addActionListener(e -> loadProductData());
        filterPanel.add(btnSearch);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(DARK_BG);
        
        // Table columns
        String[] columns = {"ID", "Name", "Category", "Price", "Available", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class; // For checkbox in "Available" column
                return String.class;
            }
        };
        
        productsTable = new JTable(tableModel);
        productsTable.setBackground(TABLE_BG);
        productsTable.setForeground(Color.WHITE);
        productsTable.setGridColor(BORDER_COLOR);
        productsTable.setRowHeight(25);
        
        // Create a custom header renderer to fix the header colors
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(DARKER_BG);
                comp.setForeground(Color.WHITE);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return comp;
            }
        };
        
        // Apply the custom renderer to all header columns
        JTableHeader header = productsTable.getTableHeader();
        header.setDefaultRenderer(headerRenderer);
        header.setBackground(DARKER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths
        productsTable.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(200);   // Name
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(100);   // Category
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(80);    // Price
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(80);    // Available
        productsTable.getColumnModel().getColumn(5).setPreferredWidth(300);   // Description
        
        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(TABLE_BG);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(DARK_BG);
        
        btnBack = new JButton("Back to Dashboard");
        styleButton(btnBack, BTN_DEFAULT, Color.WHITE);
        btnBack.addActionListener(e -> dispose());
        
        btnAdd = new JButton("Add New Product");
        styleButton(btnAdd, BTN_SUCCESS, Color.WHITE);
        btnAdd.addActionListener(e -> addNewProduct());
        
        btnEdit = new JButton("Edit Product");
        styleButton(btnEdit, BTN_INFO, Color.WHITE);
        btnEdit.addActionListener(e -> editProduct());
        
        btnDelete = new JButton("Delete Product");
        styleButton(btnDelete, BTN_DANGER, Color.WHITE);
        btnDelete.addActionListener(e -> deleteProduct());
        
        buttonPanel.add(btnBack);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load initial data
        loadProductData();
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        // Set basic properties
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Fix for Swing's default button UI - ensure these properties are set
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setOpaque(true);

        // Make the button compatible with all Look and Feel settings
        button.putClientProperty("JButton.buttonType", "square");

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            // Force repaint on press for consistent colors
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(bgColor.darker());
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(bgColor);
                button.repaint();
            }
        });
    }
    
    private void loadProductData() {
        tableModel.setRowCount(0); // Clear existing data
        
        String category = (String) filterCategory.getSelectedItem();
        String search = searchField.getText().trim();
        
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM products WHERE 1=1");
            
            // Add category filter if not "All"
            if (!"All".equals(category)) {
                query.append(" AND category = ?");
            }
            
            // Add search filter if not empty
            if (!search.isEmpty()) {
                query.append(" AND (name LIKE ? OR description LIKE ?)");
            }
            
            query.append(" ORDER BY product_id DESC");
            
            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int paramIndex = 1;
                
                if (!"All".equals(category)) {
                    stmt.setString(paramIndex++, category);
                }
                
                if (!search.isEmpty()) {
                    String searchPattern = "%" + search + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        DecimalFormat df = new DecimalFormat("#,##0.00");
                        String formattedPrice = "₱" + df.format(rs.getDouble("price"));
                        
                        Object[] row = {
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            formattedPrice,
                            rs.getBoolean("is_available"),
                            rs.getString("description")
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading product data: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void styleComboBox(JComboBox<?> comboBox) {
        // Apply consistent styling to the combo box
        comboBox.setBackground(INPUT_BG);
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        // Set UI manager properties for consistency
        UIManager.put("ComboBox.background", INPUT_BG);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", SELECTION_BG);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        
        // Custom renderer for list items
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

                if (isSelected) {
                    c.setBackground(SELECTION_BG);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(INPUT_BG);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        
        // Force update UI
        SwingUtilities.updateComponentTreeUI(comboBox);
    }
    
    private void setupDialogUI() {
        // Set up UI manager for consistent dialog appearance
        UIManager.put("OptionPane.background", TABLE_BG);
        UIManager.put("Panel.background", TABLE_BG);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("TextField.background", INPUT_BG);
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextArea.background", INPUT_BG);
        UIManager.put("TextArea.foreground", Color.WHITE);
        UIManager.put("ComboBox.background", INPUT_BG);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("Button.background", BTN_DEFAULT);
        UIManager.put("Button.foreground", Color.WHITE);
    }
    
    private void addNewProduct() {
        setupDialogUI();

        JDialog dialog = new JDialog(this, "Add New Product", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(TABLE_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setForeground(Color.WHITE);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        nameField.setBackground(INPUT_BG);
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        formPanel.add(nameField, gbc);
        
        // Category dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(Color.WHITE);
        formPanel.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"DRINK", "MEAL", "MERCHANDISE"});
        styleComboBox(categoryBox);
        formPanel.add(categoryBox, gbc);
        
        // Price field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(Color.WHITE);
        formPanel.add(priceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField priceField = new JTextField(20);
        priceField.setBackground(INPUT_BG);
        priceField.setForeground(Color.WHITE);
        priceField.setCaretColor(Color.WHITE);
        formPanel.add(priceField, gbc);
        
        // Image URL field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel imageLabel = new JLabel("Image URL:");
        imageLabel.setForeground(Color.WHITE);
        formPanel.add(imageLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField imageField = new JTextField(20);
        imageField.setBackground(INPUT_BG);
        imageField.setForeground(Color.WHITE);
        imageField.setCaretColor(Color.WHITE);
        formPanel.add(imageField, gbc);
        
        // Availability checkbox
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel availableLabel = new JLabel("Available:");
        availableLabel.setForeground(Color.WHITE);
        formPanel.add(availableLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JCheckBox availableBox = new JCheckBox();
        availableBox.setSelected(true);
        availableBox.setBackground(TABLE_BG);
        availableBox.setForeground(Color.WHITE);
        formPanel.add(availableBox, gbc);
        
        // Description text area
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(Color.WHITE);
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setBackground(INPUT_BG);
        descArea.setForeground(Color.WHITE);
        descArea.setCaretColor(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        formPanel.add(descScroll, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(DARK_BG);
        
        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn, BTN_DEFAULT, Color.WHITE);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = new JButton("Save Product");
        styleButton(saveBtn, BTN_SUCCESS, Color.WHITE);
        saveBtn.addActionListener(e -> {
            // Validate inputs
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Product name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Save to database
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO products (name, description, price, category, image_url, is_available) VALUES (?, ?, ?, ?, ?, ?)"
                 )) {
                
                stmt.setString(1, nameField.getText().trim());
                stmt.setString(2, descArea.getText().trim());
                stmt.setDouble(3, price);
                stmt.setString(4, (String) categoryBox.getSelectedItem());
                stmt.setString(5, imageField.getText().trim());
                stmt.setBoolean(6, availableBox.isSelected());
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Product added successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadProductData(); // Refresh table
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, 
                    "Error adding product: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    
    private void editProduct() {
        setupDialogUI();
        
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product to edit", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        
        // Fetch current product data
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE product_id = ?")) {
            
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JDialog dialog = new JDialog(this, "Edit Product", true);
                    dialog.setSize(500, 400);
                    dialog.setLocationRelativeTo(this);
                    dialog.setLayout(new BorderLayout());
                    
                    JPanel formPanel = new JPanel(new GridBagLayout());
                    formPanel.setBackground(TABLE_BG);
                    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.insets = new Insets(5, 5, 5, 5);
                    
                    // Name field
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    JLabel nameLabel = new JLabel("Product Name:");
                    nameLabel.setForeground(Color.WHITE);
                    formPanel.add(nameLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JTextField nameField = new JTextField(rs.getString("name"), 20);
                    nameField.setBackground(INPUT_BG);
                    nameField.setForeground(Color.WHITE);
                    nameField.setCaretColor(Color.WHITE);
                    formPanel.add(nameField, gbc);
                    
                    // Category dropdown
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.weightx = 0.0;
                    JLabel categoryLabel = new JLabel("Category:");
                    categoryLabel.setForeground(Color.WHITE);
                    formPanel.add(categoryLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JComboBox<String> categoryBox = new JComboBox<>(new String[]{"DRINK", "MEAL", "MERCHANDISE"});
                    categoryBox.setSelectedItem(rs.getString("category"));
                    styleComboBox(categoryBox);
                    formPanel.add(categoryBox, gbc);
                    
                    // Price field
                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    gbc.weightx = 0.0;
                    JLabel priceLabel = new JLabel("Price:");
                    priceLabel.setForeground(Color.WHITE);
                    formPanel.add(priceLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JTextField priceField = new JTextField(String.valueOf(rs.getDouble("price")), 20);
                    priceField.setBackground(INPUT_BG);
                    priceField.setForeground(Color.WHITE);
                    priceField.setCaretColor(Color.WHITE);
                    formPanel.add(priceField, gbc);
                    
                    // Image URL field
                    gbc.gridx = 0;
                    gbc.gridy = 3;
                    gbc.weightx = 0.0;
                    JLabel imageLabel = new JLabel("Image URL:");
                    imageLabel.setForeground(Color.WHITE);
                    formPanel.add(imageLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JTextField imageField = new JTextField(rs.getString("image_url"), 20);
                    imageField.setBackground(INPUT_BG);
                    imageField.setForeground(Color.WHITE);
                    imageField.setCaretColor(Color.WHITE);
                    formPanel.add(imageField, gbc);
                    
                    // Availability checkbox
                    gbc.gridx = 0;
                    gbc.gridy = 4;
                    gbc.weightx = 0.0;
                    JLabel availableLabel = new JLabel("Available:");
                    availableLabel.setForeground(Color.WHITE);
                    formPanel.add(availableLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JCheckBox availableBox = new JCheckBox();
                    availableBox.setSelected(rs.getBoolean("is_available"));
                    availableBox.setBackground(TABLE_BG);
                    availableBox.setForeground(Color.WHITE);
                    formPanel.add(availableBox, gbc);
                    
                    // Description text area
                    gbc.gridx = 0;
                    gbc.gridy = 5;
                    gbc.weightx = 0.0;
                    JLabel descLabel = new JLabel("Description:");
                    descLabel.setForeground(Color.WHITE);
                    formPanel.add(descLabel, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy = 6;
                    gbc.gridwidth = 2;
                    gbc.weightx = 1.0;
                    gbc.weighty = 1.0;
                    gbc.fill = GridBagConstraints.BOTH;
                    JTextArea descArea = new JTextArea(rs.getString("description"), 5, 20);
                    descArea.setBackground(INPUT_BG);
                    descArea.setForeground(Color.WHITE);
                    descArea.setCaretColor(Color.WHITE);
                    descArea.setLineWrap(true);
                    descArea.setWrapStyleWord(true);
                    JScrollPane descScroll = new JScrollPane(descArea);
                    descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                    formPanel.add(descScroll, gbc);
                    
                    // Button panel
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.setBackground(DARK_BG);
                    
                    JButton cancelBtn = new JButton("Cancel");
                    styleButton(cancelBtn, BTN_DEFAULT, Color.WHITE);
                    cancelBtn.addActionListener(e -> dialog.dispose());
                    
                    JButton saveBtn = new JButton("Save Changes");
                    styleButton(saveBtn, BTN_SUCCESS, Color.WHITE);
                    saveBtn.addActionListener(e -> {
                        // Validate inputs
                        if (nameField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Product name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        double price;
                        try {
                            price = Double.parseDouble(priceField.getText().trim());
                            if (price <= 0) {
                                JOptionPane.showMessageDialog(dialog, "Price must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Update in database
                        try (Connection conn2 = DBConnection.getConnection();
                             PreparedStatement stmt2 = conn2.prepareStatement(
                                 "UPDATE products SET name = ?, description = ?, price = ?, category = ?, image_url = ?, is_available = ? WHERE product_id = ?"
                             )) {
                            
                            stmt2.setString(1, nameField.getText().trim());
                            stmt2.setString(2, descArea.getText().trim());
                            stmt2.setDouble(3, price);
                            stmt2.setString(4, (String) categoryBox.getSelectedItem());
                            stmt2.setString(5, imageField.getText().trim());
                            stmt2.setBoolean(6, availableBox.isSelected());
                            stmt2.setInt(7, productId);
                            
                            int result = stmt2.executeUpdate();
                            if (result > 0) {
                                JOptionPane.showMessageDialog(dialog, 
                                    "Product updated successfully", 
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                                dialog.dispose();
                                loadProductData(); // Refresh table
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, 
                                "Error updating product: " + ex.getMessage(), 
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    
                    buttonPanel.add(cancelBtn);
                    buttonPanel.add(saveBtn);
                    
                    dialog.add(formPanel, BorderLayout.CENTER);
                    dialog.add(buttonPanel, BorderLayout.SOUTH);
                    dialog.setVisible(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error retrieving product data: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProduct() {
        setupDialogUI(); // Apply consistent dialog styling
        
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product to delete", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);
        
        // Confirm deletion
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the product: " + productName + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
                
                stmt.setInt(1, productId);
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Product deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProductData(); // Refresh table
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting product: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}