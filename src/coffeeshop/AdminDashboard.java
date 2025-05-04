/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class AdminDashboard extends JFrame {
    private JPanel mainPanel;
    private JButton btnManageProducts;
    private JButton btnManageOrders;
    private JButton btnManageUsers;
    private JButton btnReports;
    private JButton btnLogout;
    private JLabel lblWelcome;

    private User currentUser;

    public AdminDashboard(User user) {
        this.currentUser = user;

        try {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the frame
        setTitle("But First, Coffee - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Make the frame full screen for better layout responsiveness
        setExtendedState(JFrame.MAXIMIZED_BOTH); // <-- ADDED/MODIFIED LINE
        setUndecorated(false); // Keep standard window decorations (title bar, borders) <-- ADDED/MODIFIED LINE
        // setSize(1200, 700); // <-- REMOVED LINE
        // setLocationRelativeTo(null); // <-- REMOVED LINE


        // Create main container with BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30)); // Dark background

        // Add the header panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create and add content panel
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        // Set the content pane
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        // panel.setPreferredSize(new Dimension(1200, 150)); // <-- REMOVED fixed preferred size

        // Header Title
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(218, 165, 32)); // Gold text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); // Adjusted bottom padding
        panel.add(titleLabel, BorderLayout.NORTH);

        // Navigation Bar (Bottom)
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.BLACK);
        // navBar.setPreferredSize(new Dimension(1200, 50)); // <-- REMOVED fixed preferred size

        // Left-aligned content (logo + title)
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

        // Admin Label
        JLabel adminLabel = new JLabel("ADMIN CONTROL PANEL");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 24));
        adminLabel.setForeground(new Color(218, 165, 32)); // Gold color
        navContent.add(adminLabel);

        // Right-aligned components (welcome + logout)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5)); // Adjusted vertical gap
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Welcome Label
        lblWelcome = new JLabel("Welcome, " + currentUser.getFullName() + " (Admin)");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(new Color(220, 220, 220));
        rightPanel.add(lblWelcome);

        // Logout Button
        btnLogout = new JButton("Logout");
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(true);
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setForeground(new Color(200, 200, 200));
        btnLogout.setBackground(new Color(70, 70, 70));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effects
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setForeground(Color.WHITE);
                btnLogout.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setForeground(new Color(200, 200, 200));
                btnLogout.setBackground(new Color(70, 70, 70));
            }
        });

        btnLogout.addActionListener(e -> {
            new LoginForm().setVisible(true); // Assuming LoginForm exists
            dispose();
        });

        rightPanel.add(btnLogout);

        // Add components to navBar
        navBar.add(navContent, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);

        panel.add(navBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createContentPanel() {
        // Main content area with some padding
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create dashboard title
        JLabel dashboardTitle = new JLabel("Administrative Controls", SwingConstants.LEFT);
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        dashboardTitle.setForeground(Color.WHITE);
        dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 0)); // Adjusted bottom padding

        // Create a card panel to hold the admin function tiles
        JPanel cardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardPanel.setBackground(new Color(40, 40, 40));

        // Create admin function cards
        cardPanel.add(createFunctionCard("Manage Products", "Control the products displayed to customers", "/images/products-icon.png", e -> {
            ProductManagement productManagement = new ProductManagement(currentUser);
            productManagement.setVisible(true);
        }));

        cardPanel.add(createFunctionCard("Manage Orders", "View and process customer orders", "/images/orders-icon.png", e -> {
            OrderManagement orderManagement = new OrderManagement(currentUser);
            orderManagement.setVisible(true);
        }));

        cardPanel.add(createFunctionCard("Manage Users", "Add, edit or disable user accounts", "/images/users-icon.png", e -> {
            UserManagement userManagement = new UserManagement(currentUser);
            userManagement.setVisible(true);
        }));

        cardPanel.add(createFunctionCard("View Reports", "Access sales and inventory reports", "/images/reports-icon.png", e -> {
            ReportsSystem reports = new ReportsSystem(this, currentUser);
            reports.setVisible(true);
        }));

        // Create content wrapper to hold title and cards
        JPanel contentWrapper = new JPanel(new BorderLayout(0, 15));
        contentWrapper.setBackground(new Color(40, 40, 40));
        contentWrapper.add(dashboardTitle, BorderLayout.NORTH);
        contentWrapper.add(cardPanel, BorderLayout.CENTER); // cardPanel is in CENTER

        contentPanel.add(contentWrapper, BorderLayout.CENTER); // contentWrapper is in CENTER of contentPanel

        // Add a status bar at the bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(30, 30, 30));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)));
        // statusBar.setPreferredSize(new Dimension(1200, 25)); // <-- REMOVED fixed preferred size

        JLabel statusLabel = new JLabel(" System Status: Online | Last Updated: " +
                                       java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(150, 150, 150));
        statusBar.add(statusLabel, BorderLayout.WEST);

        contentPanel.add(statusBar, BorderLayout.SOUTH);

        return contentPanel;
    }

    private JPanel createFunctionCard(String title, String description, String iconPath, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(50, 50, 50));

        // Icon (top)
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setOpaque(false);

        // Try to load the icon
        JLabel iconLabel = new JLabel(); // Use JLabel directly
        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image scaledIcon = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledIcon));
        } else {
            // Add placeholder text if icon not found
            iconLabel.setText("[ICON]");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
            iconLabel.setForeground(new Color(218, 165, 32)); // Gold color
        }
        iconPanel.add(iconLabel);


        // Title and description (center)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        textPanel.add(Box.createVerticalGlue()); // Added glue to push text to top if card is very tall

        // Button (bottom)
        JButton actionButton = new JButton("Open");
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionButton.setForeground(Color.BLACK);
        actionButton.setBackground(new Color(218, 165, 32)); // Gold color
        actionButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        actionButton.setFocusPainted(false);
        actionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // actionButton.setAlignmentX(Component.CENTER_ALIGNMENT); // FlowLayout handles this

        // Hover effects
        actionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(new Color(255, 215, 0)); // Brighter gold on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(new Color(218, 165, 32)); // Back to normal gold
            }
        });

        if (action != null) {
            actionButton.addActionListener(action);
        }

        // Button panel to center the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(actionButton);

        // Assemble card
        card.add(iconPanel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Add hover effect to the entire card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(60, 60, 60));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 165, 32), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(50, 50, 50));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return card;
    }
}