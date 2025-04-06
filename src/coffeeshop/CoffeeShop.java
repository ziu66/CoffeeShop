/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package coffeeshop;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.sql.SQLException;

public class CoffeeShop {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting system look and feel: " + e.getMessage());
        }

        try {
            DBConnection.getConnection();
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Failed to connect to database. Please check your database server and try again.",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}

