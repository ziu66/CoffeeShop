/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JScrollPane scrollPane;
    private User currentUser;
    private JPanel contentPanel;
    private JButton[] navButtons;
    
    private List<CartItem> cartItems = new ArrayList<>();
    private double cartTotal = 0.0;
    private JLabel cartCounter;
    private String selectedAddress;
    private int currentCartId = -1;
    
    // Class to represent menu items
    private static class MenuItem {
        private int productId;
        private String name;
        private double price;

        public MenuItem(int productId, String name, double price) {
            this.productId = productId;
            this.name = name;
            this.price = price;
        }

        public int getProductId() { return productId; }
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    
    // Class to represent items in the cart
    private class CartItem {
        MenuItem item;
        int quantity;
        boolean selected;

        public CartItem(MenuItem item) {
            this.item = item;
            this.quantity = 1;
            this.selected = true;
        }
    }
    
    public UserDashboard(User user) {
        this.currentUser = user;
        initializeUI();
        loadCartItems(); // Load saved cart
    }

    private void initializeUI() {
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create header panel with promotional image
        headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create initial content panel
        contentPanel = createMenuContent();
        
        // Make it scrollable
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Frame setup
        setTitle("But First, Coffee - " + currentUser.getFullName());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(1200, 250));

        // 1. Top Welcome Message (Optional)
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // 2. Animated GIF (Center)
        JPanel gifContainer = new JPanel(new BorderLayout());
        gifContainer.setBackground(Color.BLACK);

        try {
            File gifFile = new File("C:\\Users\\sophi\\Downloads\\images\\header-backgroundd.gif");
            if (gifFile.exists()) {
                ImageIcon gifIcon = new ImageIcon(gifFile.getAbsolutePath());
                Image img = gifIcon.getImage();
                gifIcon = new ImageIcon(img);
                JLabel gifLabel = new JLabel(gifIcon);
                gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
                gifContainer.add(gifLabel, BorderLayout.CENTER);
            } else {
                System.err.println("File does not exist: " + gifFile.getAbsolutePath());
                throw new IOException("GIF file not found");
            }
        } catch (Exception e) {
            System.err.println("GIF Error: " + e.getMessage());
            JLabel errorLabel = new JLabel("Header Image Missing", SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            gifContainer.add(errorLabel, BorderLayout.CENTER);
        }
        panel.add(gifContainer, BorderLayout.CENTER);

        // 3. Navigation Bar (Bottom)
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.BLACK);
        navBar.setPreferredSize(new Dimension(1200, 50));

        // Left-aligned content (logo + buttons)
        JPanel navContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        navContent.setBackground(Color.BLACK);

        // Logo
        URL logoUrl = getClass().getResource("/images/logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image scaledLogo = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            navContent.add(logoLabel);
        } else {
            JLabel missingLogo = new JLabel("[LOGO]");
            missingLogo.setForeground(Color.WHITE);
            navContent.add(missingLogo);
        }

        // Navigation Buttons with CART
        String[] navItems = {"MENU", "MERCHANDISE", "REWARDS", "CART"};
        navButtons = new JButton[navItems.length];

        // Cart item counter
        cartCounter = new JLabel("0");
        cartCounter.setFont(new Font("Arial", Font.BOLD, 10));
        cartCounter.setForeground(Color.WHITE);
        cartCounter.setBackground(new Color(255, 102, 102));
        cartCounter.setOpaque(true);
        cartCounter.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        cartCounter.setVisible(false);
        
        for (int i = 0; i < navItems.length; i++) {
            navButtons[i] = new JButton(navItems[i]);
            navButtons[i].setForeground(Color.WHITE);
            navButtons[i].setBackground(Color.BLACK);
            navButtons[i].setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            navButtons[i].setFont(new Font("Arial", Font.BOLD, 14));
            navButtons[i].setContentAreaFilled(false);
            navButtons[i].setFocusPainted(false);

            // Special cart button styling
            if (navItems[i].equals("CART")) {
                try {
                    ImageIcon cartIcon = new ImageIcon(getClass().getResource("/images/cart-icon.png"));
                    Image scaledCartIcon = cartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    navButtons[i].setIcon(new ImageIcon(scaledCartIcon));
                    navButtons[i].setHorizontalTextPosition(SwingConstants.LEFT);
                    navButtons[i].setIconTextGap(8);

                    // Add cart counter
                    JPanel cartPanel = new JPanel(new BorderLayout());
                    cartPanel.setOpaque(false);
                    cartPanel.add(navButtons[i], BorderLayout.CENTER);
                    cartPanel.add(cartCounter, BorderLayout.NORTH);
                    navContent.add(cartPanel);
                    continue;
                } catch (Exception e) {
                    System.err.println("Couldn't load cart icon: " + e.getMessage());
                }
            }

            // Hover effects
            navButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    JButton source = (JButton)evt.getSource();
                    source.setForeground(new Color(200, 200, 200));
                    if (source.getText().equals("CART")) {
                        source.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 1));
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    JButton source = (JButton)evt.getSource();
                    source.setForeground(Color.WHITE);
                    source.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                }
            });

            final int index = i;
            navButtons[i].addActionListener(e -> {
                if (navItems[index].equals("CART")) {
                    showCart();
                } else {
                    updateActiveButton(index);
                    handleNavigation(navItems[index]);
                }
            });

            navContent.add(navButtons[i]);
        }

        // Set initial active button
        updateActiveButton(0);

        // Right-aligned components (welcome + logout)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Welcome Label
        JLabel navWelcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "! ");
        navWelcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        navWelcomeLabel.setForeground(new Color(220, 220, 220));
        rightPanel.add(navWelcomeLabel);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setForeground(new Color(200, 200, 200));
        logoutButton.setBackground(new Color(70, 70, 70));
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effects
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setForeground(Color.WHITE);
                logoutButton.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setForeground(new Color(200, 200, 200));
                logoutButton.setBackground(new Color(70, 70, 70));
            }
        });

        logoutButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        rightPanel.add(logoutButton);

        // Add components to navBar
        navBar.add(navContent, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);

        panel.add(navBar, BorderLayout.SOUTH);
        return panel;
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
            JOptionPane.showMessageDialog(this, 
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

    // Method to load cart items from database
    private void loadCartItems() {
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
                    rs.getDouble("price")
                );
                CartItem cartItem = new CartItem(item);
                cartItem.quantity = rs.getInt("quantity");
                cartItems.add(cartItem);
            }
            updateCartTotalAndCounter();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
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

    // Method to update cart counter display
    private void updateCartTotalAndCounter() {
        // Calculate total
        cartTotal = 0.0;
        int count = 0;
        
        for (CartItem item : cartItems) {
            if (item.selected) {
                cartTotal += item.item.getPrice() * item.quantity;
                count += item.quantity;
            }
        }
        
        // Update counter display
        if (cartCounter != null) {
            cartCounter.setText(String.valueOf(count));
            cartCounter.setVisible(count > 0);
        }
    }

    // Method to show cart contents
    private void showCart() {
        JFrame cartFrame = new JFrame("Shopping Cart (" + getSelectedItemCount() + ")");
        cartFrame.setSize(600, 700);
        cartFrame.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title panel with item count
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Your Cart", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JLabel itemCountLabel = new JLabel(getSelectedItemCount() + " items selected");
        itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titlePanel.add(itemCountLabel, BorderLayout.EAST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Cart items panel with checkboxes and quantities
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        if (cartItems.isEmpty()) {
            itemsPanel.add(new JLabel("Your cart is empty", SwingConstants.CENTER));
        } else {
            for (CartItem cartItem : cartItems) {
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

                // Checkbox for selection
                JCheckBox selectBox = new JCheckBox("", cartItem.selected);
                selectBox.addActionListener(e -> {
                    cartItem.selected = selectBox.isSelected();
                    updateCartDisplay(cartFrame, itemCountLabel);
                });

                // Item details
                JPanel detailsPanel = new JPanel(new BorderLayout());

                // Name and description
                JLabel nameLabel = new JLabel(cartItem.item.getName());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel descLabel = new JLabel("Size: Regular"); // Example attribute
                descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.add(nameLabel);
                textPanel.add(descLabel);

                // Price and quantity controls
                JPanel pricePanel = new JPanel(new BorderLayout());

                JLabel priceLabel = new JLabel("$" + String.format("%.2f", cartItem.item.getPrice()));
                priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                JButton minusBtn = new JButton("-");
                minusBtn.addActionListener(e -> {
                    if (cartItem.quantity > 1) {
                        cartItem.quantity--;
                        updateCartItemQuantity(cartItem.item.getProductId(), cartItem.quantity);
                        updateCartDisplay(cartFrame, itemCountLabel);
                    }
                });

                JLabel qtyLabel = new JLabel(String.valueOf(cartItem.quantity));

                JButton plusBtn = new JButton("+");
                plusBtn.addActionListener(e -> {
                    cartItem.quantity++;
                    updateCartItemQuantity(cartItem.item.getProductId(), cartItem.quantity);
                    updateCartDisplay(cartFrame, itemCountLabel);
                });

                quantityPanel.add(minusBtn);
                quantityPanel.add(qtyLabel);
                quantityPanel.add(plusBtn);

                pricePanel.add(priceLabel, BorderLayout.NORTH);
                pricePanel.add(quantityPanel, BorderLayout.SOUTH);

                // Remove button
                JButton removeBtn = new JButton("Remove");
                removeBtn.addActionListener(e -> {
                    removeCartItem(cartItem.item.getProductId());
                    cartItems.remove(cartItem);
                    updateCartTotalAndCounter();
                    updateCartDisplay(cartFrame, itemCountLabel);
                    if (cartItems.isEmpty()) {
                        cartFrame.dispose();
                    }
                });

                // Layout components
                JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
                leftPanel.add(selectBox, BorderLayout.WEST);
                leftPanel.add(textPanel, BorderLayout.CENTER);

                JPanel rightPanel = new JPanel(new BorderLayout());
                rightPanel.add(pricePanel, BorderLayout.CENTER);
                rightPanel.add(removeBtn, BorderLayout.SOUTH);

                itemPanel.add(leftPanel, BorderLayout.WEST);
                itemPanel.add(rightPanel, BorderLayout.EAST);
                itemsPanel.add(itemPanel);
                itemsPanel.add(new JSeparator());
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Checkout panel with total and proceed button
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel totalLabel = new JLabel("Selected Total: $" + String.format("%.2f", calculateSelectedTotal()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // PROCEED TO CHECKOUT BUTTON - CONNECTED TO showCheckout()
        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutBtn.setBackground(new Color(76, 175, 80)); // Green
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> {
            if (hasSelectedItems()) {
                cartFrame.setVisible(false); // Hide cart window
                showCheckout(cartFrame); // THIS IS THE CONNECTION
            } else {
                JOptionPane.showMessageDialog(cartFrame,
                    "Please select at least one item to checkout",
                    "No Items Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        checkoutPanel.add(totalLabel, BorderLayout.WEST);
        checkoutPanel.add(checkoutBtn, BorderLayout.EAST);
        mainPanel.add(checkoutPanel, BorderLayout.SOUTH);

        cartFrame.add(mainPanel);
        cartFrame.setVisible(true);
    }

    // Helper methods used in showCart()
    private int getSelectedItemCount() {
        return cartItems.stream()
                       .filter(item -> item.selected)
                       .mapToInt(item -> item.quantity)
                       .sum();
    }

    private double calculateSelectedTotal() {
        return cartItems.stream()
                       .filter(item -> item.selected)
                       .mapToDouble(item -> item.item.getPrice() * item.quantity)
                       .sum();
    }

    private boolean hasSelectedItems() {
        return cartItems.stream().anyMatch(item -> item.selected);
    }

    private void updateCartDisplay(JFrame cartFrame, JLabel itemCountLabel) {
        int selectedCount = getSelectedItemCount();
        itemCountLabel.setText(selectedCount + " items selected");
        cartFrame.setTitle("Shopping Cart (" + selectedCount + ")");
        updateCartTotalAndCounter();
    }
    
    private void showCheckout(JFrame parentFrame) {
        JFrame checkoutFrame = new JFrame("Checkout");
        checkoutFrame.setSize(600, 700);
        checkoutFrame.setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Order Summary", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Delivery/Pickup options
        JPanel deliveryPanel = new JPanel();
        deliveryPanel.setLayout(new BoxLayout(deliveryPanel, BoxLayout.Y_AXIS));
        deliveryPanel.setBorder(BorderFactory.createTitledBorder("Delivery Method"));

        ButtonGroup deliveryGroup = new ButtonGroup();
        JRadioButton deliveryBtn = new JRadioButton("Delivery");
        JRadioButton pickupBtn = new JRadioButton("Pickup");
        deliveryGroup.add(deliveryBtn);
        deliveryGroup.add(pickupBtn);
        deliveryBtn.setSelected(true);

        // Address input
        JPanel addressPanel = new JPanel(new BorderLayout());
        JLabel addressLabel = new JLabel("Delivery Address:");
        JTextArea addressField = new JTextArea(currentUser.getAddress());
        addressField.setLineWrap(true);
        addressField.setRows(3);

        // Show/hide address based on selection
        deliveryBtn.addActionListener(e -> addressPanel.setVisible(true));
        pickupBtn.addActionListener(e -> addressPanel.setVisible(false));

        addressPanel.add(addressLabel, BorderLayout.NORTH);
        addressPanel.add(new JScrollPane(addressField), BorderLayout.CENTER);

        deliveryPanel.add(deliveryBtn);
        deliveryPanel.add(pickupBtn);
        deliveryPanel.add(addressPanel);

        // Order summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));

        double subtotal = calculateSelectedTotal();
        double shipping = 105.00; // Example shipping fee

        summaryPanel.add(new JLabel("Subtotal: P" + String.format("%.2f", subtotal)));
        summaryPanel.add(new JLabel("Shipping: P" + String.format("%.2f", shipping)));
        summaryPanel.add(new JSeparator());
        summaryPanel.add(new JLabel("Total: P" + String.format("%.2f", subtotal + shipping)));

        // Payment options
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));

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

        // Confirm order button
        JButton confirmBtn = new JButton("Place Order");
        confirmBtn.addActionListener(e -> {
            selectedAddress = deliveryBtn.isSelected() ? addressField.getText() : "PICKUP";
            String paymentMethod = codBtn.isSelected() ? "COD" : 
                                 cardBtn.isSelected() ? "Credit Card" : "GCash";

            // Process order here
            JOptionPane.showMessageDialog(checkoutFrame, 
                "Order confirmed!\n" +
                "Delivery: " + (deliveryBtn.isSelected() ? selectedAddress : "Store Pickup") + "\n" +
                "Payment: " + paymentMethod + "\n" +
                "Total: P" + String.format("%.2f", subtotal + shipping),
                "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

            // Process the checkout in database
            processCheckout();
            
            // Close windows
            checkoutFrame.dispose();
            parentFrame.dispose();
        });

        // Layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(deliveryPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(summaryPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(paymentPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(confirmBtn, BorderLayout.SOUTH);

        checkoutFrame.add(mainPanel);
        checkoutFrame.setVisible(true);
    }
   
    private void processCheckout() {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. Create order record
            // Implement order creation logic here
            
            // 2. Remove items from cart
            String sql = "DELETE FROM cart_items WHERE cart_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, getOrCreateCart());
            stmt.executeUpdate();
            
            // Commit transaction
            conn.commit();
            
            // Clear local cart
            cartItems.clear();
            updateCartTotalAndCounter();
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this,
                "Error processing order: " + e.getMessage(),
                "Checkout Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
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

    private void updateActiveButton(int activeIndex) {
        for (int i = 0; i < navButtons.length; i++) {
            if (i == activeIndex) {
                navButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 15, 5, 15),
                    BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE) // Changed to white
                ));
            } else {
                navButtons[i].setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            }
        }
    }

    private void handleNavigation(String page) {
        // Remove current content
        mainPanel.remove(scrollPane);
        
        // Create new content based on selection
        switch (page) {
            case "MENU":
                contentPanel = createMenuContent();
                break;
            case "MERCHANDISE":
                contentPanel = createMerchandiseContent();
                break;
            case "REWARDS":
                contentPanel = createRewardsContent();
                break;
        }
        
        // Update scroll pane with new content
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Add back to main panel and refresh
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createMenuContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Add section header
        JLabel headerLabel = new JLabel("Our Menu");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(20));

        // Categories
        String[] categories = {"Hot Coffee", "Cold Coffee", "Tea", "Pastries"};
        
        // Fetch menu items from database
        for (String category : categories) {
            JPanel categoryPanel = createCategoryPanel(category, getMenuItemsByCategory(category));
            panel.add(categoryPanel);
            panel.add(Box.createVerticalStrut(30));
        }

        return panel;
    }

    private JPanel createCategoryPanel(String category, List<MenuItem> items) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Category header
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(categoryLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Add divider
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(15));
        
        // Grid of items
        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        itemsGrid.setBackground(Color.WHITE);
        
        if (items.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No items available in this category");
            noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noItemsLabel);
        } else {
            for (MenuItem item : items) {
                JPanel itemPanel = createItemPanel(item);
                itemsGrid.add(itemPanel);
            }
        }
        
        panel.add(itemsGrid);
        return panel;
    }
    
    private JPanel createItemPanel(MenuItem item) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Try to load product image
        JLabel imageLabel = new JLabel(new ImageIcon(getClass().getResource("/images/placeholder.png")));
        try {
            BufferedImage image = ImageIO.read(new File("C:\\Users\\sophi\\Downloads\\images\\products\\" + 
                                              item.getProductId() + ".jpg"));
            if (image != null) {
                Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imageLabel = new JLabel(new ImageIcon(scaledImage));
            }
        } catch (IOException e) {
            System.err.println("Could not load image for product ID " + item.getProductId());
        }
        
        // Center the image
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(imageLabel);
        
        // Item info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel priceLabel = new JLabel("P" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        
        // Add to cart button
        JButton addButton = new JButton("Add to Cart");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(76, 175, 80)); // Green
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addToCart(item));
        
        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT product_id, name, price FROM products WHERE category = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(new MenuItem(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading menu items: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return items;
    }
    
    private void addToCart(MenuItem item) {
        // Check if item already exists in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.item.getProductId() == item.getProductId()) {
                cartItem.quantity++;
                updateCartItemQuantity(item.getProductId(), cartItem.quantity);
                JOptionPane.showMessageDialog(this, 
                    "Added another " + item.getName() + " to your cart.",
                    "Added to Cart", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateCartTotalAndCounter();
                return;
            }
        }
        
        // Add new item to cart
        CartItem newItem = new CartItem(item);
        cartItems.add(newItem);
        
        // Save to database
        saveCartItem(item.getProductId(), 1);
        
        JOptionPane.showMessageDialog(this, 
            item.getName() + " added to cart.",
            "Added to Cart", 
            JOptionPane.INFORMATION_MESSAGE);
        
        updateCartTotalAndCounter();
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
            JOptionPane.showMessageDialog(this, 
                "Error saving cart item: " + e.getMessage(), 
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
            JOptionPane.showMessageDialog(this, 
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
            JOptionPane.showMessageDialog(this, 
                "Error removing cart item: " + e.getMessage(), 
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
    
    private JPanel createMerchandiseContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Add section header
        JLabel headerLabel = new JLabel("Merchandise");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Merchandise description
        JLabel descLabel = new JLabel("<html>Explore our exclusive collection of merchandise. " +
                                     "Wear your favorite coffee brand with pride!</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(30));
        
        // Merchandise items from database
        List<MenuItem> merchandiseItems = getMerchandiseItems();
        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        itemsGrid.setBackground(Color.WHITE);
        
        if (merchandiseItems.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No merchandise available at this time");
            noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noItemsLabel);
        } else {
            for (MenuItem item : merchandiseItems) {
                JPanel itemPanel = createItemPanel(item);
                itemsGrid.add(itemPanel);
            }
        }
        
        panel.add(itemsGrid);
        return panel;
    }
    
    private List<MenuItem> getMerchandiseItems() {
        List<MenuItem> items = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT product_id, name, price FROM products WHERE category = 'Merchandise'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(new MenuItem(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading merchandise items: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return items;
    }
    
    private JPanel createRewardsContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Add section header
        JLabel headerLabel = new JLabel("Rewards Program");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // User points
        int userPoints = getUserPoints();
        JLabel pointsLabel = new JLabel("Your current points: " + userPoints);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pointsLabel);
        panel.add(Box.createVerticalStrut(30));
        
        // Program description
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setBackground(new Color(245, 245, 245));
        descPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel howItWorksLabel = new JLabel("How It Works:");
        howItWorksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JTextArea descText = new JTextArea(
            "• Earn 1 point for every P50 spent\n" +
            "• Redeem 100 points for a free regular drink\n" +
            "• Redeem 200 points for a free premium drink\n" +
            "• Special birthday reward - double points all month!"
        );
        descText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descText.setBackground(new Color(245, 245, 245));
        descText.setEditable(false);
        descText.setLineWrap(true);
        descText.setWrapStyleWord(true);
        
        descPanel.add(howItWorksLabel);
        descPanel.add(Box.createVerticalStrut(10));
        descPanel.add(descText);
        
        panel.add(descPanel);
        panel.add(Box.createVerticalStrut(30));
        
        // Available rewards
        JLabel rewardsLabel = new JLabel("Available Rewards");
        rewardsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rewardsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(rewardsLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Rewards grid
        JPanel rewardsGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        rewardsGrid.setBackground(Color.WHITE);
        
        String[][] rewards = {
            {"Free Regular Coffee", "100"},
            {"Free Premium Coffee", "200"},
            {"50% Off Pastry", "75"},
            {"Free Merchandise Item", "500"}
        };
        
        for (String[] reward : rewards) {
            JPanel rewardPanel = createRewardPanel(reward[0], Integer.parseInt(reward[1]), userPoints);
            rewardsGrid.add(rewardPanel);
        }
        
        panel.add(rewardsGrid);
        
        return panel;
    }
    
    private int getUserPoints() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int points = 0;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT points FROM users WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getUserId());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                points = rs.getInt("points");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user points: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return points;
    }
    
    private JPanel createRewardPanel(String rewardName, int pointsCost, int userPoints) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel nameLabel = new JLabel(rewardName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel pointsLabel = new JLabel(pointsCost + " points");
        pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton redeemButton = new JButton("Redeem");
        redeemButton.setEnabled(userPoints >= pointsCost);
        
        if (!redeemButton.isEnabled()) {
            redeemButton.setToolTipText("Not enough points");
        }
        
        redeemButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to redeem " + rewardName + " for " + pointsCost + " points?",
                "Confirm Redemption",
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                // Process reward redemption
                JOptionPane.showMessageDialog(this,
                    "You have successfully redeemed " + rewardName + ".\n" +
                    "Your reward code: " + generateRewardCode(),
                    "Redemption Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(pointsLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(redeemButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private String generateRewardCode() {
        // Simple random code generator
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            int index = (int)(Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        
        return code.toString();
    }
}