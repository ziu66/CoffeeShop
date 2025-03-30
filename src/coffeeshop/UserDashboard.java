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

    public UserDashboard(User user) {
        this.currentUser = user;
        initializeUI();
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
        panel.setPreferredSize(new Dimension(1200, 250)); // Fixed header size

        // 1. Welcome Message (Top)
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // 2. Animated GIF (Center)
        JPanel gifContainer = new JPanel(new BorderLayout());
        gifContainer.setBackground(Color.BLACK);

        // GIF Loading with Debugging
        // Replace your existing GIF loading code with this
    try {
        File gifFile = new File("C:\\Users\\sophi\\Downloads\\images\\header-backgroundd.gif");

        if (gifFile.exists()) {
            // Create ImageIcon directly from file path
            ImageIcon gifIcon = new ImageIcon(gifFile.getAbsolutePath());

            // Force the image to load completely before adding to container
            Image img = gifIcon.getImage();
            // This creates a new ImageIcon that's fully loaded
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

        // Logo + Navigation Buttons Container
        JPanel navContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        navContent.setBackground(Color.BLACK);

        // 3.1 Logo
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

        // 3.2 Navigation Buttons
        String[] navItems = {"MENU", "MERCHANDISE", "REWARDS"};
        navButtons = new JButton[navItems.length];

        for (int i = 0; i < navItems.length; i++) {
            navButtons[i] = new JButton(navItems[i]);
            navButtons[i].setForeground(Color.WHITE);
            navButtons[i].setBackground(Color.BLACK);
            navButtons[i].setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            navButtons[i].setFont(new Font("Arial", Font.BOLD, 14));
            navButtons[i].setContentAreaFilled(false);
            navButtons[i].setFocusPainted(false);

            // Hover effect
            navButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    ((JButton)evt.getSource()).setForeground(new Color(200, 200, 200));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    ((JButton)evt.getSource()).setForeground(Color.WHITE);
                }
            });

            // Navigation action
            final int index = i;
            navButtons[i].addActionListener(e -> {
                updateActiveButton(index);
                handleNavigation(navItems[index]);
            });

            navContent.add(navButtons[i]);
        }

        // Set initial active button (MENU)
        updateActiveButton(0);

        navBar.add(navContent, BorderLayout.WEST);
        panel.add(navBar, BorderLayout.SOUTH);

        return panel;
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

        // Drinks section
        panel.add(createCategorySection("Drinks", createSampleDrinks()));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Meals section
        panel.add(createCategorySection("Meals", createSampleMeals()));

        return panel;
    }

    private JPanel createMerchandiseContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createCategorySection("Merchandise", createSampleMerchandise()));
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

        // Item image placeholder
        JLabel imageLabel = new JLabel(new ImageIcon(getClass().getResource("")));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(imageLabel);

        // Item name
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
        JOptionPane.showMessageDialog(this, 
            item.getName() + " added to cart!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Sample data methods
    private List<MenuItem> createSampleDrinks() {
        List<MenuItem> drinks = new ArrayList<>();
        drinks.add(new MenuItem("Espresso", 3.50));
        drinks.add(new MenuItem("Cappuccino", 4.50));
        drinks.add(new MenuItem("Latte", 4.75));
        drinks.add(new MenuItem("Mocha", 5.00));
        drinks.add(new MenuItem("Americano", 3.75));
        drinks.add(new MenuItem("Flat White", 4.25));
        return drinks;
    }

    private List<MenuItem> createSampleMeals() {
        List<MenuItem> meals = new ArrayList<>();
        meals.add(new MenuItem("Avocado Toast", 8.50));
        meals.add(new MenuItem("Breakfast Burrito", 9.75));
        meals.add(new MenuItem("Pancake Stack", 7.50));
        meals.add(new MenuItem("Eggs Benedict", 10.25));
        meals.add(new MenuItem("Greek Yogurt Bowl", 6.75));
        meals.add(new MenuItem("Breakfast Sandwich", 7.25));
        return meals;
    }

    private List<MenuItem> createSampleMerchandise() {
        List<MenuItem> merchandise = new ArrayList<>();
        merchandise.add(new MenuItem("Coffee Mug", 12.99));
        merchandise.add(new MenuItem("Travel Tumbler", 24.99));
        merchandise.add(new MenuItem("Coffee Beans (1lb)", 14.99));
        merchandise.add(new MenuItem("Brewing Kit", 29.99));
        merchandise.add(new MenuItem("Gift Card", 25.00));
        merchandise.add(new MenuItem("T-Shirt", 19.99));
        return merchandise;
    }

    private static class MenuItem {
        private String name;
        private double price;

        public MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}
