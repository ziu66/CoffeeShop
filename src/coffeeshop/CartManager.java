/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CartManager {
    private final User currentUser;
    private final List<CartItem> cartItems = new ArrayList<>();
    private int currentCartId = -1;
    private JPanel checkoutPanel;
    private JLabel cartTotalLabel;
    
    public CartManager(User user) {
        this.currentUser = user;
        loadCartItems();
        updateCartTotal();
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public double getCartTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getItem().getPrice() * item.getQuantity();
            }
        }
        return total;
    }
    
    // Create cart panel that can be added to the main user dashboard
    public JPanel createCartPanel(JPanel cardPanel, CardLayout cardLayout) {
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Your Cart");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JLabel itemCountLabel = new JLabel(cartItems.size() + " item(s)");
        itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titlePanel.add(itemCountLabel, BorderLayout.EAST);
        cartPanel.add(titlePanel, BorderLayout.NORTH);

        // Cart items panel
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);

        if (cartItems.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            itemsPanel.add(emptyLabel);
        } else {
            // Add select all checkbox
            JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectAllPanel.setBackground(Color.WHITE);

            JCheckBox selectAll = new JCheckBox("SELECT ALL (" + cartItems.size() + " ITEM(S))");
            selectAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                cartItems.forEach(item -> item.setSelected(selected));
                updateCartDisplay(itemsPanel, itemCountLabel);
            });
            selectAllPanel.add(selectAll);
            itemsPanel.add(selectAllPanel);
            itemsPanel.add(Box.createVerticalStrut(10));

            // Add cart items
            for (CartItem item : cartItems) {
                itemsPanel.add(createCartItemPanel(item, itemsPanel, itemCountLabel));
                itemsPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Order summary panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 0, 0, 0)
        ));

        // Subtotal
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(Color.WHITE);
        JLabel subtotalLabel = new JLabel("Subtotal (" + getSelectedItemCount() + " items)");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel subtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));
        subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValue, BorderLayout.EAST);
        summaryPanel.add(subtotalPanel);

        // Shipping fee
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBackground(Color.WHITE);
        shippingPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel shippingLabel = new JLabel("Shipping Fee");
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel shippingValue = new JLabel("₱40.00");
        shippingValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        shippingPanel.add(shippingLabel, BorderLayout.WEST);
        shippingPanel.add(shippingValue, BorderLayout.EAST);
        summaryPanel.add(shippingPanel);

        // Voucher code
        JPanel voucherPanel = new JPanel(new BorderLayout(5, 0));
        voucherPanel.setBackground(Color.WHITE);
        JTextField voucherField = new JTextField();
        voucherField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JButton applyBtn = new JButton("APPLY");
        applyBtn.setBackground(new Color(76, 175, 80));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setBorderPainted(false);
        voucherPanel.add(new JLabel("Enter Voucher Code"), BorderLayout.NORTH);
        voucherPanel.add(voucherField, BorderLayout.CENTER);
        voucherPanel.add(applyBtn, BorderLayout.EAST);
        summaryPanel.add(voucherPanel);
        summaryPanel.add(Box.createVerticalStrut(10));

        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel totalLabel = new JLabel("Subtotal");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel totalValue = new JLabel("₱" + String.format("%.2f", getCartTotal() + 40.00));
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValue.setForeground(new Color(220, 20, 60));
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        summaryPanel.add(totalPanel);

        // VAT notice
        JLabel vatLabel = new JLabel("VAT included, where applicable");
        vatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vatLabel.setForeground(Color.GRAY);
        vatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        summaryPanel.add(vatLabel);

        // Checkout button
        JButton checkoutBtn = new JButton("PROCEED TO CHECKOUT");
        checkoutBtn.setBackground(new Color(220, 20, 60));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.setPreferredSize(new Dimension(0, 50));
        checkoutBtn.addActionListener(e -> {
            if (getSelectedItemCount() > 0) {
                cardLayout.show(cardPanel, "checkout");
            } else {
                JOptionPane.showMessageDialog(cartPanel, 
                    "Please select at least one item to checkout",
                    "No Items Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        summaryPanel.add(checkoutBtn);

        cartPanel.add(summaryPanel, BorderLayout.SOUTH);

        return cartPanel;
    }

    private JPanel createCartItemPanel(CartItem cartItem, JPanel parentPanel, JLabel itemCountLabel) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Left side - checkbox and item info
        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setBackground(Color.WHITE);

        JCheckBox selectBox = new JCheckBox();
        selectBox.setSelected(cartItem.isSelected());
        selectBox.addActionListener(e -> {
            cartItem.setSelected(selectBox.isSelected());
            updateCartDisplay(parentPanel, itemCountLabel);
            updateCartTotal(); 
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(cartItem.getItem().getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel descLabel = new JLabel("Size: Regular");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);

        leftPanel.add(selectBox, BorderLayout.WEST);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        // Right side - price and delete button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", cartItem.getItem().getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton deleteBtn = new JButton("DELETE");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.addActionListener(e -> {
            removeCartItem(cartItem.getItem().getProductId());
            cartItems.remove(cartItem);
            updateCartDisplay(parentPanel, itemCountLabel);
            updateCartTotal();
        });

        rightPanel.add(priceLabel, BorderLayout.CENTER);
        rightPanel.add(deleteBtn, BorderLayout.SOUTH);

        itemPanel.add(leftPanel, BorderLayout.CENTER);
        itemPanel.add(rightPanel, BorderLayout.EAST);

        return itemPanel;
    }

    private void updateCartDisplay(JPanel itemsPanel, JLabel itemCountLabel) {
        itemsPanel.removeAll();

        if (cartItems.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            itemsPanel.add(emptyLabel);
        } else {
            // Add select all checkbox
            JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectAllPanel.setBackground(Color.WHITE);

            JCheckBox selectAll = new JCheckBox("SELECT ALL (" + cartItems.size() + " ITEM(S))");
            selectAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                cartItems.forEach(item -> item.setSelected(selected));
                updateCartDisplay(itemsPanel, itemCountLabel);
            });
            selectAllPanel.add(selectAll);
            updateCartTotal();
            itemsPanel.add(selectAllPanel);
            itemsPanel.add(Box.createVerticalStrut(10));

            // Add cart items
            for (CartItem item : cartItems) {
                itemsPanel.add(createCartItemPanel(item, itemsPanel, itemCountLabel));
                itemsPanel.add(Box.createVerticalStrut(10));
            }
        }

        itemCountLabel.setText(cartItems.size() + " item(s)");
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private void updateCartTotal() {
        if (cartTotalLabel != null) {
            cartTotalLabel.setText("Selected Total: ₱" + String.format("%.2f", getCartTotal()));
        }
    }

    // Create checkout panel for the main dashboard
    public JPanel createCheckoutPanel(JPanel cardPanel, CardLayout cardLayout) {
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(Color.WHITE);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backBtn = new JButton("← Back to Cart");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(Color.BLUE);
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "cart"));
        titlePanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Checkout");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        checkoutPanel.add(titlePanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Shipping address
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));
        addressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel addressTitle = new JLabel("Shipping Address");
        addressTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addressPanel.add(addressTitle);
        addressPanel.add(Box.createVerticalStrut(10));

        JTextArea addressField = new JTextArea(currentUser.getAddress());
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addressPanel.add(new JScrollPane(addressField));
        addressPanel.add(Box.createVerticalStrut(10));

        JButton addAddressBtn = new JButton("Add Shipping Address");
        addAddressBtn.setContentAreaFilled(false);
        addAddressBtn.setBorderPainted(false);
        addAddressBtn.setForeground(Color.BLUE);
        addressPanel.add(addAddressBtn);
        contentPanel.add(addressPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Order summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel summaryTitle = new JLabel("Order Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createVerticalStrut(10));

        // Add order items
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JLabel itemName = new JLabel(item.getItem().getName() + " × " + item.getQuantity());
                itemName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                JLabel itemPrice = new JLabel("₱" + String.format("%.2f", item.getItem().getPrice() * item.getQuantity()));
                itemPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                itemPanel.add(itemName, BorderLayout.WEST);
                itemPanel.add(itemPrice, BorderLayout.EAST);
                summaryPanel.add(itemPanel);
            }
        }

        summaryPanel.add(Box.createVerticalStrut(10));

        // Subtotal
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel subtotalLabel = new JLabel("Subtotal (" + getSelectedItemCount() + " items)");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel subtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));
        subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 14));

        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValue, BorderLayout.EAST);
        summaryPanel.add(subtotalPanel);

        // Shipping fee
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel shippingLabel = new JLabel("Shipping Fee");
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel shippingValue = new JLabel("₱40.00");
        shippingValue.setFont(new Font("Segoe UI", Font.BOLD, 14));

        shippingPanel.add(shippingLabel, BorderLayout.WEST);
        shippingPanel.add(shippingValue, BorderLayout.EAST);
        summaryPanel.add(shippingPanel);

        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Payment method
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel paymentTitle = new JLabel("Payment Method");
        paymentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        paymentPanel.add(paymentTitle);
        paymentPanel.add(Box.createVerticalStrut(10));

        ButtonGroup paymentGroup = new ButtonGroup();
        JRadioButton codBtn = new JRadioButton("Cash on Delivery");
        JRadioButton cardBtn = new JRadioButton("Credit/Debit Card");
        JRadioButton gcashBtn = new JRadioButton("GCash");

        paymentGroup.add(codBtn);
        paymentGroup.add(cardBtn);
        paymentGroup.add(gcashBtn);
        codBtn.setSelected(true);

        paymentPanel.add(codBtn);
        paymentPanel.add(cardBtn);
        paymentPanel.add(gcashBtn);
        contentPanel.add(paymentPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Total and place order button
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel totalValue = new JLabel("₱" + String.format("%.2f", getCartTotal() + 40.00));
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValue.setForeground(new Color(220, 20, 60));

        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        contentPanel.add(totalPanel);

        JButton placeOrderBtn = new JButton("PLACE ORDER");
        placeOrderBtn.setBackground(new Color(220, 20, 60));
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        placeOrderBtn.setBorderPainted(false);
        placeOrderBtn.setPreferredSize(new Dimension(0, 50));
        placeOrderBtn.addActionListener(e -> {
            // Process the order
            processCheckout();

            // Show confirmation
            JOptionPane.showMessageDialog(checkoutPanel, 
                "Your order has been placed successfully!", 
                "Order Confirmed", 
                JOptionPane.INFORMATION_MESSAGE);

            // Return to menu
            cardLayout.show(cardPanel, "menu");
        });
        contentPanel.add(placeOrderBtn);

        checkoutPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        return checkoutPanel;
    }


    public int getTotalItemCount() {
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }
    
    public int getSelectedItemCount() {
        return cartItems.stream()
                       .filter(CartItem::isSelected)
                       .mapToInt(CartItem::getQuantity)
                       .sum();
    }
    
    private int getOrCreateCart() {
        if (currentCartId != -1) return currentCartId;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT cart_id FROM carts WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getUserId());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                currentCartId = rs.getInt("cart_id");
                return currentCartId;
            }
            
            // If no cart exists, create one
            String insertSql = "INSERT INTO carts (user_id) VALUES (?)";
            stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, currentUser.getUserId());
            stmt.executeUpdate();
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                currentCartId = rs.getInt(1);
                return currentCartId;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error accessing cart: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return -1; // Error case
    }

    public void loadCartItems() {
        cartItems.clear();
        int cartId = getOrCreateCart();
        if (cartId == -1) return;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT p.product_id, p.name, p.price, ci.quantity " +
                        "FROM cart_items ci " +
                        "JOIN products p ON ci.product_id = p.product_id " +
                        "WHERE ci.cart_id = ?";
                        
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem item = new MenuItem(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    null
                );
                CartItem cartItem = new CartItem(item);
                cartItem.setQuantity(rs.getInt("quantity"));
                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error loading cart items: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToCart(MenuItem item) {
        // Check if item already exists in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getProductId() == item.getProductId()) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                updateCartItemQuantity(item.getProductId(), cartItem.getQuantity());
                return;
            }
        }
        
        // Add new item to cart
        CartItem newItem = new CartItem(item);
        cartItems.add(newItem);
        
        // Save to database
        saveCartItem(item.getProductId(), 1);
        updateCartTotal();
    }
    
    private void saveCartItem(int productId, int quantity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, getOrCreateCart());
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
        } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Error adding item to cart: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private void updateCartItemQuantity(int productId, int quantity) {
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
        conn = DBConnection.getConnection();
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, quantity);
        stmt.setInt(2, getOrCreateCart());
        stmt.setInt(3, productId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Error updating cart item: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private void removeCartItem(int productId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
        conn = DBConnection.getConnection();
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, getOrCreateCart());
        stmt.setInt(2, productId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Error removing item from cart: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private void processCheckout() {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction
        
        // 1. Create order
        String orderSql = "INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (?, NOW(), ?, 'Pending')";
        stmt = conn.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, currentUser.getUserId());
        stmt.setDouble(2, getCartTotal() + 105.00); // Add shipping cost
        stmt.executeUpdate();
        
        // Get the new order ID
        rs = stmt.getGeneratedKeys();
        int orderId = -1;
        if (rs.next()) {
            orderId = rs.getInt(1);
        } else {
            throw new SQLException("Failed to create order, no ID obtained.");
        }
        
        // 2. Add order items for selected products
        String orderItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        stmt = conn.prepareStatement(orderItemSql);
        
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getItem().getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getItem().getPrice());
                stmt.addBatch();
            }
        }
        stmt.executeBatch();
        
        // 3. Clear selected items from cart
        String clearCartSql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
        stmt = conn.prepareStatement(clearCartSql);
        
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                stmt.setInt(1, getOrCreateCart());
                stmt.setInt(2, item.getItem().getProductId());
                stmt.addBatch();
            }
        }
        stmt.executeBatch();
        
        // Commit the transaction
        conn.commit();
        
        // Update local cart items
        cartItems.removeIf(CartItem::isSelected);
        
    } catch (SQLException e) {
        // Rollback transaction on error
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(null, 
            "Error processing order: " + e.getMessage(), 
            "Checkout Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
}