/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        
        // Initialize components
        btnManageProducts = new JButton("Manage Products");
        btnManageOrders = new JButton("Manage Orders");
        btnManageUsers = new JButton("Manage Users");
        btnReports = new JButton("View Reports");
        btnLogout = new JButton("Logout");
        lblWelcome = new JLabel();
        
        // Create main panel with layout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblWelcome.setText("Welcome, " + user.getFullName() + " (Admin)");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(lblWelcome);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.add(btnManageProducts);
        buttonPanel.add(btnManageOrders);
        buttonPanel.add(btnManageUsers);
        buttonPanel.add(btnReports);
        
        // Create footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(btnLogout);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Set up the frame
        setTitle("But First, Coffee - Admin Dashboard");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Add action listeners
        btnManageProducts.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Product Management will be implemented here", 
                "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnManageOrders.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Order Management will be implemented here", 
                "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnManageUsers.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "User Management will be implemented here", 
                "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnReports.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Reports will be implemented here", 
                "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnLogout.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
    }
}