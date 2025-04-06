/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Initialize CardLayout system
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Add all content panels to cardPanel
        cardPanel.add(createMenuContent(), "menu");
        cardPanel.add(createMerchandiseContent(), "merchandise");
        cardPanel.add(createRewardsContent(), "rewards");
        cardPanel.add(createCartContent(), "cart"); 
        cardPanel.add(cartManager.createCheckoutPanel(cardPanel, cardLayout), "checkout");

        // Add cardPanel to mainPanel (center)
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Create header panel with promotional image
        headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Frame setup
        setTitle("But First, Coffee - " + currentUser.getFullName());
        setContentPane(mainPanel);
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

            if (productName.contains("cappuccino")) {
                imagePath = "/images/cappuccino.png";
            } else if (productName.contains("espresso")) {
                imagePath = "/images/espresso.png";
            } else if (productName.contains("latte")) {
                imagePath = "/images/latte.png";
            } else {
                imagePath = "";
            }

            java.net.URL imageURL = getClass().getResource(imagePath);
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel = new JLabel(new ImageIcon(scaledImage));
            } else {
                System.err.println("Image resource not found: " + imagePath);
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
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> {
            cartManager.addToCart(item);
            updateCartTotalAndCounter();
            JOptionPane.showMessageDialog(this, 
                item.getName() + " added to cart.",
                "Added to Cart", 
                JOptionPane.INFORMATION_MESSAGE);
        });

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
    
    private void updateCartTotalAndCounter() {
        int totalCount = cartManager.getTotalItemCount();
        if (cartCounter != null) {
            cartCounter.setText(String.valueOf(totalCount));
            cartCounter.setVisible(totalCount > 0);
        }
    }
    
    private JPanel createMerchandiseContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel headerLabel = new JLabel("Merchandise");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(20));

        JLabel descLabel = new JLabel("<html>Explore our exclusive collection of merchandise. " +
                                     "Wear your favorite coffee brand with pride!</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(30));

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