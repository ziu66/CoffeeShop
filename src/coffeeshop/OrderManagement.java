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
    
    public OrderManagement(User user) {
        this.currentUser = user;
        
        // Set up the frame
        setTitle("But First, Coffee - Order Management");
        setSize(1200, 700);
        setLocationRelativeTo(null);
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
        
        filterStatus = new JComboBox<>(new String[]{"All", "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"});
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
        
        // Table columns
        String[] columns = {"Order ID", "Customer", "Date", "Status", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
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
        
        // Set column widths
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
                    int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
                    loadOrderDetails(orderId);
                }
            }
        });
        
        // Add table to a scroll pane
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
        
        String[] itemColumns = {"Product", "Price", "Quantity", "Subtotal"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        orderItemsTable = new JTable(itemsTableModel);
        orderItemsTable.setBackground(new Color(50, 50, 50));
        orderItemsTable.setForeground(Color.WHITE);
        orderItemsTable.setGridColor(new Color(70, 70, 70));
        orderItemsTable.setRowHeight(25);
        orderItemsTable.getTableHeader().setBackground(new Color(30, 30, 30));
        orderItemsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane itemsScrollPane = new JScrollPane(orderItemsTable);
        itemsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        itemsScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        
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
        btnPrintOrder.addActionListener(e -> printOrder());
        btnPrintOrder.setEnabled(false);
        
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
                        notes.append("Shipping Fee: ").append(df.format(rs.getDouble("shipping_fee")));
                        
                        orderNotes.setText(notes.toString());
                        
                        // Enable buttons
                        btnUpdateStatus.setEnabled(true);
                        btnPrintOrder.setEnabled(true);
                    }
                }
            }
            
            // Query for order items
            String itemsQuery = 
                "SELECT oi.*, p.name " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(itemsQuery)) {
                stmt.setInt(1, orderId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("₱#,##0.00");
                    
                    while (rs.next()) {
                        double price = rs.getDouble("price_at_order");
                        int quantity = rs.getInt("quantity");
                        double subtotal = price * quantity;
                        
                        Object[] row = {
                            rs.getString("name"),
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
    }
    
    private void updateOrderStatus() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) ordersTable.getValueAt(selectedRow, 3);
        
        // Create a combo box with status options
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"});
        statusCombo.setSelectedItem(currentStatus);
        
        // Show dialog to select new status
        int result = JOptionPane.showConfirmDialog(this, 
            new Object[]{"Select new status:", statusCombo}, 
            "Update Order Status", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusCombo.getSelectedItem();
            
            // Update status in database
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE orders SET status = ? WHERE order_id = ?")) {
                
                stmt.setString(1, newStatus);
                stmt.setInt(2, orderId);
                
                int updateResult = stmt.executeUpdate();
                if (updateResult > 0) {
                    // Update the UI
                    ordersTable.setValueAt(newStatus, selectedRow, 3);
                    lblOrderStatus.setText("Status: " + newStatus);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Order status updated successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error updating order status: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
        
        // Generate order receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("==================================\n");
        receipt.append("           BUT FIRST, COFFEE           \n");
        receipt.append("           ORDER RECEIPT           \n");
        receipt.append("==================================\n\n");
        
        receipt.append("Order ID: ").append(orderId).append("\n");
        receipt.append(lblOrderDate.getText()).append("\n");
        receipt.append(lblCustomerInfo.getText()).append("\n");
        receipt.append(lblOrderStatus.getText()).append("\n\n");
        
        receipt.append("ORDER ITEMS:\n");
        receipt.append("----------------------------------\n");
        receipt.append(String.format("%-25s %8s %5s %10s\n", "Item", "Price", "Qty", "Subtotal"));
        receipt.append("----------------------------------\n");
        
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            String product = (String) itemsTableModel.getValueAt(i, 0);
            String price = (String) itemsTableModel.getValueAt(i, 1);
            int qty = (int) itemsTableModel.getValueAt(i, 2);
            String subtotal = (String) itemsTableModel.getValueAt(i, 3);
            
            receipt.append(String.format("%-25s %8s %5d %10s\n", 
                truncateString(product, 25), price, qty, subtotal));
        }
        
        receipt.append("----------------------------------\n");
        receipt.append(lblOrderTotal.getText()).append("\n\n");
        
        receipt.append("ADDITIONAL DETAILS:\n");
        receipt.append("----------------------------------\n");
        receipt.append(orderNotes.getText()).append("\n\n");
        
        receipt.append("==================================\n");
        receipt.append("         Thank you for your order!         \n");
        receipt.append("==================================\n");
        
        // Create print preview dialog
        JDialog previewDialog = new JDialog(this, "Order Receipt Preview", true);
        previewDialog.setSize(500, 600);
        previewDialog.setLocationRelativeTo(this);
        
        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("Print");
        JButton cancelButton = new JButton("Cancel");
        
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(previewDialog, 
                "Printing functionality would be implemented here.\n" +
                "In a real application, this would send the receipt to a printer.",
                "Print Simulation", JOptionPane.INFORMATION_MESSAGE);
            previewDialog.dispose();
        });
        
        cancelButton.addActionListener(e -> previewDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(cancelButton);
        
        previewDialog.setLayout(new BorderLayout());
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        previewDialog.setVisible(true);
    }
    
    private String truncateString(String str, int length) {
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length - 3) + "...";
    }
}