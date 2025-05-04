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
import java.util.List;

// Added imports for printing
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.StringReader;

public class OrderManagement extends JFrame {
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JTable orderItemsTable;
    private DefaultTableModel itemsTableModel;
    private JComboBox<String> filterStatus;
    private JTextField searchField;
    private JTextArea orderNotes;
    private JLabel lblCustomerInfo, lblOrderTotal, lblOrderDate, lblOrderStatus;
    private JButton btnUpdateStatus, btnPrintOrder, btnBack;
    private User currentUser;

    // Store the generated receipt text temporarily for printing
    private String generatedReceiptText;

    public OrderManagement(User user) {
        this.currentUser = user;

        // Set up the frame
        setTitle("But First, Coffee - Order Management");
        // Make the frame full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // <-- ADDED/MODIFIED LINE
        setUndecorated(false); // Keep standard window decorations (title bar, borders) <-- ADDED/MODIFIED LINE
        // setSize(1200, 700); // <-- REMOVED LINE
        // setLocationRelativeTo(null); // <-- REMOVED LINE
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(40, 40, 40));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 30, 30));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(218, 165, 32)); // Gold

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Filter and Search Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(new Color(30, 30, 30));

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setForeground(Color.WHITE);
        filterPanel.add(filterLabel);

        filterStatus = new JComboBox<>(new String[]{"All", "PENDING", "PROCESSING","ON ITS WAY", "DELIVERED", "CANCELLED"});
        filterStatus.setBackground(new Color(60, 60, 60));
        filterStatus.setForeground(Color.WHITE);
        filterStatus.addActionListener(e -> loadOrderData());
        filterPanel.add(filterStatus);

        filterPanel.add(Box.createHorizontalStrut(15));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        filterPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setBackground(new Color(60, 60, 60));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.addActionListener(e -> loadOrderData());
        filterPanel.add(searchField);

        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, new Color(218, 165, 32), Color.BLACK);
        btnSearch.addActionListener(e -> loadOrderData());
        filterPanel.add(btnSearch);

        headerPanel.add(filterPanel, BorderLayout.EAST);

        // Content Panel (split between orders list and order details)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setBackground(new Color(40, 40, 40));
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // Orders Table Panel (left side)
        JPanel ordersPanel = new JPanel(new BorderLayout(0, 10));
        ordersPanel.setBackground(new Color(40, 40, 40));

        // Table columns for orders table
        String[] ordersColumns = {"Order ID", "Customer", "Date", "Status", "Total"}; // Renamed for clarity
        tableModel = new DefaultTableModel(ordersColumns, 0) { // Used for ordersTable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setBackground(new Color(50, 50, 50));
        ordersTable.setForeground(Color.WHITE);
        ordersTable.setGridColor(new Color(70, 70, 70));
        ordersTable.setRowHeight(25);
        ordersTable.getTableHeader().setBackground(new Color(30, 30, 30));
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Set column widths for orders table
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(70);     // Order ID
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(200);    // Customer
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(150);    // Date
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(100);    // Status
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(80);     // Total

        // Add selection listener
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the orderId from the data model row, not the table view row
                    // (important if sorting/filtering is ever added)
                    int modelRow = ordersTable.convertRowIndexToModel(selectedRow);
                    int orderId = (int) tableModel.getValueAt(modelRow, 0);
                    loadOrderDetails(orderId);
                } else {
                    clearOrderDetails(); // Clear details if selection is cleared
                }
            }
        });


        // Add orders table to a scroll pane
        JScrollPane orderScrollPane = new JScrollPane(ordersTable);
        orderScrollPane.setBorder(BorderFactory.createEmptyBorder());
        orderScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        ordersPanel.add(orderScrollPane, BorderLayout.CENTER);

        // Order Details Panel (right side)
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 15));
        detailsPanel.setBackground(new Color(40, 40, 40));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Order info panel
        JPanel orderInfoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        orderInfoPanel.setBackground(new Color(50, 50, 50));
        orderInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        lblOrderDate = new JLabel("Date: ");
        lblOrderDate.setForeground(Color.WHITE);
        orderInfoPanel.add(lblOrderDate);

        lblCustomerInfo = new JLabel("Customer: ");
        lblCustomerInfo.setForeground(Color.WHITE);
        orderInfoPanel.add(lblCustomerInfo);

        lblOrderStatus = new JLabel("Status: ");
        lblOrderStatus.setForeground(Color.WHITE);
        orderInfoPanel.add(lblOrderStatus);

        lblOrderTotal = new JLabel("Total: ");
        lblOrderTotal.setForeground(Color.WHITE);
        lblOrderTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderInfoPanel.add(lblOrderTotal);

        // Order items table
        JPanel itemsPanel = new JPanel(new BorderLayout(0, 5));
        itemsPanel.setBackground(new Color(40, 40, 40));

        JLabel itemsTitle = new JLabel("Order Items");
        itemsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemsTitle.setForeground(Color.WHITE);
        itemsPanel.add(itemsTitle, BorderLayout.NORTH);

        // Table columns for order items table
        String[] itemColumns = {"Product", "Price", "Quantity", "Subtotal"}; // Used for orderItemsTable
        itemsTableModel = new DefaultTableModel(itemColumns, 0) { // Used for orderItemsTable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
             @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class; // Quantity is int
                // Price and Subtotal are formatted Strings in the table model view,
                // but we'll access underlying doubles for printing if possible,
                // or parse the strings if not easily available.
                return super.getColumnClass(columnIndex);
            }
        };

        orderItemsTable = new JTable(itemsTableModel); // Initialize the table
        orderItemsTable.setBackground(new Color(50, 50, 50));
        orderItemsTable.setForeground(Color.WHITE);
        orderItemsTable.setGridColor(new Color(70, 70, 70));
        orderItemsTable.setRowHeight(25);
        orderItemsTable.getTableHeader().setBackground(new Color(30, 30, 30));
        orderItemsTable.getTableHeader().setForeground(Color.WHITE);
         // Re-apply the header renderer or set header properties directly for item table too
         orderItemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));


        JScrollPane itemsScrollPane = new JScrollPane(orderItemsTable);
        itemsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        itemsScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);

        // --- ADDED CODE BLOCK ---
        // Set column widths for the orderItemsTable AFTER it's created and added to scroll pane
        // Adjust the preferred width for the "Product" column (index 0)
        orderItemsTable.getColumnModel().getColumn(0).setPreferredWidth(250); // Increased width
        // You can optionally set widths for other columns too if needed
        orderItemsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Price
        orderItemsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        orderItemsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Subtotal
        // --- END ADDED CODE BLOCK ---


        // Notes panel
        JPanel notesPanel = new JPanel(new BorderLayout(0, 5));
        notesPanel.setBackground(new Color(40, 40, 40));

        JLabel notesTitle = new JLabel("Order Notes & Details");
        notesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        notesTitle.setForeground(Color.WHITE);
        notesPanel.add(notesTitle, BorderLayout.NORTH);

        orderNotes = new JTextArea(4, 20);
        orderNotes.setBackground(new Color(60, 60, 60));
        orderNotes.setForeground(Color.WHITE);
        orderNotes.setCaretColor(Color.WHITE);
        orderNotes.setEditable(false);
        orderNotes.setLineWrap(true);
        orderNotes.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(orderNotes);
        notesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(new Color(40, 40, 40));

        btnUpdateStatus = new JButton("Update Status");
        styleButton(btnUpdateStatus, new Color(0, 102, 204), Color.WHITE);
        btnUpdateStatus.addActionListener(e -> updateOrderStatus());
        btnUpdateStatus.setEnabled(false);

        btnPrintOrder = new JButton("Print Order");
        styleButton(btnPrintOrder, new Color(70, 70, 70), Color.WHITE);
        // --- ADDED ACTION LISTENER BACK ---
        btnPrintOrder.addActionListener(e -> printOrder());
        // --- END ADDED ACTION LISTENER BACK ---
        btnPrintOrder.setEnabled(false); // This will be enabled when an order is selected

        actionPanel.add(btnUpdateStatus);
        actionPanel.add(btnPrintOrder);

        // Combine all panels in details panel
        JPanel topDetailsPanel = new JPanel(new BorderLayout(0, 10));
        topDetailsPanel.setBackground(new Color(40, 40, 40));
        topDetailsPanel.add(orderInfoPanel, BorderLayout.NORTH);
        topDetailsPanel.add(itemsPanel, BorderLayout.CENTER);

        detailsPanel.add(topDetailsPanel, BorderLayout.CENTER);
        detailsPanel.add(notesPanel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setLeftComponent(ordersPanel);
        splitPane.setRightComponent(detailsPanel);

        // Button Panel (bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40));

        btnBack = new JButton("Back to Dashboard");
        styleButton(btnBack, new Color(70, 70, 70), Color.WHITE);
        btnBack.addActionListener(e -> dispose());
        buttonPanel.add(btnBack);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        // actionPanel is added to BorderLayout.EAST, potentially taking space from the splitPane
        // Consider if this is the desired layout - typically action buttons affect the selected
        // item and would be closer to the details panel or below it.
        // For now, keeping the original structure but noting this.
        mainPanel.add(actionPanel, BorderLayout.EAST);


        setContentPane(mainPanel);

        // Load initial data
        loadOrderData();
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
        });
    }

    private void loadOrderData() {
        tableModel.setRowCount(0); // Clear existing data

        String status = (String) filterStatus.getSelectedItem();
        String search = searchField.getText().trim();

        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT o.order_id, u.full_name, u.email, o.order_date, o.status, o.total_amount " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "WHERE 1=1");

            // Add status filter if not "All"
            if (!"All".equals(status)) {
                query.append(" AND o.status = ?");
            }

            // Add search filter if not empty
            if (!search.isEmpty()) {
                query.append(" AND (u.full_name LIKE ? OR u.email LIKE ? OR CAST(o.order_id AS CHAR) LIKE ?)");
            }

            query.append(" ORDER BY o.order_date DESC");

            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int paramIndex = 1;

                if (!"All".equals(status)) {
                    stmt.setString(paramIndex++, status);
                }

                if (!search.isEmpty()) {
                    String searchPattern = "%" + search + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Format timestamp to date string
                        Timestamp timestamp = rs.getTimestamp("order_date");
                        String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(timestamp);

                        // Format total amount
                        DecimalFormat df = new DecimalFormat("₱#,##0.00");
                        String formattedTotal = df.format(rs.getDouble("total_amount"));

                        // Format customer info
                        String customerInfo = rs.getString("full_name") + " (" + rs.getString("email") + ")";

                        Object[] row = {
                            rs.getInt("order_id"),
                            customerInfo,
                            formattedDate,
                            rs.getString("status"),
                            formattedTotal
                        };
                        tableModel.addRow(row);
                    }

                    // Clear order details if no orders found
                    if (tableModel.getRowCount() == 0) {
                        clearOrderDetails();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading order data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void loadOrderDetails(int orderId) {
        // Clear previous data
        itemsTableModel.setRowCount(0);
        orderNotes.setText("");

        try (Connection conn = DBConnection.getConnection()) {
            // Query for order info
            String orderQuery =
                "SELECT o.*, u.full_name, u.email, u.phone, u.address " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "WHERE o.order_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(orderQuery)) {
                stmt.setInt(1, orderId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Format timestamp to date string
                        Timestamp timestamp = rs.getTimestamp("order_date");
                        String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(timestamp);

                        // Update order info labels
                        lblOrderDate.setText("Date: " + formattedDate);
                        lblCustomerInfo.setText("Customer: " + rs.getString("full_name") + " (" + rs.getString("email") + ")");
                        lblOrderStatus.setText("Status: " + rs.getString("status"));

                        // Format total amount
                        DecimalFormat df = new DecimalFormat("₱#,##0.00");
                        String formattedTotal = df.format(rs.getDouble("total_amount"));
                        lblOrderTotal.setText("Total Amount: " + formattedTotal);

                        // Build order notes
                        StringBuilder notes = new StringBuilder();
                        notes.append("Delivery Method: ").append(rs.getString("delivery_method")).append("\n");

                        if ("DELIVERY".equals(rs.getString("delivery_method"))) {
                            notes.append("Delivery Address: ").append(rs.getString("delivery_address")).append("\n");
                        }

                        notes.append("Payment Method: ").append(rs.getString("payment_method")).append("\n");
                        notes.append("Phone: ").append(rs.getString("phone")).append("\n");
                        notes.append("Subtotal: ").append(df.format(rs.getDouble("subtotal"))).append("\n");
                        notes.append("Shipping Fee: ").append(df.format(rs.getDouble("shipping_fee"))).append("\n");

                        // --- Add Reward Discount and Points Earned to Notes ---
                        if (rs.getDouble("reward_discount") > 0) {
                            notes.append("Reward Discount: -").append(df.format(rs.getDouble("reward_discount"))).append("\n");
                        }
                        notes.append("Points Earned: ").append(rs.getInt("points_earned")).append("\n");
                        // --- End Additions ---


                        orderNotes.setText(notes.toString());

                        // Enable buttons
                        btnUpdateStatus.setEnabled(true);
                        btnPrintOrder.setEnabled(true);
                    } else {
                        clearOrderDetails(); // Should not happen if an order was selected in the table
                    }
                }
            }

            // Query for order items
            // --- MODIFIED QUERY: Join with sizes to get the size name ---
            String itemsQuery =
                "SELECT oi.*, p.name, s.size_name " + // Select size_name
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "LEFT JOIN sizes s ON oi.size_id = s.size_id " + // LEFT JOIN because size_id can be NULL
                "WHERE oi.order_id = ?";
            // --- END MODIFIED QUERY ---

            try (PreparedStatement stmt = conn.prepareStatement(itemsQuery)) {
                stmt.setInt(1, orderId);

                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("₱#,##0.00");

                    while (rs.next()) {
                        // --- MODIFIED LOGIC: Include size name in the product display ---
                        String productName = rs.getString("name");
                        String sizeName = rs.getString("size_name"); // Get the size name

                        String displayProduct = productName;
                        // If sizeName is not null and not empty, append it to the product name
                        if (sizeName != null && !sizeName.trim().isEmpty()) {
                            displayProduct += " (" + sizeName + ")";
                        }
                        // --- END MODIFIED LOGIC ---

                        double price = rs.getDouble("price_at_order"); // Price per item instance (already includes size price)
                        int quantity = rs.getInt("quantity");
                        double subtotal = price * quantity; // Calculate subtotal for the row

                        Object[] row = {
                            displayProduct, // Use the combined name (Product + Size)
                            df.format(price),
                            quantity,
                            df.format(subtotal)
                        };
                        itemsTableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading order details: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearOrderDetails() {
        // Clear all order details
        lblOrderDate.setText("Date: ");
        lblCustomerInfo.setText("Customer: ");
        lblOrderStatus.setText("Status: ");
        lblOrderTotal.setText("Total: ");
        orderNotes.setText("");
        itemsTableModel.setRowCount(0);
        btnUpdateStatus.setEnabled(false);
        btnPrintOrder.setEnabled(false);
        generatedReceiptText = null; // Clear generated text
    }

    private void updateOrderStatus() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) ordersTable.getValueAt(selectedRow, 3);

        // Fix the status options to match database enum
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "PENDING", "PROCESSING", "ON_ITS_WAY", "DELIVERED", "CANCELLED"
        });
        // Match the current status correctly, handling spaces/underscores
         String currentStatusDbFormat = currentStatus.replace(" ", "_");
         boolean found = false;
         for(int i=0; i<statusCombo.getItemCount(); i++){
             if(statusCombo.getItemAt(i).equals(currentStatusDbFormat)){
                 statusCombo.setSelectedIndex(i);
                 found = true;
                 break;
             }
         }
         if(!found){ // Handle cases where status might not match expected list
             statusCombo.setSelectedItem(null); // Select nothing
         }


        int result = JOptionPane.showConfirmDialog(this,
            new Object[]{"Select new status:", statusCombo},
            "Update Order Status",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusCombo.getSelectedItem();

            // Ensure a status was selected
            if (newStatus == null) {
                 JOptionPane.showMessageDialog(this,
                     "Please select a valid status.",
                     "Validation Error", JOptionPane.WARNING_MESSAGE);
                 return;
            }


            try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?")) {

                stmt.setString(1, newStatus);
                stmt.setInt(2, orderId);

                int updateResult = stmt.executeUpdate();
                if (updateResult > 0) {
                    // Update the UI - convert to display format
                    String displayStatus = newStatus.replace("_", " ");
                    ordersTable.setValueAt(displayStatus, selectedRow, 3);
                    lblOrderStatus.setText("Status: " + displayStatus);

                    // Refresh the notifications panel if it exists
                    refreshNotificationsPanel();

                    JOptionPane.showMessageDialog(this,
                        "Order status updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Reload order data to ensure consistency
                    loadOrderData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error updating order status: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     private void refreshNotificationsPanel() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JFrame) {
                Component[] components = ((JFrame) window).getContentPane().getComponents();
                for (Component comp : components) {
                    // Check if the component is an instance of NotificationsPanel
                    // Note: NotificationsPanel class was not provided, assuming it exists
                    // and has a public method loadNotifications().
                    if (comp != null && comp.getClass().getName().equals("coffeeshop.NotificationsPanel")) {
                         try {
                            // Use reflection if NotificationsPanel is not directly accessible
                            comp.getClass().getMethod("loadNotifications").invoke(comp);
                         } catch (Exception e) {
                            System.err.println("Could not refresh NotificationsPanel: " + e.getMessage());
                            e.printStackTrace();
                         }
                        // If NotificationsPanel is in the same package and accessible:
                        // if (comp instanceof NotificationsPanel) {
                        //    ((NotificationsPanel) comp).loadNotifications();
                        // }
                    }
                }
            }
        }
    }

    // --- printOrder method - MODIFIED String.format and truncate width ---
    private void printOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to print.",
                "No Order Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) ordersTable.getValueAt(selectedRow, 0);

        // Generate order receipt text
        StringBuilder receipt = new StringBuilder();
        receipt.append("====================================================================\n");
        receipt.append("                         BUT FIRST, COFFEE           \n");
        receipt.append("                           ORDER RECEIPT           \n");
        receipt.append("====================================================================\n\n");

        receipt.append("Order ID: ").append(orderId).append("\n");
        receipt.append(lblOrderDate.getText()).append("\n");
        receipt.append(lblCustomerInfo.getText()).append("\n");
        receipt.append(lblOrderStatus.getText()).append("\n\n");

        receipt.append("ORDER ITEMS:\n");
        receipt.append("--------------------------------------------------------------------\n"); // Adjusted length
        // Adjust format string widths for potentially better alignment with monospaced font
        // Item: 30, Price: 10, Qty: 5, Subtotal: 12 -> Total width ~57 chars (fits A4, maybe wide receipt)
        // Let's try slightly wider for clarity on screen preview and potentially A4 print
        String itemFormat = "%-35s %10s %5d %12s\n"; // Item takes 35 chars, others 10, 5, 12
        int itemColumnWidth = 35; // Match format string width

        receipt.append(String.format("%-"+itemColumnWidth+"s %10s %5s %12s\n", // Header uses the same width format
            "Item", "Price", "Qty", "Subtotal"));
        receipt.append("--------------------------------------------------------------------\n"); // Keep consistent length

        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            String product = (String) itemsTableModel.getValueAt(i, 0);
            String price = (String) itemsTableModel.getValueAt(i, 1); // Already formatted string
            int qty = (int) itemsTableModel.getValueAt(i, 2);
            String subtotal = (String) itemsTableModel.getValueAt(i, 3); // Already formatted string

             // Truncate product name for the receipt text to fit the widened column
             String truncatedProduct = truncateString(product, itemColumnWidth); // Use the new width

            receipt.append(String.format(itemFormat, // Use the new format string
                truncatedProduct, price, qty, subtotal));
        }

        receipt.append("--------------------------------------------------------------------\n"); // Keep consistent length
        receipt.append(lblOrderTotal.getText()).append("\n\n");

        receipt.append("ADDITIONAL DETAILS:\n");
        receipt.append("--------------------------------------------------------------------\n"); // Keep consistent length
        receipt.append(orderNotes.getText()).append("\n\n");

        receipt.append("====================================================================\n"); // Keep consistent length
        receipt.append("                        Thank you for your order!         \n"); // Center this manually if needed
        receipt.append("====================================================================\n"); // Keep consistent length

        // Store the generated text
        generatedReceiptText = receipt.toString();

        // --- Display Print Preview Dialog ---
        JDialog previewDialog = new JDialog(this, "Order Receipt Preview", true);
        previewDialog.setSize(550, 650); // Slightly larger preview window to show more text
        previewDialog.setLocationRelativeTo(this);

        JTextArea receiptArea = new JTextArea(generatedReceiptText);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Use Monospaced for consistent formatting
        receiptArea.setEditable(false);
        receiptArea.setLineWrap(false); // Prevent wrapping in the preview to better show column alignment
        receiptArea.setWrapStyleWord(false); // Prevent word wrapping

        JScrollPane scrollPane = new JScrollPane(receiptArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("Print");
        JButton cancelButton = new JButton("Cancel");

        styleButton(printButton, new Color(0, 102, 204), Color.WHITE); // Style buttons
        styleButton(cancelButton, new Color(130, 70, 70), Color.WHITE);

        // Action for the Print button in the preview dialog
        printButton.addActionListener(e -> {
            previewDialog.dispose(); // Close the preview dialog

            PrinterJob job = PrinterJob.getPrinterJob();

            // Create the Printable object using the stored generated text
            ReceiptPrintable printableReceipt = new ReceiptPrintable(generatedReceiptText);
            job.setPrintable(printableReceipt);

            // Show the standard print dialog
            boolean doPrint = job.printDialog();

            if (doPrint) {
                try {
                    // Perform the actual printing
                    job.print();
                    // Optional: Show a success message after print job is sent
                    // JOptionPane.showMessageDialog(this, "Print job sent.", "Print Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (PrinterException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error during printing: " + ex.getMessage(),
                        "Print Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 // User cancelled the print dialog
                 // Optional: Show a cancellation message
                 // JOptionPane.showMessageDialog(this, "Printing cancelled by user.", "Print Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> previewDialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(cancelButton);

        previewDialog.setLayout(new BorderLayout());
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);

        previewDialog.setVisible(true); // Show the preview dialog
    }

    private String truncateString(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) {
             // Pad with spaces if shorter than required length,
             // otherwise String.format %- won't work correctly for fixed columns
            StringBuilder padded = new StringBuilder(str);
            while (padded.length() < length) {
                padded.append(" ");
            }
             // Return exactly the required length
            return padded.toString();
        }
        // Truncate and add ellipsis
        if (length > 3) {
             return str.substring(0, length - 3) + "...";
        } else {
             // Handle cases where truncation length is too small for ellipsis
             return str.substring(0, length);
        }
    }


    // --- NEW INNER CLASS FOR PRINTING ---
    // (Unchanged from previous version, as the fix is in generating the text)
    private static class ReceiptPrintable implements Printable {
        private String receiptText;
        private List<String> lines; // Store lines for pagination

        public ReceiptPrintable(String text) {
            this.receiptText = text;
            this.lines = new ArrayList<>();
            // Split the text into lines for pagination
            try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Adding lines directly, assuming pre-formatting handles layout
                     lines.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle potential error splitting lines, though unlikely for StringReader
                lines.clear();
                lines.add("Error preparing print data.");
            }
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) graphics;

            // Set origin to top-left corner of the printable area
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Set font (Monospaced is good for ASCII art/receipts)
            Font font = new Font("Monospaced", Font.PLAIN, 12); // Keep using Monospaced
            g2d.setFont(font);

            // Get font metrics to calculate line height
            FontMetrics metrics = g2d.getFontMetrics(font);
            int lineHeight = metrics.getHeight();
            // int descent = metrics.getDescent(); // Not strictly needed for sequential drawing

            // Calculate printable area dimensions
            double printableHeight = pageFormat.getImageableHeight();

            // Calculate how many lines fit on a page based on line height
            int linesPerPage = (int) (printableHeight / lineHeight);

            // Check if there are any lines to print
            if (lines.isEmpty()) {
                return NO_SUCH_PAGE; // Nothing to print
            }

            // Calculate the start and end line indices for the current page
            int startLineIndex = pageIndex * linesPerPage;
            int endLineIndex = Math.min(startLineIndex + linesPerPage, lines.size());

            // If the requested page is beyond the total number of pages, indicate no such page
            if (startLineIndex >= lines.size()) {
                return NO_SUCH_PAGE;
            }

            // Draw lines for the current page
            int currentY = metrics.getAscent(); // Start position for the first line on the page (Y is baseline)

            for (int i = startLineIndex; i < endLineIndex; i++) {
                String line = lines.get(i);
                g2d.drawString(line, 0, currentY); // Draw at X=0, current Y (baseline)
                currentY += lineHeight; // Move down for the next line
            }

            // Indicate that this page exists and was printed
            return PAGE_EXISTS;
        }
    }
    // --- END NEW INNER CLASS ---

    // Main method for testing (unchanged)
     public static void main(String[] args) {
        // Example user for testing
        User testAdmin = new User();
        testAdmin.setUserId(1);
        testAdmin.setUsername("admin");
        testAdmin.setAdmin(true);
         testAdmin.setFullName("Test Admin"); // Added full name for testing

        try {
            // Use System L&F for consistency with other windows or a custom theme
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
             // Keep cross-platform for consistency if ProductManagement uses it
             UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            OrderManagement orderManagement = new OrderManagement(testAdmin);
            orderManagement.setVisible(true);
        });
    }
}