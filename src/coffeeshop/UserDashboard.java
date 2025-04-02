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
    private int currentActiveIndex = 0; // Add this as a class field
    
    private static class MenuItem {
        private int productId;
        private String name;
        private double price;
        private String description;

        public MenuItem(int productId, String name, double price, String description) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.description = description;
        }

        public int getProductId() { return productId; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getDescription() { return description; }
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
            // Special cart button styling
        if (navItems[i].equals("CART")) {
            try {
                // Fix the empty resource path
                ImageIcon cartIcon = new ImageIcon(getClass().getResource("/images/logo.png")); // Use your actual cart icon path
                Image scaledCartIcon = cartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                navButtons[i].setIcon(new ImageIcon(scaledCartIcon));
                navButtons[i].setHorizontalTextPosition(SwingConstants.LEFT);
                navButtons[i].setIconTextGap(8);

                // Make sure we still add the action listeners before continuing
                final int index = i;
                navButtons[i].addActionListener(e -> {
                    if (navItems[index].equals("CART")) {
                        showCart();
                    } else {
                        updateActiveButton(index);
                        handleNavigation(navItems[index]);
                    }
                });

                // Add hover effects
                // Modify the mouseEntered and mouseExited methods in your MouseAdapter:
                navButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        JButton source = (JButton)evt.getSource();
                        source.setForeground(new Color(200, 200, 200));

                        // Don't change border on hover for active button
                        if (source.getText().equals("CART")) {
                            source.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 1));
                        }
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        JButton source = (JButton)evt.getSource();
                        source.setForeground(Color.WHITE);

                        // Restore proper border based on active state
                        int index = -1;
                        for (int j = 0; j < navButtons.length; j++) {
                            if (navButtons[j] == source) {
                                index = j;
                                break;
                            }
                        }

                        if (index != -1) {
                            if (source == navButtons[currentActiveIndex] && !source.getText().equals("CART")) {
                                // Active button - keep white underline
                                source.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE));
                            } else {
                                // Inactive button - no underline
                                source.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                            }
                        }
                    }
                });

                // Add cart counter
                JPanel cartPanel = new JPanel(new BorderLayout());
                cartPanel.setOpaque(false);
                cartPanel.add(navButtons[i], BorderLayout.CENTER);
                cartPanel.add(cartCounter, BorderLayout.NORTH);
                navContent.add(cartPanel);
                continue;
            } catch (Exception e) {
                System.err.println("Couldn't load cart icon: " + e.getMessage());
                // Fall through to regular button setup if icon loading fails
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
                rs.getDouble("price"),
                null  // or "" if you prefer an empty string instead of null
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
        cartTotal = 0.0;
        int selectedCount = 0;
        int totalCount = 0;

        for (CartItem item : cartItems) {
            // Count all items for the counter
            totalCount += item.quantity;

            // Still track selected items for total price calculation
            if (item.selected) {
                cartTotal += item.item.getPrice() * item.quantity;
                selectedCount += item.quantity;
            }
        }

        // Update counter display with total count (not just selected)
        if (cartCounter != null) {
            cartCounter.setText(String.valueOf(totalCount));
            cartCounter.setVisible(totalCount > 0);
        }
    }

    // Method to show cart contents
    private void showCart() {
        JFrame cartFrame = new JFrame("Shopping Cart (" + cartItems.size() + ")");
        cartFrame.setSize(600, 700);
        cartFrame.setLocationRelativeTo(this);
        cartFrame.setResizable(false);

        // Use a modern font throughout
        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font subtitleFont = new Font("Segoe UI", Font.BOLD, 16);
        Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);

        // Use better colors
        Color accentColor = new Color(41, 128, 185); // Nice blue
        Color buttonColor = new Color(46, 204, 113); // Fresh green
        Color backgroundColor = new Color(245, 245, 245); // Light gray

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(backgroundColor);

        // Title panel with item count and nicer styling
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Your Cart", JLabel.LEFT);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(accentColor);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JLabel itemCountLabel = new JLabel(cartItems.size() + " items in cart");
        itemCountLabel.setFont(smallFont);
        titlePanel.add(itemCountLabel, BorderLayout.EAST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Cart items panel with better styling
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (cartItems.isEmpty()) {
            JPanel emptyCartPanel = new JPanel(new BorderLayout());
            emptyCartPanel.setBackground(Color.WHITE);
            emptyCartPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyLabel.setFont(subtitleFont);
            emptyLabel.setForeground(Color.GRAY);
            emptyCartPanel.add(emptyLabel, BorderLayout.CENTER);
            itemsPanel.add(emptyCartPanel);
        } else {
            for (CartItem cartItem : cartItems) {
                // Create a panel with more appropriate border size
                JPanel itemPanel = new JPanel(new BorderLayout(15, 0));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(8, 5, 8, 5) // Reduced padding
                ));

                // Checkbox for selection with better styling
                JCheckBox selectBox = new JCheckBox("", cartItem.selected);
                selectBox.setBackground(Color.WHITE);
                selectBox.addActionListener(e -> {
                    cartItem.selected = selectBox.isSelected();
                    updateCartDisplay(cartFrame, itemCountLabel);
                });

                // Item details with better layout
                JPanel detailsPanel = new JPanel(new BorderLayout(0, 5));
                detailsPanel.setBackground(Color.WHITE);

                // Name and description
                JLabel nameLabel = new JLabel(cartItem.item.getName());
                nameLabel.setFont(subtitleFont);

                JLabel descLabel = new JLabel("Size: Regular"); // Example attribute
                descLabel.setFont(smallFont);
                descLabel.setForeground(Color.GRAY);

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setBackground(Color.WHITE);
                textPanel.add(nameLabel);
                textPanel.add(descLabel);

                // Stylish price and quantity controls
                JPanel pricePanel = new JPanel(new BorderLayout(0, 10));
                pricePanel.setBackground(Color.WHITE);

                JLabel priceLabel = new JLabel("₱" + String.format("%.2f", cartItem.item.getPrice()));
                priceLabel.setFont(subtitleFont);
                priceLabel.setForeground(accentColor);

                // Better styled quantity controls
                JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                quantityPanel.setBackground(Color.WHITE);

                JButton minusBtn = new JButton("-");
                minusBtn.setFont(new Font("Arial", Font.BOLD, 14));
                minusBtn.setPreferredSize(new Dimension(30, 30));
                minusBtn.setFocusPainted(false);
                minusBtn.setBackground(Color.WHITE);
                minusBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                minusBtn.addActionListener(e -> {
                    if (cartItem.quantity > 1) {
                        cartItem.quantity--;
                        updateCartItemQuantity(cartItem.item.getProductId(), cartItem.quantity);
                        updateCartDisplay(cartFrame, itemCountLabel);
                    }
                });

                JLabel qtyLabel = new JLabel(String.valueOf(cartItem.quantity));
                qtyLabel.setFont(normalFont);
                qtyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                JButton plusBtn = new JButton("+");
                plusBtn.setFont(new Font("Arial", Font.BOLD, 14));
                plusBtn.setPreferredSize(new Dimension(30, 30));
                plusBtn.setFocusPainted(false);
                plusBtn.setBackground(Color.WHITE);
                plusBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
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

                // Remove button styling
                JButton removeBtn = new JButton("Remove");
                removeBtn.setFont(smallFont);
                removeBtn.setForeground(new Color(231, 76, 60)); // Red
                removeBtn.setContentAreaFilled(false);
                // Add a light border
                removeBtn.setBorderPainted(true);
                removeBtn.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60, 100), 1));
                removeBtn.setFocusPainted(false);
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                leftPanel.setBackground(Color.WHITE);
                leftPanel.add(selectBox, BorderLayout.WEST);
                leftPanel.add(textPanel, BorderLayout.CENTER);

                JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
                rightPanel.setBackground(Color.WHITE);
                rightPanel.add(pricePanel, BorderLayout.CENTER);
                rightPanel.add(removeBtn, BorderLayout.SOUTH);

                itemPanel.add(leftPanel, BorderLayout.WEST);
                itemPanel.add(rightPanel, BorderLayout.EAST);
                itemsPanel.add(itemPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Checkout panel with total and proceed button
        JPanel checkoutPanel = new JPanel(new BorderLayout(0, 15));
        checkoutPanel.setBackground(backgroundColor);
        checkoutPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 0, 0, 0)
        ));

        JLabel totalLabel = new JLabel("Selected Total: ₱" + String.format("%.2f", calculateSelectedTotal()));
        totalLabel.setFont(subtitleFont);

        // Styled checkout button
        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setFont(subtitleFont);
        checkoutBtn.setBackground(buttonColor);
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.setPreferredSize(new Dimension(200, 40));
        checkoutBtn.addActionListener(e -> {
            if (hasSelectedItems()) {
                cartFrame.setVisible(false); // Hide cart window
                showCheckout(cartFrame);
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
            cartFrame.setTitle("Shopping Cart (" + cartItems.size() + ")");
            itemCountLabel.setText(cartItems.size() + " items in cart");
            // Rest of method stays the same
        }
    
    private void showCheckout(JFrame parentFrame) {
        JFrame checkoutFrame = new JFrame("Checkout");
        checkoutFrame.setSize(600, 700);
        checkoutFrame.setLocationRelativeTo(parentFrame);
        checkoutFrame.setResizable(false);

        // Define fonts and colors
        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font subtitleFont = new Font("Segoe UI", Font.BOLD, 16);
        Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);

        Color accentColor = new Color(41, 128, 185); // Nice blue
        Color buttonColor = new Color(46, 204, 113); // Fresh green
        Color backgroundColor = new Color(245, 245, 245); // Light gray
        Color sectionBgColor = Color.WHITE;

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(backgroundColor);

        // Title with better styling
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Order Summary", JLabel.LEFT);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(accentColor);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);

        // Delivery/Pickup options with better styling
        JPanel deliveryPanel = new JPanel();
        deliveryPanel.setLayout(new BoxLayout(deliveryPanel, BoxLayout.Y_AXIS));
        deliveryPanel.setBackground(sectionBgColor);
        deliveryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel deliveryTitle = new JLabel("Delivery Method");
        deliveryTitle.setFont(subtitleFont);
        deliveryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        deliveryPanel.add(deliveryTitle);

        ButtonGroup deliveryGroup = new ButtonGroup();
        JRadioButton deliveryBtn = new JRadioButton("Delivery");
        deliveryBtn.setFont(normalFont);
        deliveryBtn.setBackground(sectionBgColor);
        JRadioButton pickupBtn = new JRadioButton("Pickup");
        pickupBtn.setFont(normalFont);
        pickupBtn.setBackground(sectionBgColor);
        deliveryGroup.add(deliveryBtn);
        deliveryGroup.add(pickupBtn);
        deliveryBtn.setSelected(true);

        // Address input with better styling
        JPanel addressPanel = new JPanel(new BorderLayout(0, 5));
        addressPanel.setBackground(sectionBgColor);
        addressPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel addressLabel = new JLabel("Delivery Address:");
        addressLabel.setFont(normalFont);

        JTextArea addressField = new JTextArea(currentUser.getAddress());
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setRows(3);
        addressField.setFont(normalFont);
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // Show/hide address based on selection
        deliveryBtn.addActionListener(e -> addressPanel.setVisible(true));
        pickupBtn.addActionListener(e -> addressPanel.setVisible(false));

        addressPanel.add(addressLabel, BorderLayout.NORTH);
        addressPanel.add(new JScrollPane(addressField), BorderLayout.CENTER);

        JPanel deliveryOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        deliveryOptionsPanel.setBackground(sectionBgColor);
        deliveryOptionsPanel.add(deliveryBtn);
        deliveryOptionsPanel.add(pickupBtn);

        deliveryPanel.add(deliveryOptionsPanel);
        deliveryPanel.add(addressPanel);

        // Order summary with better styling
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(sectionBgColor);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel orderSummaryTitle = new JLabel("Order Summary");
        orderSummaryTitle.setFont(subtitleFont);
        orderSummaryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        summaryPanel.add(orderSummaryTitle);

        double subtotal = calculateSelectedTotal();
        double shipping = 105.00; // Example shipping fee

        // Add summary items with better styling
        JPanel priceDetailsPanel = new JPanel(new GridLayout(3, 2, 0, 10));
        priceDetailsPanel.setBackground(sectionBgColor);

        JLabel subtotalLabel = new JLabel("Subtotal:");
        subtotalLabel.setFont(normalFont);
        JLabel subtotalValue = new JLabel("₱" + String.format("%.2f", subtotal));
        subtotalValue.setFont(normalFont);
        subtotalValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel shippingLabel = new JLabel("Shipping:");
        shippingLabel.setFont(normalFont);
        JLabel shippingValue = new JLabel("₱" + String.format("%.2f", shipping));
        shippingValue.setFont(normalFont);
        shippingValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel totalLabel = new JLabel("Total:");
        totalLabel.setFont(subtitleFont);
        JLabel totalValue = new JLabel("₱" + String.format("%.2f", subtotal + shipping));
        totalValue.setFont(subtitleFont);
        totalValue.setForeground(accentColor);
        totalValue.setHorizontalAlignment(SwingConstants.RIGHT);

        priceDetailsPanel.add(subtotalLabel);
        priceDetailsPanel.add(subtotalValue);
        priceDetailsPanel.add(shippingLabel);
        priceDetailsPanel.add(shippingValue);
        priceDetailsPanel.add(totalLabel);
        priceDetailsPanel.add(totalValue);

        summaryPanel.add(priceDetailsPanel);

        // Payment options with better styling
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(sectionBgColor);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel paymentTitle = new JLabel("Payment Method");
        paymentTitle.setFont(subtitleFont);
        paymentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        paymentPanel.add(paymentTitle);

        ButtonGroup paymentGroup = new ButtonGroup();

        JPanel paymentOptionsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        paymentOptionsPanel.setBackground(sectionBgColor);

        JRadioButton codBtn = new JRadioButton("Cash on Delivery");
        codBtn.setFont(normalFont);
        codBtn.setBackground(sectionBgColor);

        JRadioButton cardBtn = new JRadioButton("Credit/Debit Card");
        cardBtn.setFont(normalFont);
        cardBtn.setBackground(sectionBgColor);

        JRadioButton gcashBtn = new JRadioButton("GCash");
        gcashBtn.setFont(normalFont);
        gcashBtn.setBackground(sectionBgColor);

        paymentGroup.add(codBtn);
        paymentGroup.add(cardBtn);
        paymentGroup.add(gcashBtn);
        codBtn.setSelected(true);

        paymentOptionsPanel.add(codBtn);
        paymentOptionsPanel.add(cardBtn);
        paymentOptionsPanel.add(gcashBtn);
        paymentPanel.add(paymentOptionsPanel);

        // Add all sections to content panel
        contentPanel.add(deliveryPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(paymentPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Confirm order button with better styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);

        JButton confirmBtn = new JButton("Place Order");
        confirmBtn.setFont(subtitleFont);
        confirmBtn.setBackground(buttonColor);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setPreferredSize(new Dimension(180, 40));
        confirmBtn.addActionListener(e -> {
            selectedAddress = deliveryBtn.isSelected() ? addressField.getText() : "PICKUP";
            String paymentMethod = codBtn.isSelected() ? "COD" : 
                                 cardBtn.isSelected() ? "Credit Card" : "GCash";

            // Create a nicer confirmation dialog
            JPanel confirmPanel = new JPanel(new BorderLayout(0, 15));
            confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel confirmTitle = new JLabel("Order Confirmed!");
            confirmTitle.setFont(subtitleFont);
            confirmTitle.setForeground(new Color(39, 174, 96));
            confirmTitle.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 5, 10));
            detailsPanel.add(new JLabel("Delivery:"));
            detailsPanel.add(new JLabel(deliveryBtn.isSelected() ? "To Address" : "Store Pickup"));
            detailsPanel.add(new JLabel("Payment:"));
            detailsPanel.add(new JLabel(paymentMethod));
            detailsPanel.add(new JLabel("Total:"));
            detailsPanel.add(new JLabel("₱" + String.format("%.2f", subtotal + shipping)));

            confirmPanel.add(confirmTitle, BorderLayout.NORTH);
            confirmPanel.add(detailsPanel, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                checkoutFrame, 
                confirmPanel,
                "Order Confirmed", 
                JOptionPane.PLAIN_MESSAGE
            );

            // Process the checkout in database
            processCheckout();

            // Close windows
            checkoutFrame.dispose();
            parentFrame.dispose();
        });

        buttonPanel.add(confirmBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

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
        currentActiveIndex = activeIndex;

        for (int i = 0; i < navButtons.length; i++) {
            if (i == activeIndex && !navButtons[i].getText().equals("CART")) {
                navButtons[i].setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE));
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

        // Categories based on your database
        String[] categories = {"DRINK", "MEAL"};

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

        // Map database category to display name
        String displayCategory;
        switch(category) {
            case "DRINK":
                displayCategory = "Drinks";
                break;
            case "MEAL":
                displayCategory = "Meals";
                break;
            case "MERCHANDISE":
                displayCategory = "Shop Merchandise";
                break;
            default:
                displayCategory = category;
        }

        // Category header
        JLabel categoryLabel = new JLabel(displayCategory);
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
        JLabel imageLabel = new JLabel();
            try {
                String imagePath = null;
                String productName = item.getName().toLowerCase();

                // Assign specific image resources for the products you have
                if (productName.contains("cappuccino")) {
                    imagePath = "/images/cappuccino.png";
                } else if (productName.contains("espresso")) {
                    imagePath = "/images/espresso.png";
                } else if (productName.contains("latte")) {
                    imagePath = "/images/latte.png";
                } else {
                    // Default resource
                    imagePath = "";
                }

                // Load the image from resources
                java.net.URL imageURL = getClass().getResource(imagePath);
                if (imageURL != null) {
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel = new JLabel(new ImageIcon(scaledImage));
                } else {
                    System.err.println("Image resource not found: " + imagePath);
                    // Use a placeholder or default icon
                    imageLabel = new JLabel("No Image");
                }
            } catch (Exception e) {
                System.err.println("Could not load image: " + e.getMessage());
                imageLabel = new JLabel("No Image");
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
            String sql = "SELECT product_id, name, price, description FROM products WHERE category = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new MenuItem(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description")
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

        // Merchandise items from database - using the correct category "MERCHANDISE"
        List<MenuItem> merchandiseItems = getMenuItemsByCategory("MERCHANDISE");
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