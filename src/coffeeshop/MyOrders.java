/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

    public class MyOrders extends JPanel {
        private JTable ordersTable;
        private DefaultTableModel tableModel;
        private JTable orderItemsTable;
        private DefaultTableModel itemsTableModel;
        private JComboBox<String> filterStatus;
        private JTextArea orderNotes;
        private JLabel lblOrderTotal, lblOrderDate, lblOrderStatus;
        private User currentUser;

        public MyOrders(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 30, 30));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("My Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(218, 165, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Filter Panel with improved styling
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(new Color(30, 30, 30));

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterLabel.setForeground(Color.WHITE);
        filterPanel.add(filterLabel);

        // Call setupFilterStatus instead of creating combo box directly
        setupFilterStatus();
        filterPanel.add(filterStatus);

        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel with adjusted divider location
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500); // Reduced from 600 to give more space to details
        splitPane.setBackground(new Color(40, 40, 40));
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Orders Table Panel (left side) - unchanged
        JPanel ordersPanel = new JPanel(new BorderLayout(0, 10));
        ordersPanel.setBackground(new Color(40, 40, 40));
        
        // Table columns
        String[] columns = {"Order ID", "Date", "Status", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(tableModel);
        styleTable(ordersTable);
        
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
                    loadOrderDetails(orderId);
                }
            }
        });
        
        JScrollPane orderScrollPane = new JScrollPane(ordersTable);
        orderScrollPane.setBorder(BorderFactory.createEmptyBorder());
        orderScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        ordersPanel.add(orderScrollPane, BorderLayout.CENTER);
        
        // Order Details Panel (right side) - modified to give more space to details
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 15));
        detailsPanel.setBackground(new Color(40, 40, 40));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Order info panel - unchanged
        JPanel orderInfoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        orderInfoPanel.setBackground(new Color(50, 50, 50));
        orderInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        lblOrderDate = new JLabel("Date: ");
        lblOrderDate.setForeground(Color.WHITE);
        orderInfoPanel.add(lblOrderDate);
        
        lblOrderStatus = new JLabel("Status: ");
        lblOrderStatus.setForeground(Color.WHITE);
        orderInfoPanel.add(lblOrderStatus);
        
        lblOrderTotal = new JLabel("Total: ");
        lblOrderTotal.setForeground(Color.WHITE);
        lblOrderTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderInfoPanel.add(lblOrderTotal);
        
        // Order items table - made shorter by reducing its preferred size
        // Order items table - balanced size
        JPanel itemsPanel = new JPanel(new BorderLayout(0, 5));
        itemsPanel.setBackground(new Color(40, 40, 40));
        itemsPanel.setPreferredSize(new Dimension(0, 200)); // Balanced height
        
        JLabel itemsTitle = new JLabel("Order Items");
        itemsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemsTitle.setForeground(Color.WHITE);
        itemsPanel.add(itemsTitle, BorderLayout.NORTH);
        
        String[] itemColumns = {"Product", "Price", "Quantity", "Subtotal"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderItemsTable = new JTable(itemsTableModel);
        styleTable(orderItemsTable);
        
        JScrollPane itemsScrollPane = new JScrollPane(orderItemsTable);
        itemsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        itemsScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        
        // Notes panel - balanced size
        JPanel notesPanel = new JPanel(new BorderLayout(0, 5));
        notesPanel.setBackground(new Color(40, 40, 40));
        notesPanel.setPreferredSize(new Dimension(0, 200)); // Same height as items panel
        
        JLabel notesTitle = new JLabel("Order Details");
        notesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        notesTitle.setForeground(Color.WHITE);
        notesPanel.add(notesTitle, BorderLayout.NORTH);
        
        orderNotes = new JTextArea(6, 20); // Adjusted rows
        orderNotes.setBackground(new Color(60, 60, 60));
        orderNotes.setForeground(Color.WHITE);
        orderNotes.setCaretColor(Color.WHITE);
        orderNotes.setEditable(false);
        orderNotes.setLineWrap(true);
        orderNotes.setWrapStyleWord(true);
        orderNotes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane notesScrollPane = new JScrollPane(orderNotes);
        notesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);
        
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
        add(splitPane, BorderLayout.CENTER);
        
        // Load initial data
        loadOrderData();
    }
    
    private void setupFilterStatus() {
        filterStatus = new JComboBox<>(new String[]{"All", "PENDING", "PROCESSING", "DELIVERED", "CANCELLED"});
        filterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterStatus.setBackground(new Color(60, 60, 60));
        filterStatus.setForeground(Color.WHITE);
        filterStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Fix for combo box display issues
        filterStatus.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(new Color(80, 80, 80));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(true);
                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Paint custom background for the combobox display area
                g.setColor(new Color(60, 60, 60));
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        // Updated renderer that handles all states
        filterStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // This handles both the dropdown items and the main display
                if (index == -1) {
                    // This is the currently selected item displayed in the combo box itself
                    renderer.setForeground(Color.WHITE);
                    renderer.setBackground(new Color(60, 60, 60));
                } else {
                    // These are items in the dropdown list
                    renderer.setForeground(Color.WHITE);
                    renderer.setBackground(isSelected ? new Color(80, 80, 80) : new Color(60, 60, 60));
                }

                renderer.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                return renderer;
            }
        });

        // Fix for the dropdown popup
        filterStatus.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JComboBox<?> combo = (JComboBox<?>) e.getSource();
                Object child = combo.getAccessibleContext().getAccessibleChild(0);
                if (child instanceof JPopupMenu) {
                    JPopupMenu popup = (JPopupMenu) child;
                    popup.setBackground(new Color(60, 60, 60));
                    popup.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
                }
            }

            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        filterStatus.addActionListener(e -> loadOrderData());
    }
    
    private void styleTable(JTable table) {
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 70));
        table.setRowHeight(25);
        
        // Improved table header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 30, 30));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        
        // Disable hover effects on header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(30, 30, 30));
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return this;
            }
        });
    }
    
    private void loadOrderData() {
        tableModel.setRowCount(0); // Clear existing data
        
        String status = (String) filterStatus.getSelectedItem();
        
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT order_id, order_date, status, total_amount " +
                "FROM orders " +
                "WHERE user_id = ?");
            
            // Add status filter if not "All"
            if (!"All".equals(status)) {
                query.append(" AND status = ?");
            }
            
            query.append(" ORDER BY order_date DESC");
            
            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                stmt.setInt(1, currentUser.getUserId());
                
                if (!"All".equals(status)) {
                    stmt.setString(2, status);
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
                        
                        Object[] row = {
                            rs.getInt("order_id"),
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
                "SELECT * FROM orders WHERE order_id = ? AND user_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(orderQuery)) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, currentUser.getUserId());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Format timestamp to date string
                        Timestamp timestamp = rs.getTimestamp("order_date");
                        String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(timestamp);
                        
                        // Update order info labels
                        lblOrderDate.setText("Date: " + formattedDate);
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
                        notes.append("Subtotal: ").append(df.format(rs.getDouble("subtotal"))).append("\n");
                        notes.append("Shipping Fee: ").append(df.format(rs.getDouble("shipping_fee")));
                        
                        orderNotes.setText(notes.toString());
                    }
                }
            }
           
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
        lblOrderStatus.setText("Status: ");
        lblOrderTotal.setText("Total: ");
        orderNotes.setText("");
        itemsTableModel.setRowCount(0);
    }
}
