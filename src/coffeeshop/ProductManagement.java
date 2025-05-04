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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.plaf.basic.BasicButtonUI;
import java.util.List;

public class ProductManagement extends JFrame {
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnBack;
    private JComboBox<String> filterCategory;
    private JTextField searchField;
    private User currentUser;

    private JTabbedPane categoryTabs; // New member for tabs
    private JTable drinksTable, mealsTable, merchandiseTable; // Tables for each category
    private DefaultTableModel drinksTableModel, mealsTableModel, merchandiseTableModel; // Models for each category
    private List<Size> availableSizes; // Keep this for drink size columns
    
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
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
       // Set up the frame
        setTitle("But First, Coffee - Product Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
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

        // Keep the filterCategory JComboBox, but its action listener will now
        // reload the data for the *current* tab, not switch tabs.
        // The JTabbedPane handles category switching.
        filterCategory = new JComboBox<>(new String[]{"All", "DRINK", "MEAL", "MERCHANDISE"});
        styleComboBox(filterCategory);
        // filterCategory.addActionListener(e -> loadProductData()); // Remove this listener
        filterPanel.add(filterCategory);

        filterPanel.add(Box.createHorizontalStrut(15));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        filterPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setBackground(INPUT_BG);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        // searchField.addActionListener(e -> loadProductData()); // Remove this listener
        filterPanel.add(searchField);

        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, GOLD_ACCENT, Color.BLACK);
        // btnSearch.addActionListener(e -> loadProductData()); // Remove this listener
        btnSearch.addActionListener(e -> refreshActiveTab()); // Call the new refresh method
        filterPanel.add(btnSearch);

        headerPanel.add(filterPanel, BorderLayout.EAST);

        // --- MODIFICATION START: Use JTabbedPane for category tables ---

        categoryTabs = new JTabbedPane();
        categoryTabs.setBackground(DARK_BG);
        categoryTabs.setForeground(Color.WHITE);
         // Optional: Custom UI for tabs for better dark theme integration
         categoryTabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
             @Override
             protected void installDefaults() {
                 super.installDefaults();
                 // Customize colors
                 UIManager.put("TabbedPane.contentAreaColor", DARK_BG);
                 UIManager.put("TabbedPane.darkShadow", BORDER_COLOR);
                 UIManager.put("TabbedPane.light", BORDER_COLOR);
                 UIManager.put("TabbedPane.highlight", BORDER_COLOR);
                 UIManager.put("TabbedPane.selected", DARKER_BG); // Color of the selected tab
                 UIManager.put("TabbedPane.unselectedBackground", DARK_BG); // Color of unselected tabs
                 UIManager.put("TabbedPane.foreground", Color.WHITE); // Text color
                 UIManager.put("TabbedPane.focus", new Color(0,0,0,0)); // Remove focus border
             }
             @Override
             protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
                 // Prevent painting the default border
             }
             @Override
              protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                  Color color = isSelected ? DARKER_BG : DARK_BG;
                  g.setColor(color);
                  g.fillRect(x, y, w, h);
              }

               @Override
              protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                   // Draw a border around the tab if needed
                   g.setColor(BORDER_COLOR);
                   g.drawRect(x, y, w, h); // Simple rectangle border
               }
                @Override
               protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                   // Remove focus indicator
               }
                @Override
                protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                    // Increase tab height slightly for better appearance
                    return fontHeight + 10;
                }
                @Override
                protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                     // Increase tab width slightly for padding
                    return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 20;
                }

         });


        loadSizes(); // Load sizes for the Drinks table's columns

        // Create and add the Drinks tab
        JPanel drinksPanel = createCategoryPanel("DRINK");
        categoryTabs.addTab("Drinks", drinksPanel);

        // Create and add the Meals tab
        JPanel mealsPanel = createCategoryPanel("MEAL");
        categoryTabs.addTab("Meals", mealsPanel);

        // Create and add the Merchandise tab
        JPanel merchPanel = createCategoryPanel("MERCHANDISE");
        categoryTabs.addTab("Merchandise", merchPanel);

        // Add a ChangeListener to reload data when switching tabs
        categoryTabs.addChangeListener(e -> refreshActiveTab());

        // Add the tabbed pane to the main panel
        mainPanel.add(categoryTabs, BorderLayout.CENTER);

        // --- MODIFICATION END ---


        // Button Panel - DECLARE AND INITIALIZE HERE
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

        // Add components to main panel - Ensure buttonPanel is added AFTER being declared
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        // mainPanel.add(tablePanel, BorderLayout.CENTER); // Remove the old table panel
        // mainPanel.add(categoryTabs, BorderLayout.CENTER); // This was added above in the MODIFICATION block
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Now safe to add buttonPanel

        setContentPane(mainPanel);

        // Load initial data (for the first tab)
        refreshActiveTab(); // Load data for the initially selected tab
    
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        // Create a custom button UI
        button.setUI(new BasicButtonUI());

        // Set basic properties
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Explicitly set these properties to ensure the button displays correctly
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setOpaque(true);

        // Add this line to ensure the LAF doesn't override your settings
        button.putClientProperty("JButton.buttonType", null);
    }
    
            // --- Keep or add this method ---
    private void loadSizes() {
       availableSizes = new ArrayList<>();
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            // Fetch size_id and size_name, ordered by sort_order
            ResultSet rs = stmt.executeQuery("SELECT size_id, size_name FROM sizes ORDER BY sort_order")) {
           while (rs.next()) {
               // Create Size objects. The price argument here is just a placeholder;
               // the actual size price is fetched per product in loadProductData.
               availableSizes.add(new Size(rs.getInt("size_id"), rs.getString("size_name"), 0));
           }
       } catch (SQLException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(this,
               "Error loading size data: " + e.getMessage(),
               "Database Error", JOptionPane.ERROR_MESSAGE);
       }
   }
   // --- End loadSizes() method ---
        
    private void loadProductData() {
        tableModel.setRowCount(0); // Clear existing data

        String categoryFilter = (String) filterCategory.getSelectedItem(); // Renamed to avoid conflict
        String search = searchField.getText().trim();

        // Build the dynamic part of the SELECT clause for size prices
        StringBuilder selectSizePrices = new StringBuilder();
        for (Size size : availableSizes) {
            // Use conditional aggregation (MAX in this case) to pivot product_sizes data.
            // Alias the column using the size name for easy retrieval from ResultSet.
            // COALESCE(ps.price, 0.00) ensures we get 0.00 instead of NULL if a size price is missing.
            selectSizePrices.append(", MAX(CASE WHEN ps.size_id = ").append(size.getId())
                             .append(" THEN COALESCE(ps.price, 0.00) ELSE 0.00 END) AS `").append(size.getName()).append(" Price`");
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Base query: Join products with product_sizes and sizes
            // Use LEFT JOINs to include products that may not have entries in product_sizes (like meals/merchandise)
            // or may not have prices for all sizes.
            StringBuilder query = new StringBuilder(
                "SELECT p.product_id, p.name, p.category, p.price AS base_price, p.is_available, p.description"); // Select base product fields
            query.append(selectSizePrices); // Add dynamically generated size price selections
            query.append(" FROM products p ");
            query.append(" LEFT JOIN product_sizes ps ON p.product_id = ps.product_id ");
            query.append(" LEFT JOIN sizes s ON ps.size_id = s.size_id ");
            query.append(" WHERE 1=1"); // Start of dynamic filtering

            // Add category filter if not "All"
            if (!"All".equals(categoryFilter)) {
                query.append(" AND p.category = ?");
            }

            // Add search filter if not empty
            if (!search.isEmpty()) {
                // Search name or description
                query.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
            }

            // Group by product to get one row per product with pivoted size prices
            query.append(" GROUP BY p.product_id ");
            query.append(" ORDER BY p.product_id DESC");

            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int paramIndex = 1;

                if (!"All".equals(categoryFilter)) {
                    stmt.setString(paramIndex++, categoryFilter);
                }

                if (!search.isEmpty()) {
                    String searchPattern = "%" + search + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }

                // Debug print the final query being executed
                // System.out.println("Executing Query: " + stmt.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("#,##0.00");

                    while (rs.next()) {
                        List<Object> row = new ArrayList<>();
                        row.add(rs.getInt("product_id"));
                        row.add(rs.getString("name"));
                        String productCategory = rs.getString("category");
                        row.add(productCategory);

                        // Display "Base Price" based on category
                        String formattedBasePrice = "-"; // Default for sized items (drinks)
                        double basePrice = rs.getDouble("base_price");
                        // Check if the category is one that uses the base_price column
                        if ("MEAL".equals(productCategory) || "MERCHANDISE".equals(productCategory)) {
                             if (!rs.wasNull()) { // Ensure the base_price wasn't NULL in the DB
                                formattedBasePrice = "₱" + df.format(basePrice);
                             } else {
                                 formattedBasePrice = "-"; // Display "-" if base price is NULL
                             }
                        }
                        row.add(formattedBasePrice); // Add the base price value

                        // Add size-specific prices dynamically
                        for (Size size : availableSizes) {
                             // Retrieve the price using the dynamic alias from the SQL query
                            double sizePrice = rs.getDouble(size.getName() + " Price"); // Uses alias defined in SQL
                            String formattedSizePrice = "-"; // Default display if price is 0.00 or null

                            // If the category is DRINK and the price is greater than 0, display it
                            if ("DRINK".equals(productCategory)) {
                                 // The SQL COALESCE makes it 0.00 if null, so just check > 0
                                if (sizePrice > 0.00) {
                                    formattedSizePrice = "₱" + df.format(sizePrice);
                                }
                            }
                             // For MEAL/MERCHANDISE, size price columns will be 0.00 due to LEFT JOIN/COALESCE,
                             // which correctly results in the default "-" display here.
                            row.add(formattedSizePrice);
                        }

                        row.add(rs.getBoolean("is_available"));
                        row.add(rs.getString("description"));

                        tableModel.addRow(row.toArray());
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
        // Apply consistent styling to the combo box without hover effects
        comboBox.setBackground(INPUT_BG);
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        // Custom renderer for list items without hover effects
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

        // Add these lines for button styling
        UIManager.put("Button.select", SELECTION_BG);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }
    
        // --- MODIFICATION START: Helper method to create category panels ---
    private JPanel createCategoryPanel(String category) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG); // Use the same background as the main panel

        String[] columns;
        DefaultTableModel model;
        JTable table;

        if ("DRINK".equals(category)) {
             // Columns for Drinks: ID, Name, Category, Plus a column for each size price, Available, Description
            List<String> columnList = new ArrayList<>();
            columnList.add("ID");
            columnList.add("Name");
            columnList.add("Category");
            // Add size price columns dynamically based on loaded sizes
            for (Size size : availableSizes) {
                columnList.add(size.getName() + " Price"); // Column header per size (e.g., "12oz Price", "16oz Price")
            }
            columnList.add("Available");
            columnList.add("Description");
            columns = columnList.toArray(new String[0]);

            model = new DefaultTableModel(columns, 0) {
                 @Override
                 public boolean isCellEditable(int row, int column) { return false; } // Make table non-editable
                 @Override
                 public Class<?> getColumnClass(int columnIndex) {
                     // Find the index of the "Available" column dynamically
                     int availableColIndex = -1;
                     for(int i=0; i < getColumnCount(); i++) {
                         if(getColumnName(i).equals("Available")) {
                             availableColIndex = i;
                             break;
                         }
                     }
                     // If it's the Available column, return Boolean class for checkbox rendering
                     if (columnIndex == availableColIndex) return Boolean.class;

                     // All other columns will display text (String), including formatted prices
                    return String.class;
                 }
             };
            drinksTableModel = model; // Assign to the specific model member
            table = new JTable(drinksTableModel);
            drinksTable = table; // Assign to the specific table member

        } else { // MEAL or MERCHANDISE
            columns = new String[]{"ID", "Name", "Category", "Price", "Available", "Description"}; // Standard columns
            model = new DefaultTableModel(columns, 0) {
                 @Override
                 public boolean isCellEditable(int row, int column) { return false; } // Make table non-editable
                 @Override
                 public Class<?> getColumnClass(int columnIndex) {
                    // For MEAL/MERCH tables, Available is typically at index 4 in this structure
                    if (columnIndex == 4) return Boolean.class;
                    return String.class;
                 }
            };
            // Assign the model and table to the specific members based on category
            if ("MEAL".equals(category)) {
                mealsTableModel = model;
                table = new JTable(mealsTableModel);
                mealsTable = table;
            } else { // MERCHANDISE
                merchandiseTableModel = model;
                table = new JTable(merchandiseTableModel);
                merchandiseTable = table;
            }
        }

        // Style the table (common styling for all tables)
        table.setBackground(TABLE_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(25);
        table.setSelectionBackground(SELECTION_BG); // Add selection color
        table.setSelectionForeground(Color.WHITE); // Ensure selection foreground is visible

        // Style the table header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(DARKER_BG);
                comp.setForeground(Color.WHITE);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 12));
                setHorizontalAlignment(JLabel.CENTER); // Center align header text
                return comp;
            }
        };
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(headerRenderer);
        header.setBackground(DARKER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false); // Prevent column reordering


        // Set column widths (adjust indices based on category and number of size columns)
        if ("DRINK".equals(category)) {
            table.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(180);   // Name
            table.getColumnModel().getColumn(2).setPreferredWidth(90);   // Category

            int baseSizeColumnIndex = 3; // Index where the first size price column starts (after ID, Name, Category)

            // Set preferred width for each size price column dynamically
            for (int i = 0; i < availableSizes.size(); i++) {
                 table.getColumnModel().getColumn(baseSizeColumnIndex + i).setPreferredWidth(90); // Width for size price columns
            }

            // Indices for "Available" and "Description" are shifted by the number of size columns
            int availableColumnIndex = baseSizeColumnIndex + availableSizes.size();
            int descriptionColumnIndex = availableColumnIndex + 1;

            table.getColumnModel().getColumn(availableColumnIndex).setPreferredWidth(70);    // Available
            table.getColumnModel().getColumn(descriptionColumnIndex).setPreferredWidth(250);   // Description

        } else { // MEAL or MERCHANDISE
            table.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(200);   // Name
            table.getColumnModel().getColumn(2).setPreferredWidth(100);   // Category
            table.getColumnModel().getColumn(3).setPreferredWidth(80);    // Price
            table.getColumnModel().getColumn(4).setPreferredWidth(80);    // Available
            table.getColumnModel().getColumn(5).setPreferredWidth(300);   // Description
        }

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border
        scrollPane.getViewport().setBackground(TABLE_BG); // Match viewport background
        panel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane to the category panel

        return panel; // Return the created panel
    }

private void addNewProduct() {
    setupDialogUI(); // Apply custom UI

    JDialog dialog = new JDialog(this, "Add New Product", true);
    dialog.setSize(600, 500); // Increased size to accommodate size price fields
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(TABLE_BG);
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Name field
    gbc.gridx = 0; gbc.gridy = 0; JLabel nameLabel = new JLabel("Product Name:"); nameLabel.setForeground(Color.WHITE); formPanel.add(nameLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0; JTextField nameField = new JTextField(20); nameField.setBackground(INPUT_BG); nameField.setForeground(Color.WHITE); nameField.setCaretColor(Color.WHITE); formPanel.add(nameField, gbc);

    // Category dropdown
    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; JLabel categoryLabel = new JLabel("Category:"); categoryLabel.setForeground(Color.WHITE); formPanel.add(categoryLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0; JComboBox<String> categoryBox = new JComboBox<>(new String[]{"DRINK", "MEAL", "MERCHANDISE"}); 
    styleComboBox(categoryBox); 
    formPanel.add(categoryBox, gbc);

    // Panel for size prices (only shown when category is DRINK)
    JPanel sizePricePanel = new JPanel(new GridBagLayout());
    sizePricePanel.setBackground(TABLE_BG);
    sizePricePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR), "Size Prices", TitledBorder.LEFT, TitledBorder.TOP, null, GOLD_ACCENT));
    
    // Create price fields for each size
    Map<Integer, JTextField> sizePriceFields = new HashMap<>();
    GridBagConstraints spGbc = new GridBagConstraints();
    spGbc.fill = GridBagConstraints.HORIZONTAL;
    spGbc.insets = new Insets(3, 3, 3, 3);
    
    int row = 0;
    for (Size size : availableSizes) {
        spGbc.gridx = 0; spGbc.gridy = row; 
        JLabel sizeLabel = new JLabel(size.getName() + ":"); 
        sizeLabel.setForeground(Color.WHITE); 
        sizePricePanel.add(sizeLabel, spGbc);
        
        spGbc.gridx = 1; 
        JTextField priceField = new JTextField(10);
        priceField.setBackground(INPUT_BG);
        priceField.setForeground(Color.WHITE);
        priceField.setCaretColor(Color.WHITE);
        sizePriceFields.put(size.getId(), priceField);
        sizePricePanel.add(priceField, spGbc);
        
        row++;
    }

    // Regular price field (for non-drink items)
    gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; JLabel priceLabel = new JLabel("Price:"); priceLabel.setForeground(Color.WHITE); formPanel.add(priceLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0; JTextField priceField = new JTextField(20); priceField.setBackground(INPUT_BG); priceField.setForeground(Color.WHITE); priceField.setCaretColor(Color.WHITE); formPanel.add(priceField, gbc);

    // Show/hide price fields based on category selection
    categoryBox.addActionListener(e -> {
        String selectedCategory = (String) categoryBox.getSelectedItem();
        formPanel.remove(sizePricePanel);
        formPanel.remove(priceLabel);
        formPanel.remove(priceField);
        
        if ("DRINK".equals(selectedCategory)) {
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
            formPanel.add(sizePricePanel, gbc);
        } else {
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; 
            formPanel.add(priceLabel, gbc);
            gbc.gridx = 1; 
            formPanel.add(priceField, gbc);
        }
        formPanel.revalidate();
        formPanel.repaint();
    });

    // Image URL field with file chooser
    gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0.0; JLabel imageLabel = new JLabel("Image URL:"); imageLabel.setForeground(Color.WHITE); formPanel.add(imageLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0; JPanel imagePanel = new JPanel(new BorderLayout(5, 0)); imagePanel.setBackground(TABLE_BG); JTextField imageField = new JTextField(20); imageField.setBackground(INPUT_BG); imageField.setForeground(Color.WHITE); imageField.setCaretColor(Color.WHITE); imagePanel.add(imageField, BorderLayout.CENTER);
    JButton browseBtn = new JButton("Browse"); styleButton(browseBtn, BTN_DEFAULT, Color.WHITE);
    browseBtn.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String imageDir = "images/";
                File saveDir = new File(imageDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                String fileName = selectedFile.getName();
                String fileExtension = "";
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileExtension = fileName.substring(dotIndex);
                    fileName = fileName.substring(0, dotIndex);
                }
                String savedFileName = "product_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
                File destinationFile = new File(saveDir, savedFileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imageField.setText(savedFileName);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error saving image: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    });
    imagePanel.add(browseBtn, BorderLayout.EAST); formPanel.add(imagePanel, gbc);

    // Availability checkbox
    gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; JLabel availableLabel = new JLabel("Available:"); availableLabel.setForeground(Color.WHITE); formPanel.add(availableLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0; JCheckBox availableBox = new JCheckBox(); availableBox.setSelected(true); availableBox.setBackground(TABLE_BG); availableBox.setForeground(Color.WHITE); formPanel.add(availableBox, gbc);

    // Description text area
    gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0; JLabel descLabel = new JLabel("Description:"); descLabel.setForeground(Color.WHITE); formPanel.add(descLabel, gbc);
    gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
    JTextArea descArea = new JTextArea(5, 20); descArea.setBackground(INPUT_BG); descArea.setForeground(Color.WHITE); descArea.setCaretColor(Color.WHITE); descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
    JScrollPane descScroll = new JScrollPane(descArea); descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR)); formPanel.add(descScroll, gbc);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); buttonPanel.setBackground(DARK_BG);
    JButton cancelBtn = new JButton("Cancel"); styleButton(cancelBtn, BTN_DEFAULT, Color.WHITE); cancelBtn.addActionListener(e -> dialog.dispose());
    JButton saveBtn = new JButton("Save Product"); styleButton(saveBtn, BTN_SUCCESS, Color.WHITE);
    saveBtn.addActionListener(e -> {
        String name = nameField.getText().trim();
        String description = descArea.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String imageURL = imageField.getText().trim();
        boolean isAvailable = availableBox.isSelected();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Product name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // First insert the product
            String insertProductSQL = "INSERT INTO products (name, description, price, category, image_url, is_available) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement productStmt = conn.prepareStatement(insertProductSQL, Statement.RETURN_GENERATED_KEYS);
            
            // Set base price to 0 for drinks (since they use size prices)
            double basePrice = "DRINK".equals(category) ? 0 : Double.parseDouble(priceField.getText().trim());
            
            productStmt.setString(1, name);
            productStmt.setString(2, description);
            productStmt.setDouble(3, basePrice);
            productStmt.setString(4, category);
            productStmt.setString(5, imageURL);
            productStmt.setBoolean(6, isAvailable);

            int productResult = productStmt.executeUpdate();
            
            if (productResult > 0 && "DRINK".equals(category)) {
                // Get the generated product ID
                ResultSet generatedKeys = productStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int productId = generatedKeys.getInt(1);
                    
                    // Insert size prices for drinks
                    String insertSizeSQL = "INSERT INTO product_sizes (product_id, size_id, price) VALUES (?, ?, ?)";
                    PreparedStatement sizeStmt = conn.prepareStatement(insertSizeSQL);
                    
                    for (Size size : availableSizes) {
                        JTextField sizeField = sizePriceFields.get(size.getId());
                        try {
                            double sizePrice = Double.parseDouble(sizeField.getText().trim());
                            if (sizePrice > 0) {
                                sizeStmt.setInt(1, productId);
                                sizeStmt.setInt(2, size.getId());
                                sizeStmt.setDouble(3, sizePrice);
                                sizeStmt.addBatch();
                            }
                        } catch (NumberFormatException ex) {
                            // Skip if price is invalid
                        }
                    }
                    
                    sizeStmt.executeBatch();
                }
            }

            JOptionPane.showMessageDialog(dialog,
                "Product added successfully",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refreshActiveTab();
        } catch (SQLException | NumberFormatException ex) {
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

    JTable activeTable = getSelectedTable();
    DefaultTableModel activeModel = getSelectedTableModel();

    int selectedRow = activeTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
            "Please select a product to edit",
            "Selection Required", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int productId = (int) activeModel.getValueAt(selectedRow, 0);

    try (Connection conn = DBConnection.getConnection()) {
        // Get product info
        PreparedStatement productStmt = conn.prepareStatement("SELECT * FROM products WHERE product_id = ?");
        productStmt.setInt(1, productId);
        ResultSet productRs = productStmt.executeQuery();

        if (productRs.next()) {
            String currentName = productRs.getString("name");
            String currentCategory = productRs.getString("category");
            String currentDescription = productRs.getString("description");
            String currentImageURL = productRs.getString("image_url");
            boolean currentAvailability = productRs.getBoolean("is_available");

            // Get size prices if it's a drink
            Map<Integer, Double> sizePrices = new HashMap<>();
            if ("DRINK".equals(currentCategory)) {
                PreparedStatement sizeStmt = conn.prepareStatement(
                    "SELECT size_id, price FROM product_sizes WHERE product_id = ?");
                sizeStmt.setInt(1, productId);
                ResultSet sizeRs = sizeStmt.executeQuery();
                
                while (sizeRs.next()) {
                    sizePrices.put(sizeRs.getInt("size_id"), sizeRs.getDouble("price"));
                }
            }

            JDialog dialog = new JDialog(this, "Edit Product", true);
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(TABLE_BG);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Product ID (Display only)
            gbc.gridx = 0; gbc.gridy = 0; JLabel idLabel = new JLabel("Product ID:"); idLabel.setForeground(Color.WHITE); formPanel.add(idLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; JTextField idField = new JTextField(String.valueOf(productId)); idField.setEditable(false); idField.setBackground(INPUT_BG); idField.setForeground(Color.WHITE); formPanel.add(idField, gbc);

            // Name field
            gbc.gridx = 0; gbc.gridy = 1; JLabel nameLabel = new JLabel("Product Name:"); nameLabel.setForeground(Color.WHITE); formPanel.add(nameLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; JTextField nameField = new JTextField(currentName, 20); nameField.setBackground(INPUT_BG); nameField.setForeground(Color.WHITE); nameField.setCaretColor(Color.WHITE); formPanel.add(nameField, gbc);

            // Category (Display only)
            gbc.gridx = 0; gbc.gridy = 2; JLabel categoryLabel = new JLabel("Category:"); categoryLabel.setForeground(Color.WHITE); formPanel.add(categoryLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; JTextField categoryField = new JTextField(currentCategory); categoryField.setEditable(false); categoryField.setBackground(INPUT_BG); categoryField.setForeground(Color.WHITE); formPanel.add(categoryField, gbc);

            // Size prices panel for drinks
            JPanel sizePricePanel = new JPanel(new GridBagLayout());
            sizePricePanel.setBackground(TABLE_BG);
            sizePricePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR), "Size Prices", TitledBorder.LEFT, TitledBorder.TOP, null, GOLD_ACCENT));
            
            Map<Integer, JTextField> sizePriceFields = new HashMap<>();
            if ("DRINK".equals(currentCategory)) {
                GridBagConstraints spGbc = new GridBagConstraints();
                spGbc.fill = GridBagConstraints.HORIZONTAL;
                spGbc.insets = new Insets(3, 3, 3, 3);
                
                int row = 0;
                for (Size size : availableSizes) {
                    spGbc.gridx = 0; spGbc.gridy = row; 
                    JLabel sizeLabel = new JLabel(size.getName() + ":"); 
                    sizeLabel.setForeground(Color.WHITE); 
                    sizePricePanel.add(sizeLabel, spGbc);
                    
                    spGbc.gridx = 1; 
                    JTextField priceField = new JTextField(10);
                    priceField.setBackground(INPUT_BG);
                    priceField.setForeground(Color.WHITE);
                    priceField.setCaretColor(Color.WHITE);
                    
                    // Set existing price if available
                    if (sizePrices.containsKey(size.getId())) {
                        priceField.setText(String.valueOf(sizePrices.get(size.getId())));
                    }
                    
                    sizePriceFields.put(size.getId(), priceField);
                    sizePricePanel.add(priceField, spGbc);
                    
                    row++;
                }
                
                gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
                formPanel.add(sizePricePanel, gbc);
            } else {
                // Regular price field for non-drinks
                gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; JLabel priceLabel = new JLabel("Price:"); priceLabel.setForeground(Color.WHITE); formPanel.add(priceLabel, gbc);
                gbc.gridx = 1; gbc.gridwidth = 1; JTextField priceField = new JTextField(String.valueOf(productRs.getDouble("price")), 20); priceField.setBackground(INPUT_BG); priceField.setForeground(Color.WHITE); priceField.setCaretColor(Color.WHITE); formPanel.add(priceField, gbc);
            }

            // Image URL field with file chooser
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0.0; JLabel imageLabel = new JLabel("Image URL:"); imageLabel.setForeground(Color.WHITE); formPanel.add(imageLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; JPanel imagePanel = new JPanel(new BorderLayout(5, 0)); imagePanel.setBackground(TABLE_BG); JTextField imageField = new JTextField(currentImageURL, 20); imageField.setBackground(INPUT_BG); imageField.setForeground(Color.WHITE); imageField.setCaretColor(Color.WHITE); imagePanel.add(imageField, BorderLayout.CENTER);
            JButton browseBtn = new JButton("Browse"); styleButton(browseBtn, BTN_DEFAULT, Color.WHITE);
            browseBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Image files", "jpg", "jpeg", "png", "gif"));
                if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        String imageDir = "images/";
                        File saveDir = new File(imageDir);
                        if (!saveDir.exists()) {
                            saveDir.mkdirs();
                        }
                        String fileName = selectedFile.getName();
                        String fileExtension = "";
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex > 0) {
                            fileExtension = fileName.substring(dotIndex);
                            fileName = fileName.substring(0, dotIndex);
                        }
                        String savedFileName = "product_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
                        File destinationFile = new File(saveDir, savedFileName);

                        Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imageField.setText(savedFileName);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(dialog,
                            "Error saving image: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });
            imagePanel.add(browseBtn, BorderLayout.EAST); formPanel.add(imagePanel, gbc);

            // Availability checkbox
            gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0; JLabel availableLabel = new JLabel("Available:"); availableLabel.setForeground(Color.WHITE); formPanel.add(availableLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; JCheckBox availableBox = new JCheckBox(); availableBox.setSelected(currentAvailability); availableBox.setBackground(TABLE_BG); availableBox.setForeground(Color.WHITE); formPanel.add(availableBox, gbc);

            // Description text area
            gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.0; JLabel descLabel = new JLabel("Description:"); descLabel.setForeground(Color.WHITE); formPanel.add(descLabel, gbc);
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
            JTextArea descArea = new JTextArea(currentDescription, 5, 20); descArea.setBackground(INPUT_BG); descArea.setForeground(Color.WHITE); descArea.setCaretColor(Color.WHITE); descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descArea); descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR)); formPanel.add(descScroll, gbc);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); buttonPanel.setBackground(DARK_BG);
            JButton cancelBtn = new JButton("Cancel"); styleButton(cancelBtn, BTN_DEFAULT, Color.WHITE); cancelBtn.addActionListener(e -> dialog.dispose());
            JButton saveBtn = new JButton("Save Changes"); styleButton(saveBtn, BTN_SUCCESS, Color.WHITE);
            saveBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String description = descArea.getText().trim();
                String imageURL = imageField.getText().trim();
                boolean isAvailable = availableBox.isSelected();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Product name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Update product info
                    String updateProductSQL = "UPDATE products SET name = ?, description = ?, image_url = ?, is_available = ? WHERE product_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateProductSQL);
                    updateStmt.setString(1, name);
                    updateStmt.setString(2, description);
                    updateStmt.setString(3, imageURL);
                    updateStmt.setBoolean(4, isAvailable);
                    updateStmt.setInt(5, productId);
                    updateStmt.executeUpdate();

                    // Handle size prices for drinks
                    if ("DRINK".equals(currentCategory)) {
                        // First delete existing size prices
                        PreparedStatement deleteStmt = conn.prepareStatement(
                            "DELETE FROM product_sizes WHERE product_id = ?");
                        deleteStmt.setInt(1, productId);
                        deleteStmt.executeUpdate();

                        // Insert new size prices
                        String insertSizeSQL = "INSERT INTO product_sizes (product_id, size_id, price) VALUES (?, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSizeSQL);
                        
                        for (Size size : availableSizes) {
                            JTextField sizeField = sizePriceFields.get(size.getId());
                            try {
                                double sizePrice = Double.parseDouble(sizeField.getText().trim());
                                if (sizePrice > 0) {
                                    insertStmt.setInt(1, productId);
                                    insertStmt.setInt(2, size.getId());
                                    insertStmt.setDouble(3, sizePrice);
                                    insertStmt.addBatch();
                                }
                            } catch (NumberFormatException ex) {
                                // Skip if price is invalid
                            }
                        }
                        
                        insertStmt.executeBatch();
                    } else {
                        // Update regular price for non-drinks
                        try {
                            double price = Double.parseDouble(((JTextField)formPanel.getComponent(11)).getText().trim());
                            PreparedStatement priceStmt = conn.prepareStatement(
                                "UPDATE products SET price = ? WHERE product_id = ?");
                            priceStmt.setDouble(1, price);
                            priceStmt.setInt(2, productId);
                            priceStmt.executeUpdate();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog,
                                "Please enter a valid price",
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(dialog,
                        "Product updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshActiveTab();
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
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error fetching product data for edit: " + e.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void deleteProduct() {
        // Get the currently active table and model
        JTable activeTable = getSelectedTable();
        DefaultTableModel activeModel = getSelectedTableModel();

        int selectedRow = activeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a product to delete",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get product ID and Name from the selected row (assuming ID is column 0, Name is column 1)
        int productId = (int) activeModel.getValueAt(selectedRow, 0);
        String productName = (String) activeModel.getValueAt(selectedRow, 1);

        // Confirmation dialog
        setupDialogUI(); // Apply custom UI
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the product:\n" + productName + "?\n\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE product_id = ?")) {

                stmt.setInt(1, productId);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Product deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshActiveTab(); // Refresh the table for the currently selected tab
                } else {
                     JOptionPane.showMessageDialog(this,
                        "Product not found or could not be deleted.",
                        "Deletion Failed", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting product: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
     private JTable getSelectedTable() {
         int selectedIndex = categoryTabs.getSelectedIndex();
         String tabTitle = categoryTabs.getTitleAt(selectedIndex);
         switch (tabTitle) {
             case "Drinks": return drinksTable;
             case "Meals": return mealsTable;
             case "Merchandise": return merchandiseTable;
             default: return null; // Should not happen
         }
     }

     private DefaultTableModel getSelectedTableModel() {
         int selectedIndex = categoryTabs.getSelectedIndex();
         String tabTitle = categoryTabs.getTitleAt(selectedIndex);
         switch (tabTitle) {
             case "Drinks": return drinksTableModel;
             case "Meals": return mealsTableModel;
             case "Merchandise": return merchandiseTableModel;
             default: return null; // Should not happen
         }
     }
     
     private void refreshActiveTab() {
        String search = searchField.getText().trim();
        String categoryFilter = (String) filterCategory.getSelectedItem(); // Get the category filter value

        int selectedIndex = categoryTabs.getSelectedIndex();
        String tabTitle = categoryTabs.getTitleAt(selectedIndex); // Get the title of the active tab

        // Determine which load method to call based on the active tab AND the category filter
        // If "All" is selected in the filter, just load the active tab's data.
        // If a specific category is selected, only load that category's data if the tab matches.
        // If the tab doesn't match the specific category filter, clear the table? Or disable filter?
        // Let's make the filter apply only to the data loaded in the CURRENT tab.
        // The category filter JComboBox is slightly redundant with tabs, but we'll keep it and apply it
        // *within* the load method for the active tab.

        // Get the category associated with the current tab
        String activeTabCategory;
        switch (tabTitle) {
            case "Drinks": activeTabCategory = "DRINK"; break;
            case "Meals": activeTabCategory = "MEAL"; break;
            case "Merchandise": activeTabCategory = "MERCHANDISE"; break;
            default: activeTabCategory = "All"; // Should not happen with defined tabs
        }

        // Check if the global category filter matches the current tab's category.
        // If the filter is "All" or matches the tab's category, load the data.
        // Otherwise, clear the current tab's table.
        if ("All".equals(categoryFilter) || categoryFilter.equals(activeTabCategory)) {
             switch (tabTitle) {
                case "Drinks":
                    loadDrinkData(search); // Apply search filter
                    break;
                case "Meals":
                    loadMealData(search); // Apply search filter
                    break;
                case "Merchandise":
                    loadMerchandiseData(search); // Apply search filter
                    break;
            }
        } else {
            // If the selected filter doesn't match the active tab, clear the table
             DefaultTableModel activeModel = getSelectedTableModel();
             if (activeModel != null) {
                 activeModel.setRowCount(0);
             }
        }
    }
     
         private void loadMerchandiseData(String searchFilter) {
         merchandiseTableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT product_id, name, category, price, is_available, description FROM products WHERE category = 'MERCHANDISE'");

            if (!searchFilter.isEmpty()) {
                query.append(" AND (name LIKE ? OR description LIKE ?)");
            }

            query.append(" ORDER BY product_id DESC");

            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int paramIndex = 1;
                if (!searchFilter.isEmpty()) {
                    String searchPattern = "%" + searchFilter + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            "₱" + df.format(rs.getDouble("price")), // Merchandise uses the base price column
                            rs.getBoolean("is_available"),
                            rs.getString("description")
                        };
                        merchandiseTableModel.addRow(row); // Add the row to the Merchandise table model
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading merchandise data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        private void loadMealData(String searchFilter) {
        mealsTableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT product_id, name, category, price, is_available, description FROM products WHERE category = 'MEAL'");

            if (!searchFilter.isEmpty()) {
                query.append(" AND (name LIKE ? OR description LIKE ?)");
            }

            query.append(" ORDER BY product_id DESC");

            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int paramIndex = 1;
                if (!searchFilter.isEmpty()) {
                    String searchPattern = "%" + searchFilter + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            "₱" + df.format(rs.getDouble("price")), // Meals use the base price column
                            rs.getBoolean("is_available"),
                            rs.getString("description")
                        };
                        mealsTableModel.addRow(row); // Add the row to the Meals table model
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading meal data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
            private void loadDrinkData(String searchFilter) {
        drinksTableModel.setRowCount(0); // Clear existing data

        // Build the dynamic part of the SELECT clause for size prices
        // This creates columns like `12oz Price`, `16oz Price`, etc.
        StringBuilder selectSizePrices = new StringBuilder();
        for (Size size : availableSizes) {
             // Use conditional aggregation (MAX in this case) to pivot product_sizes data.
             // Alias the column using the size name for easy retrieval from ResultSet.
             // COALESCE(ps.price, 0.00) ensures we get 0.00 instead of NULL if a size price is missing for a product/size combo.
             selectSizePrices.append(", MAX(CASE WHEN ps.size_id = ").append(size.getId())
                              .append(" THEN COALESCE(ps.price, 0.00) ELSE 0.00 END) AS `").append(size.getName()).append(" Price`");
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Base query: Join products with product_sizes and sizes
            // Use LEFT JOINs to include products that may not have entries in product_sizes
            StringBuilder query = new StringBuilder(
                "SELECT p.product_id, p.name, p.category, p.is_available, p.description"); // Select base product fields
            query.append(selectSizePrices); // Add dynamically generated size price selections
            query.append(" FROM products p ");
            query.append(" LEFT JOIN product_sizes ps ON p.product_id = ps.product_id ");
            query.append(" LEFT JOIN sizes s ON ps.size_id = s.size_id ");
            query.append(" WHERE p.category = 'DRINK' "); // Filter specifically for drinks

            // Add search filter if not empty
            if (!searchFilter.isEmpty()) { // Use the searchFilter parameter passed to this method
                query.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
            }

            query.append(" GROUP BY p.product_id "); // Group by product to pivot sizes
            query.append(" ORDER BY p.product_id DESC"); // Order by product ID

            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int paramIndex = 1;

                if (!searchFilter.isEmpty()) {
                    String searchPattern = "%" + searchFilter + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }

                // Debug print the final query being executed
                // System.out.println("Executing Drink Query: " + stmt.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("#,##0.00");

                    while (rs.next()) {
                        List<Object> row = new ArrayList<>();
                        row.add(rs.getInt("product_id"));
                        row.add(rs.getString("name"));
                        row.add(rs.getString("category"));

                        // Add size-specific prices dynamically based on the aliases used in the SQL select
                        for (Size size : availableSizes) {
                            // Retrieve the price using the dynamic alias from the SQL query
                            double sizePrice = rs.getDouble(size.getName() + " Price"); // Uses alias defined in SQL
                            String formattedSizePrice = "-"; // Default display if price is 0.00 or null (from COALESCE)

                             // If the category is DRINK and the price is greater than 0, display it
                             // Note: For drinks, the base_price column in 'products' is often ignored if 'product_sizes' exists.
                             // We are displaying the price from 'product_sizes' here.
                            if (sizePrice > 0.00) {
                                formattedSizePrice = "₱" + df.format(sizePrice);
                            }
                            row.add(formattedSizePrice);
                        }

                        row.add(rs.getBoolean("is_available"));
                        row.add(rs.getString("description"));

                        drinksTableModel.addRow(row.toArray()); // Add the row to the Drinks table model
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading drink data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }  
}