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

public class NotificationsPanel extends JPanel {
    private User currentUser;
    private JPanel notificationsContainer;
    private JLabel emptyLabel;
    private JScrollPane scrollPane;
    
    private final Color BACKGROUND_DARK = new Color(40, 40, 40);
    private final Color TEXT_COLOR = new Color(240, 240, 240);
    private final Color DIVIDER_COLOR = new Color(70, 70, 70);
    private final Color NOTIFICATION_BG = new Color(50, 50, 50); // Slightly lighter than background

    public NotificationsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_DARK);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_DARK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Notifications container
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(BACKGROUND_DARK);

        scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_DARK);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        emptyLabel = new JLabel("No notifications yet");
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emptyLabel.setForeground(Color.WHITE);
        emptyLabel.setAlignmentX(CENTER_ALIGNMENT);

        add(scrollPane, BorderLayout.CENTER);

        loadNotifications();
    }

    void loadNotifications() {
        notificationsContainer.removeAll();

        List<Notification> notifications = getNotificationsFromDatabase();

        if (notifications.isEmpty()) {
            // Keep centering for the empty message
            notificationsContainer.add(Box.createVerticalGlue());
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationsContainer.add(emptyLabel);
            notificationsContainer.add(Box.createVerticalGlue());
        } else {
            // Add notifications with improved spacing
            boolean firstItem = true;
            for (Notification notification : notifications) {
                if (!firstItem) {
                    notificationsContainer.add(Box.createVerticalStrut(8)); // Reduced spacing between items
                } else {
                    // Add a small strut above the first item for top padding
                    notificationsContainer.add(Box.createVerticalStrut(5));
                    firstItem = false;
                }

                notificationsContainer.add(createNotificationItem(notification));
            }
            
            // Add vertical glue at the end to push all items to the top
            notificationsContainer.add(Box.createVerticalGlue());
        }

        notificationsContainer.revalidate();
        notificationsContainer.repaint();
        // Ensure the scroll pane view is reset to the top when content changes
        SwingUtilities.invokeLater(() -> {
             JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
             if (verticalScrollBar != null) {
                 verticalScrollBar.setValue(0);
             }
         });
    }

    public static void createNotification(int userId, int orderId, String message) {
        String query = "INSERT INTO notifications (user_id, order_id, message) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, orderId);
            stmt.setString(3, message);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
        }
    }

    /**
     * Creates an order status notification
     */
    public static void createOrderStatusNotification(int userId, int orderId, String status) {
        String message = "";
        switch(status) {
            case "PENDING":
                message = "Your order #" + orderId + " has been received and is pending";
                break;
            case "PROCESSING":
                message = "Your order #" + orderId + " is being prepared";
                break;
            case "ON_ITS_WAY":
                message = "Your order #" + orderId + " is on its way";
                break;
            case "DELIVERED":
                message = "Your order #" + orderId + " has been delivered";
                break;
            case "CANCELLED":
                message = "Your order #" + orderId + " has been cancelled";
                break;
            default:
                 message = "Your order #" + orderId + " status changed to " + status; // Fallback
                 break;
        }
        createNotification(userId, orderId, message);
    }

    private JPanel createNotificationItem(Notification notification) {
        // Create a panel with compact size that adapts to content
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(NOTIFICATION_BG); // Slightly lighter background for each notification
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DIVIDER_COLOR, 1, true), // Rounded border with divider color
            BorderFactory.createEmptyBorder(6, 10, 6, 10) // Inner padding - reduced vertical padding
        ));
        
        // Make sure panel doesn't stretch beyond what's needed
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36)); // Limit max height
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Notification text label
        JLabel messageLabel = new JLabel(notification.getMessage());
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(TEXT_COLOR);
        
        panel.add(messageLabel, BorderLayout.CENTER);

        // Add hover effect and click behavior
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(60, 60, 60)); // Slightly lighter on hover
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(NOTIFICATION_BG); // Back to normal
            }
            
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!notification.isRead()) {
                    markNotificationAsRead(notification.getId());
                }
            }
        });

        return panel;
    }

    private List<Notification> getNotificationsFromDatabase() {
        List<Notification> notifications = new ArrayList<>();

        // Select all notifications for the current user, ordered by creation date
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

    private static class Notification {
        public enum Type {
            ORDER, REWARD, SYSTEM
        }

        private int id;
        private int orderId;
        private String message;
        private boolean isRead;
        private Timestamp date;
        private Type type;

        public Notification(int id, int orderId, String message, boolean isRead, Timestamp date) {
            this.id = id;
            this.orderId = orderId;
            this.message = message;
            this.isRead = isRead;
            this.date = date;
            this.type = orderId > 0 ? Type.ORDER : Type.SYSTEM;
        }

        public int getId() { return id; }
        public int getOrderId() { return orderId; }
        public String getMessage() { return message; }
        public boolean isRead() { return isRead; }
        public Timestamp getDate() { return date; }
    }
}