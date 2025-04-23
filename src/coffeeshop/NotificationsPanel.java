/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.basic.BasicButtonUI;

public class NotificationsPanel extends JPanel {
    private User currentUser;
    private JPanel notificationsContainer;
    private JLabel emptyLabel;
    private Timer refreshTimer;
    
    private final Color THEME_GOLD = new Color(218, 165, 32);
    private final Color BACKGROUND_DARK = new Color(40, 40, 40);
    private final Color CARD_READ = new Color(50, 50, 50);
    private final Color CARD_UNREAD = new Color(60, 70, 80);
    private final Color BUTTON_BG = new Color(70, 70, 70);
    private final Color TEXT_COLOR = new Color(240, 240, 240);
    
    public NotificationsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_DARK);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(BACKGROUND_DARK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("My Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(THEME_GOLD);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton markAllReadButton = new JButton("Mark All as Read");
        markAllReadButton.setBackground(THEME_GOLD);
        markAllReadButton.setForeground(Color.BLACK);
        markAllReadButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        markAllReadButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(THEME_GOLD.darker(), 1),
            BorderFactory.createEmptyBorder(6, 15, 6, 15)
        ));
        markAllReadButton.setFocusPainted(false);
        markAllReadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        markAllReadButton.addActionListener(e -> markAllNotificationsRead());
        markAllReadButton.setContentAreaFilled(true);
        markAllReadButton.setOpaque(true);
        
        buttonPanel.add(markAllReadButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(BACKGROUND_DARK);
        notificationsContainer.setAlignmentY(Component.TOP_ALIGNMENT); // Add this line
        
        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_DARK);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        scrollPane.getViewport().setAlignmentY(Component.TOP_ALIGNMENT);
        
        
        emptyLabel = new JLabel("No notifications yet");
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emptyLabel.setForeground(Color.WHITE);
        emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        add(scrollPane, BorderLayout.CENTER);
        
        loadNotifications();
    }
    
    void loadNotifications() {
        notificationsContainer.removeAll();
        refreshTimer = new Timer(10000, e -> loadNotifications());
        refreshTimer.start();
        
        List<Notification> notifications = getNotificationsFromDatabase();
        
        if (notifications.isEmpty()) {
            notificationsContainer.add(Box.createVerticalGlue());
            notificationsContainer.add(emptyLabel);
            notificationsContainer.add(Box.createVerticalGlue());
        } else {
            // Add filter options
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            filterPanel.setBackground(BACKGROUND_DARK);
            
            JButton allButton = new JButton("All");
            JButton unreadButton = new JButton("Unread");
            
            // Style "All" button as selected by default
            allButton.setUI(new BasicButtonUI()); // Force basic UI
            allButton.setBackground(THEME_GOLD);
            allButton.setForeground(Color.BLACK);
            allButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            allButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_GOLD.darker(), 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
            ));
            allButton.setFocusPainted(false);
            allButton.setContentAreaFilled(true);
            allButton.setOpaque(true);
            
            // Style "Unread" button as unselected
            unreadButton.setUI(new BasicButtonUI()); // Force basic UI
            unreadButton.setBackground(new Color(90, 90, 90)); // Darker than BUTTON_BG
            unreadButton.setForeground(TEXT_COLOR);
            unreadButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            unreadButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90).darker(), 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
            ));
            unreadButton.setFocusPainted(false);
            unreadButton.setContentAreaFilled(true);
            unreadButton.setOpaque(true);
            
            // Add action listeners (not implemented for demo)
            allButton.addActionListener(e -> {
                allButton.setBackground(THEME_GOLD);
                allButton.setForeground(Color.BLACK);
                allButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                unreadButton.setBackground(new Color(90, 90, 90));unreadButton.setBackground(BUTTON_BG);
                unreadButton.setForeground(TEXT_COLOR);
                unreadButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                
                // Implementation would filter notifications
            });
            
            unreadButton.addActionListener(e -> {
                unreadButton.setBackground(THEME_GOLD);
                unreadButton.setForeground(Color.BLACK);
                unreadButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                allButton.setBackground(BUTTON_BG);
                allButton.setForeground(TEXT_COLOR);
                allButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                
                // Implementation would filter notifications
            });
            
            filterPanel.add(allButton);
            filterPanel.add(unreadButton);
            
            notificationsContainer.add(filterPanel);
            notificationsContainer.add(Box.createVerticalStrut(10));
            
            for (Notification notification : notifications) {
                notificationsContainer.add(createNotificationCard(notification));
                notificationsContainer.add(Box.createVerticalStrut(8));
            }
             notificationsContainer.add(Box.createVerticalGlue());
        }
        
        notificationsContainer.revalidate();
        notificationsContainer.repaint();
        
        JScrollPane scrollPane = (JScrollPane) getComponent(1); // Assuming scroll pane is at index 1
        scrollPane.getVerticalScrollBar().setValue(0);
        
    }
    
    private JPanel createNotificationCard(Notification notification) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(notification.isRead() ? CARD_READ : CARD_UNREAD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        card.setPreferredSize(new Dimension(480, 120)); // Adjust width and height as needed
        card.setMinimumSize(new Dimension(480, 120));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 120)); // Allow horizontal stretching but fix height
    
        // Status indicator for unread notifications
        if (!notification.isRead()) {
            JPanel indicatorPanel = new JPanel();
            indicatorPanel.setPreferredSize(new Dimension(6, 0));
            indicatorPanel.setBackground(THEME_GOLD);
            card.add(indicatorPanel, BorderLayout.WEST);
        }
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 5));
        contentPanel.setOpaque(false);
        
        // Order info and icon panel
        JPanel orderInfoPanel = new JPanel(new BorderLayout(10, 0));
        orderInfoPanel.setOpaque(false);
        
        // Order icon (coffee icon)
        JLabel iconLabel = new JLabel("☕");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(THEME_GOLD);
        
        // Order title
        JLabel orderLabel = new JLabel("Order #" + notification.getOrderId());
        orderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderLabel.setForeground(Color.WHITE);
        
        orderInfoPanel.add(iconLabel, BorderLayout.WEST);
        orderInfoPanel.add(orderLabel, BorderLayout.CENTER);
        
        // Notification content
        JLabel contentLabel = new JLabel("<html><div style='width:300px'>" + 
            notification.getMessage() + "</div></html>");
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentLabel.setForeground(new Color(220, 220, 220));
        
        JLabel dateLabel = new JLabel(notification.getFormattedDate());
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dateLabel.setForeground(new Color(180, 180, 180));
        
        contentPanel.add(orderInfoPanel, BorderLayout.NORTH);
        contentPanel.add(contentLabel, BorderLayout.CENTER);
        contentPanel.add(dateLabel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Mark as read button for unread notifications
        if (!notification.isRead()) {
            JButton markReadButton = new JButton("√");
            markReadButton.setToolTipText("Mark as read");
            markReadButton.setPreferredSize(new Dimension(24, 24));
            markReadButton.setMaximumSize(new Dimension(24, 24));
            markReadButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
            markReadButton.setMargin(new Insets(0, 0, 0, 0));
            markReadButton.setBackground(THEME_GOLD);
            markReadButton.setForeground(Color.BLACK);
            markReadButton.setBorder(BorderFactory.createLineBorder(THEME_GOLD.darker(), 1));
            markReadButton.setFocusPainted(false);
            markReadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            markReadButton.addActionListener(e -> {
                markNotificationAsRead(notification.getId());
                loadNotifications(); // Refresh the view
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setOpaque(false);
            buttonPanel.add(markReadButton);
            card.add(buttonPanel, BorderLayout.EAST);
        }
        
        return card;
    }
    
    private List<Notification> getNotificationsFromDatabase() {
        List<Notification> notifications = new ArrayList<>();
        
        String query = "SELECT n.notification_id, n.order_id, n.message, n.is_read, n.created_at " +
                       "FROM notifications n " +
                       "WHERE n.user_id = ? " +
                       "ORDER BY n.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUser.getUserId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("order_id"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading notifications: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return notifications;
    }

    private void refreshNotificationsPanel() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JFrame) {
                Component[] components = ((JFrame) window).getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof NotificationsPanel) {
                        ((NotificationsPanel) comp).loadNotifications();
                    }
                }
            }
        }
    }
    
    private void markNotificationAsRead(int notificationId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE notifications SET is_read = 1 WHERE notification_id = ?")) {
            
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error marking notification as read: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void markAllNotificationsRead() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0")) {
            
            stmt.setInt(1, currentUser.getUserId());
            int count = stmt.executeUpdate();
            
            if (count > 0) {
                JOptionPane.showMessageDialog(this, 
                    count + " notifications marked as read", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            loadNotifications(); // Refresh the view
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error marking notifications as read: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static class Notification {
        private int id;
        private int orderId;
        private String message;
        private boolean isRead;
        private Timestamp date;
        
        public Notification(int id, int orderId, String message, boolean isRead, Timestamp date) {
            this.id = id;
            this.orderId = orderId;
            this.message = message;
            this.isRead = isRead;
            this.date = date;
        }
        
        public int getId() { return id; }
        public int getOrderId() { return orderId; }
        public String getMessage() { return message; }
        public boolean isRead() { return isRead; }
        public Timestamp getDate() { return date; }
        
        public String getFormattedDate() {
            return new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a")
                .format(date);
        }
    }
    
    public void cleanup() {
    if (refreshTimer != null && refreshTimer.isRunning()) {
        refreshTimer.stop();
        refreshTimer = null;
    }
}
}