/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JScrollPane scrollPane;
    private User currentUser;

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

        // Create content panel with categories
        JPanel contentPanel = createContentPanel();
        
        // Make it scrollable
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
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

        // Welcome label (top center)
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Header GIF (main image)
        URL imageUrl = getClass().getResource("/images/header-background2.gif");
        if (imageUrl != null) {
            ImageIcon headerIcon = new ImageIcon(imageUrl);
            JLabel promotionalImage = new JLabel(headerIcon);
            promotionalImage.setPreferredSize(new Dimension(1000, 150));
            panel.add(promotionalImage, BorderLayout.CENTER);
        }

        // Navigation bar (logo + buttons on left)
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        navBar.setBackground(Color.BLACK);
        navBar.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        // Add logo
        URL logoUrl = getClass().getResource("/images/logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            // Scale logo if needed
            Image scaledLogo = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            navBar.add(logoLabel);
        }

        // Add navigation buttons
        String[] navItems = {"MENU", "MERCHANDISE", "REWARDS"};
        for (String item : navItems) {
            JButton navButton = createNavButton(item);
            navBar.add(navButton);
        }

        panel.add(navBar, BorderLayout.SOUTH);
        return panel;
    }


    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add drinks section
        panel.add(createCategorySection("Drinks", createSampleDrinks()));
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Space between sections
        
        // Add meals section
        panel.add(createCategorySection("Meals", createSampleMeals()));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Add more sections as needed...

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

        // Item image (placeholder)
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
        addButton.setBackground(new Color(0, 102, 51)); // Green color
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addToCart(item));
        card.add(addButton);

        return card;
    }

    private void addToCart(MenuItem item) {
        JOptionPane.showMessageDialog(this, 
            item.getName() + " added to cart!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

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

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Fix for gray background
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(200, 200, 200)); // Light gray on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE); // White normally
            }
        });

        return button;
    }

    // Menu item class
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