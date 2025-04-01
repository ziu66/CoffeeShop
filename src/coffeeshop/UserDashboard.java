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
    
    public UserDashboard(User user) {
        this.currentUser = user;
        initializeUI();
        loadCartItems(); // Add this line to load saved cart
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
    
    String sql = "SELECT cart_id FROM carts WHERE user_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
         
        stmt.setInt(1, currentUser.getUserId());
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            currentCartId = rs.getInt("cart_id");
            return currentCartId;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    // If no cart exists, create one
    String insertSql = "INSERT INTO carts (user_id) VALUES (?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
         
        stmt.setInt(1, currentUser.getUserId());
        stmt.executeUpdate();
        
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            currentCartId = rs.getInt(1);
            return currentCartId;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return -1; // Error case
}

// 2. Method to load cart items from database
    private void loadCartItems() {
        cartItems.clear();
        int cartId = getOrCreateCart();
        if (cartId == -1) return;

        String sql = "SELECT p.product_id, p.name, p.price, ci.quantity " +
                    "FROM cart_items ci " +
                    "JOIN products p ON ci.product_id = p.product_id " +
                    "WHERE ci.cart_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

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
            updateCartCounter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Update your MenuItem class (inside UserDashboard)
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

    // Method to update cart counter
    public void updateCartCounter(int itemCount) {
        if (cartCounter != null) {
            cartCounter.setText(String.valueOf(itemCount));
            cartCounter.setVisible(itemCount > 0);
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
                        updateCartDisplay(cartFrame, itemCountLabel);
                    }
                });

                JLabel qtyLabel = new JLabel(String.valueOf(cartItem.quantity));

                JButton plusBtn = new JButton("+");
                plusBtn.addActionListener(e -> {
                    cartItem.quantity++;
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
                    cartItems.remove(cartItem);
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
        itemCountLabel.setText(getSelectedItemCount() + " items selected");
        cartFrame.setTitle("Shopping Cart (" + getSelectedItemCount() + ")");
        // You could also update the total label here if needed
    }
    
    private void calculateTotal() {
        cartTotal = 0.0;
        for (CartItem item : cartItems) {
            if (item.selected) {
                cartTotal += item.item.getPrice() * item.quantity;
            }
        }
        updateCartCounter();
    }

    private void updateCartCounter() {
        int count = cartItems.stream()
                     .filter(item -> item.selected)
                     .mapToInt(item -> item.quantity)
                     .sum();
        cartCounter.setText(String.valueOf(count));
        cartCounter.setVisible(count > 0);
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

        double subtotal = calculateSelectedItems();
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

            // Remove selected items
            cartItems.removeIf(item -> item.selected);
            calculateTotal();
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
        // After successful payment processing:
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, getOrCreateCart());
            stmt.executeUpdate();

            // Clear local cart
            cartItems.clear();
            updateCartCounter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double calculateSelectedItems() {
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            if (item.selected) {
                subtotal += item.item.getPrice() * item.quantity;
            }
        }
        return subtotal;
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load drinks
        List<MenuItem> drinks = getDatabaseDrinks();
        if (!drinks.isEmpty()) {
            panel.add(createCategorySection("Drinks", drinks));
            panel.add(Box.createRigidArea(new Dimension(0, 30)));
        } else {
            System.out.println("No drinks found in database"); // Debug
        }

        // Load food
        List<MenuItem> food = getDatabaseFood();
        if (!food.isEmpty()) {
            panel.add(createCategorySection("Food", food));
        } else {
            System.out.println("No food found in database"); // Debug
        }

        return panel;
    }

    private JPanel createMerchandiseContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<MenuItem> merchandise = getDatabaseMerchandise();
        if (!merchandise.isEmpty()) {
            panel.add(createCategorySection("Merchandise", merchandise));
        } else {
            panel.add(new JLabel("No merchandise available"));
            System.out.println("No merchandise found in database"); // Debug
        }

        return panel;
    }

    
    private JPanel createRewardsContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Rewards information
        JLabel rewardsLabel = new JLabel("Your Rewards");
        rewardsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rewardsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(rewardsLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel pointsLabel = new JLabel("Points: 150");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        pointsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(pointsLabel);

        return panel;
    }

    private JPanel createCategorySection(String title, List<MenuItem> items) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Items grid
        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        itemsGrid.setBackground(Color.WHITE);
        itemsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (MenuItem item : items) {
            itemsGrid.add(createItemCard(item));
        }

        sectionPanel.add(itemsGrid);
        return sectionPanel;
    }

    private JPanel createItemCard(MenuItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Item image - Loading from resources
        JLabel imageLabel = new JLabel();
        try {
            String imagePath = "/images/products/" + convertToImageName(item.getName());
            URL imageUrl = getClass().getResource(imagePath);

            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                System.out.println("Successfully loaded image: " + imagePath); // Debug
            } else {
                System.out.println("Image not found: " + imagePath); // Debug
                // Set a default placeholder image
                imageLabel.setIcon(createPlaceholderIcon());
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageLabel.setIcon(createPlaceholderIcon());
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(imageLabel);


        // Item name (removed ID display for cleaner UI)
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));

        // Item price
        JLabel priceLabel = new JLabel(String.format("$%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add to cart button
        JButton addButton = new JButton("Add to cart");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.setBackground(new Color(0, 102, 51));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addToCart(item));
        card.add(addButton);

        return card;
    }
    
    private String convertToImageName(String productName) {
    return productName.toLowerCase()
            .replace(" ", "-")
            .replace("'", "")
            .replace("(", "")
            .replace(")", "") + ".png";
}

    private ImageIcon createPlaceholderIcon() {
        // Create a simple placeholder image
        BufferedImage img = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 150, 150);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("No Image", 50, 75);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private String getLocalImagePath(String productName) {
        // Convert product name to image filename format
        String imageName = productName.toLowerCase()
            .replace(" ", "-")          // Replace spaces with hyphens
            .replace("'", "")           // Remove apostrophes
            .replace("(", "")           // Remove special chars
            .replace(")", "") + ".png";  // Assume JPG format

        // Get the image path (adjust this to your actual image location)
        String imagePath = "images/" + imageName;

        // Check if file exists
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            return imagePath;
        }

        // Try PNG if JPG not found
        imagePath = "images/" + imageName.replace(".jpg", ".png");
        imageFile = new File(imagePath);
        return imageFile.exists() ? imagePath : null;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(200, 200, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void addToCart(MenuItem item) {
        int cartId = getOrCreateCart();
        if (cartId == -1) return;

        // Check if item already in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.item.getProductId() == item.getProductId()) {
                updateCartItemQuantity(item.getProductId(), cartItem.quantity + 1);
                return;
            }
        }

        // Add new item to database
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            stmt.setInt(2, item.getProductId());
            stmt.executeUpdate();

            // Add to local cart
            cartItems.add(new CartItem(item));
            updateCartCounter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateCartItemQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeCartItem(productId);
            return;
        }

        // Update database
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, getOrCreateCart());
            stmt.setInt(3, productId);
            stmt.executeUpdate();

            // Update local cart
            for (CartItem cartItem : cartItems) {
                if (cartItem.item.getProductId() == productId) {
                    cartItem.quantity = newQuantity;
                    break;
                }
            }
            updateCartCounter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeCartItem(int productId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, getOrCreateCart());
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
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
    
    private List<MenuItem> getDatabaseDrinks() {
        return getProductsByCategory("DRINK");
    }

    private List<MenuItem> getDatabaseFood() {
        return getProductsByCategory("FOOD");
    }

    private List<MenuItem> getDatabaseMerchandise() {
        return getProductsByCategory("MERCHANDISE");
    }

    private List<MenuItem> getProductsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT product_id, name, price FROM products WHERE category = ? AND is_available = TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Loading " + category + " items:"); // Debug
            
            System.out.println("Database connection test:");
            try (Connection conn = DBConnection.getConnection()) {
                System.out.println("Connection successful!");
            } catch (SQLException e) {
                System.out.println("Connection failed:");
                e.printStackTrace();
            }

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                System.out.println(" - " + id + ": " + name + " ($" + price + ")"); // Debug

                items.add(new MenuItem(id, name, price));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to load " + category.toLowerCase() + " items",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        return items;
    }

    private static class MenuItem {
        private int productId;  // Now includes productId
        private String name;
        private double price;

        public MenuItem(int productId, String name, double price) {
            this.productId = productId;
            this.name = name;
            this.price = price;
        }

        // Getters
        public int getProductId() { return productId; }
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    }
}
