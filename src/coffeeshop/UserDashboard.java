/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JScrollPane scrollPane;
    private final User currentUser;
    private JPanel contentPanel;
    private JButton[] navButtons;
    private CardLayout cardLayout;
    private JLabel cartCounter;
    private String selectedAddress;
    private int currentActiveIndex = 0;
    private JPanel cardPanel; // This will hold all your different "pages"
    
    private final CartManager cartManager;
    
    public UserDashboard(User user) {
        this.currentUser = user;
        this.cartManager = new CartManager(user);
        initializeUI();
    }

    private void initializeUI() {
        // 1. Create main panel - this will be the root container
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 40, 40)); // Dark background to match admin theme

        // 2. Create card panel with card layout - this will hold all views
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(40, 40, 40)); // Dark background

        // 3. Create all content panels FIRST (as separate instances)
        JPanel menuPanel = createMenuContent();
        JPanel merchPanel = createMerchandiseContent();
        JPanel rewardsPanel = createRewardsContent();
        JPanel cartPanel = createCartContent();

        // 4. Add content panels to card panel (NOT to mainPanel)
        cardPanel.add(menuPanel, "menu");
        cardPanel.add(merchPanel, "merchandise");
        cardPanel.add(rewardsPanel, "rewards");
        cardPanel.add(cartPanel, "cart");

        // Create checkout panel and add it to cardPanel directly
        JPanel checkoutPanel = cartManager.createCheckoutPanel(cardPanel, cardLayout);
        cardPanel.add(checkoutPanel, "checkout");

        // 5. Add card panel to main panel
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // 6. Create and add header panel
        headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 7. Frame setup - only set content pane once
        setContentPane(mainPanel);
        setTitle("But First, Coffee - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private JPanel createCartContent() {
        return cartManager.createCartPanel(cardPanel, cardLayout);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(1200, 250));

        // 1. Top Welcome Message (Optional)
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(218, 165, 32)); // Gold color to match admin theme
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
        cartCounter.setForeground(Color.BLACK);
        cartCounter.setBackground(new Color(218, 165, 32)); // Gold color
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

            if (navItems[i].equals("CART")) {
                try {
                    URL cartIconUrl = getClass().getResource("/images/cart-icon.png");
                    if (cartIconUrl != null) {
                        ImageIcon icon = new ImageIcon(cartIconUrl);
                        navButtons[i].setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                    }
                } catch (Exception e) {
                    System.err.println("Couldn't load cart icon: " + e.getMessage());
                }

                navButtons[i].addActionListener(e -> cardLayout.show(cardPanel, "cart"));

                JPanel cartPanel = new JPanel(new BorderLayout());
                cartPanel.setOpaque(false);
                cartPanel.add(navButtons[i], BorderLayout.CENTER);
                cartPanel.add(cartCounter, BorderLayout.NORTH);
                navContent.add(cartPanel);
                continue;
            }

            // Hover effects
            navButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    JButton source = (JButton)evt.getSource();
                    source.setForeground(new Color(218, 165, 32)); // Gold color on hover
                    if (source.getText().equals("CART")) {
                        source.setBorder(BorderFactory.createLineBorder(new Color(218, 165, 32), 1));
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
                    cardLayout.show(cardPanel, "cart");
                    updateActiveButton(index);
                } else {
                    updateActiveButton(index);
                    // Map navigation items to card names
                    String cardName = navItems[index].toLowerCase();
                    cardLayout.show(cardPanel, cardName);
                }
            });

            navContent.add(navButtons[i]);
        }

        updateActiveButton(0);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel navWelcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "! ");
        navWelcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        navWelcomeLabel.setForeground(new Color(220, 220, 220));
        rightPanel.add(navWelcomeLabel);

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

        navBar.add(navContent, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);

        panel.add(navBar, BorderLayout.SOUTH);
        return panel;
    }

    private void updateActiveButton(int activeIndex) {
        currentActiveIndex = activeIndex;

        for (int i = 0; i < navButtons.length; i++) {
            if (i == activeIndex && !navButtons[i].getText().equals("CART")) {
                navButtons[i].setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(218, 165, 32))); // Gold underline
            } else {
                navButtons[i].setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            }
        }
    }

    private JPanel createMenuContent() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 40, 40)); // Dark background

        // Header panel (stays at the top)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(40, 40, 40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        // Add section header
        JLabel headerLabel = new JLabel("Our Menu");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(headerLabel);

        // Content panel that will be scrollable (contains all categories)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // Categories based on your database
        String[] categories = {"DRINK", "MEAL"};

        for (String category : categories) {
            List<MenuItem> items = getMenuItemsByCategory(category);
            JPanel categoryPanel = createCategoryPanel(category, items);
            contentPanel.add(categoryPanel);
            contentPanel.add(Box.createVerticalStrut(30));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null); // Remove border
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createCategoryPanel(String category, List<MenuItem> items) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(40, 40, 40)); // Dark background

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(40, 40, 40));

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

        JLabel categoryLabel = new JLabel(displayCategory);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        categoryLabel.setForeground(new Color(218, 165, 32)); // Gold color
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(categoryLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        separator.setForeground(new Color(70, 70, 70)); // Darker separator
        headerPanel.add(separator);
        headerPanel.add(Box.createVerticalStrut(15));

        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        itemsGrid.setBackground(new Color(40, 40, 40)); // Dark background

        if (items.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No items available in this category");
            noItemsLabel.setForeground(Color.WHITE);
            noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsGrid.add(noItemsLabel);
        } else {
            for (MenuItem item : items) {
                JPanel itemPanel = createItemPanel(item);
                itemsGrid.add(itemPanel);
            }
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(itemsGrid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createItemPanel(MenuItem item) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel imageLabel = new JLabel();
        try {
            String imagePath = null;
            String productName = item.getName().toLowerCase();

            if (productName.contains("cappuccino")) {
                imagePath = "/images/cappuccino.png";
            } else if (productName.contains("espresso")) {
                imagePath = "/images/espresso.png";
            } else if (productName.contains("latte")) {
                imagePath = "/images/latte.png";
            } else if (productName.contains("coffee")) {
                imagePath = "";
            } else if (productName.contains("tea")) {
                imagePath = "/images/tea.png";
            } else if (productName.contains("sandwich")) {
                imagePath = "/images/sandwich.png";
            } else if (productName.contains("cake") || productName.contains("pastry")) {
                imagePath = "/images/cake.png";
            } else if (productName.contains("breakfast")) {
                imagePath = "";
            } else if (productName.contains("salad")) {
                imagePath = "/images/salad.png";
            } else if (productName.contains("mug") || productName.contains("tumbler")) {
                imagePath = "/images/mug.png";
            } else if (productName.contains("t-shirt") || productName.contains("shirt")) {
                imagePath = "/images/tshirt.png";
            } else {
                // Default image based on category
                if (productName.contains("drink")) {
                    imagePath = "";
                } else if (productName.contains("meal")) {
                    imagePath = "";
                } else {
                    imagePath = "";
                }
            }

            java.net.URL imageURL = getClass().getResource(imagePath);
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel = new JLabel(new ImageIcon(scaledImage));
            } else {
                System.err.println("Image resource not found: " + imagePath);
                // Create placeholder image
                imageLabel = createPlaceholderImage(150, 150, item.getName().substring(0, 1).toUpperCase());
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + e.getMessage());
            // Create placeholder with first letter of product name
            imageLabel = createPlaceholderImage(150, 150, item.getName().substring(0, 1).toUpperCase());
        }

        // Center the image
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(50, 50, 50));
        imagePanel.add(imageLabel);

        // Item info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 50));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("P" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(new Color(218, 165, 32)); // Gold color for price
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add description if available
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel("<html><body style='width: 150px'>" + 
                                         item.getDescription() + "</body></html>");
            descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            descLabel.setForeground(new Color(200, 200, 200));
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(priceLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(descLabel);
        } else {
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(priceLabel);
        }

        // Content panel (image and info)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(50, 50, 50));
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        // Add to cart button with fixed color
        JButton addButton = new JButton("Add to Cart");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(235, 94, 40)); // Orange color as specified
        addButton.setForeground(Color.WHITE); // White text for better contrast
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(panel.getWidth(), 40)); // Fixed height

        // Fix the button color issue
        addButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        addButton.setOpaque(true);

        addButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 104, 50), 1), // Slightly lighter border
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        addButton.setFocusPainted(false);

        // Hover effects
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(255, 114, 60)); // Brighter on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(235, 94, 40)); // Back to normal
            }
        });

        addButton.addActionListener(e -> {
            cartManager.addToCart(item, 1); // Add quantity parameter
            updateCartTotalAndCounter();
            JOptionPane.showMessageDialog(this, 
                item.getName() + " added to cart.",
                "Added to Cart", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        // Add components to the item panel
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH); // Button at the bottom

        return panel;
    }

    // Helper method to create placeholder images when no image is available
    private JLabel createPlaceholderImage(int width, int height, String letter) {
        JLabel placeholder = new JLabel(letter);
        placeholder.setPreferredSize(new Dimension(width, height));
        placeholder.setHorizontalAlignment(JLabel.CENTER);
        placeholder.setVerticalAlignment(JLabel.CENTER);
        placeholder.setOpaque(true);
        placeholder.setBackground(new Color(70, 70, 70));
        placeholder.setForeground(Color.WHITE);
        placeholder.setFont(new Font("Segoe UI", Font.BOLD, 48));
        placeholder.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        return placeholder;
    }
    
    private List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT product_id, name, price, description FROM products WHERE category = ? AND is_available = 1";
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
    
    private void updateCartTotalAndCounter() {
        int totalCount = cartManager.getCartItems().size();
        if (cartCounter != null) {
            cartCounter.setText(String.valueOf(totalCount));
            cartCounter.setVisible(totalCount > 0);
        }
    }
    
    private JPanel createMerchandiseContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 40)); // Dark background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel headerLabel = new JLabel("Merchandise");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(20));

        JLabel descLabel = new JLabel("<html>Explore our exclusive collection of merchandise. " +
                                     "Wear your favorite coffee brand with pride!</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(200, 200, 200)); // Light gray for description
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(30));

        List<MenuItem> merchandiseItems = getMenuItemsByCategory("MERCHANDISE");
        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        itemsGrid.setBackground(new Color(40, 40, 40)); // Dark background

        if (merchandiseItems.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No merchandise available at this time");
            noItemsLabel.setForeground(Color.WHITE);
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
        panel.setBackground(new Color(40, 40, 40)); // Dark background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Add section header
        JLabel headerLabel = new JLabel("Rewards Program");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel); 
        panel.add(Box.createVerticalStrut(20));

        // User points
        int userPoints = getUserPoints();
        JLabel pointsLabel = new JLabel("Your current points: " + userPoints);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pointsLabel.setForeground(new Color(218, 165, 32)); // Gold color for points
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pointsLabel);
        panel.add(Box.createVerticalStrut(30));

        // Program description
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setBackground(new Color(50, 50, 50)); // Slightly lighter than background
        descPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel howItWorksLabel = new JLabel("How It Works:");
        howItWorksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        howItWorksLabel.setForeground(Color.WHITE);

        JTextArea descText = new JTextArea(
            "• Earn 1 point for every P50 spent\n" +
            "• Redeem 100 points for a free regular drink\n" +
            "• Redeem 200 points for a free premium drink\n" +
            "• Special birthday reward - double points all month!"
        );
        descText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descText.setBackground(new Color(50, 50, 50));
        descText.setForeground(new Color(200, 200, 200)); // Light gray for text
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
        rewardsLabel.setForeground(new Color(218, 165, 32)); // Gold color
        rewardsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(rewardsLabel);
        panel.add(Box.createVerticalStrut(10));

        // Rewards grid
        JPanel rewardsGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        rewardsGrid.setBackground(new Color(40, 40, 40)); // Dark background

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
        return 0; // Temporary fix
    }
    
    private JPanel createRewardPanel(String rewardName, int pointsCost, int userPoints) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50)); // Slightly lighter background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel nameLabel = new JLabel(rewardName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);

        JLabel pointsLabel = new JLabel(pointsCost + " points");
        pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pointsLabel.setForeground(new Color(218, 165, 32)); // Gold color for points

        JButton redeemButton = new JButton("Redeem");
        redeemButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        redeemButton.setBackground(new Color(218, 165, 32)); // Gold button
        redeemButton.setForeground(Color.BLACK);
        redeemButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        redeemButton.setFocusPainted(false);
        redeemButton.setEnabled(userPoints >= pointsCost);

        if (!redeemButton.isEnabled()) {
            redeemButton.setToolTipText("Not enough points");
            redeemButton.setBackground(new Color(100, 100, 100)); // Disabled state
            redeemButton.setForeground(new Color(150, 150, 150));
        } else {
            // Hover effects (only for enabled buttons)
            redeemButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (redeemButton.isEnabled()) {
                        redeemButton.setBackground(new Color(255, 215, 0)); // Brighter gold on hover
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (redeemButton.isEnabled()) {
                        redeemButton.setBackground(new Color(218, 165, 32)); // Back to normal gold
                    }
                }
            });
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
        infoPanel.setBackground(new Color(50, 50, 50));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(pointsLabel);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(redeemButton, BorderLayout.EAST);

        // Add hover effect to the entire panel
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(60, 60, 60));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 165, 32), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                infoPanel.setBackground(new Color(60, 60, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(50, 50, 50));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                infoPanel.setBackground(new Color(50, 50, 50));
            }
        });

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