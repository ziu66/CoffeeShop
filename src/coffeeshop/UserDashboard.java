/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel menuPanel;
    private User currentUser;

    public UserDashboard(User user) {
        this.currentUser = user;

        // Create main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.setBackground(Color.BLACK);  // Set main panel background to black

        // Create header panel with promotional image
        headerPanel = createHeaderPanel();
        headerPanel.setBackground(Color.BLACK);  // Set header panel background to black

        // Create menu panel
        menuPanel = createMenuPanel();
        menuPanel.setBackground(Color.BLACK);  // Set menu panel background to black

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Set up the frame
        setTitle("But First, Coffee - " + user.getFullName());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set to full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.BLACK);  // Optional: ensure content pane is black
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Explicitly set maximum and minimum sizes as well
        panel.setPreferredSize(new Dimension(1200, 250));
        panel.setMaximumSize(new Dimension(1200, 250));
        panel.setMinimumSize(new Dimension(1200, 250));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Load the header background GIF
        URL imageUrl = getClass().getResource("/images/header-background2.gif");
        if (imageUrl == null) {
            System.out.println("GIF not found! Check file path.");
            JLabel promotionalImage = new JLabel("Header Image Not Found");
            panel.add(promotionalImage, BorderLayout.CENTER);
        } else {
            ImageIcon headerIcon = new ImageIcon(imageUrl);
            JLabel promotionalImage = new JLabel(headerIcon);

            // Also set sizes for the image label
            promotionalImage.setPreferredSize(new Dimension(1000, 150));
            promotionalImage.setMaximumSize(new Dimension(1000, 150));
            promotionalImage.setMinimumSize(new Dimension(1000, 150));

            panel.add(promotionalImage, BorderLayout.CENTER);
        }

        panel.add(welcomeLabel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Drinks Menu
        panel.add(createMenuButton("View Menu", "/menu-icon.png", e -> {
            JOptionPane.showMessageDialog(this, 
                "Full menu will be displayed here", 
                "Menu", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        // Place Order
        panel.add(createMenuButton("Place Order", "/order-icon.png", e -> {
            JOptionPane.showMessageDialog(this, 
                "Order placement process will start here", 
                "Place Order", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        // Order History
        panel.add(createMenuButton("Order History", "/history-icon.png", e -> {
            JOptionPane.showMessageDialog(this, 
                "Your previous orders will be shown here", 
                "Order History", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        // Account Settings
        panel.add(createMenuButton("Account Settings", "/settings-icon.png", e -> {
            JOptionPane.showMessageDialog(this, 
                "Update your account information here", 
                "Account Settings", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        // Rewards
        panel.add(createMenuButton("Rewards", "/rewards-icon.png", e -> {
            JOptionPane.showMessageDialog(this, 
                "Check your reward points and benefits", 
                "Rewards", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        // Logout
        panel.add(createMenuButton("Logout", "/logout-icon.png", e -> {
            new LoginForm().setVisible(true);
            dispose();
        }));
        
        return panel;
    }

    private JButton createMenuButton(String text, String iconPath, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        
        try {
            // Try to load icon, use a default if not found
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Fallback if icon not found
            button.setText(text);
        }
        
        button.setPreferredSize(new Dimension(150, 150));
        button.addActionListener(action);
        
        // Styling to make it look more modern
        button.setBackground(new Color(0, 102, 51)); // Starbucks green
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        return button;
    }
}