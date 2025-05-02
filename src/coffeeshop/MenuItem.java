/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop; // Make sure the package is correct

import javax.swing.ImageIcon;

public class MenuItem {
    private int id;
    private String name;
    private double price; // This price is primarily for non-DRINK items now
    private String description;
    private String imageUrl;
    private ImageIcon imageIcon;
    private String category;
    private String drinkType;

    // Keep the constructor that includes all current fields from products table
    public MenuItem(int id, String name, double price, String description, String imageUrl, String category, String drinkType) {
        this.id = id;
        this.name = name;
        this.price = price; // Still store the price from products table (useful for non-drinks)
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.drinkType = drinkType;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; } // Getter for the price from products table
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public ImageIcon getImageIcon() { return imageIcon; }
    public String getCategory() { return category; }
    public String getDrinkType() { return drinkType; }

    // Setter for the loaded image icon
    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    // We won't need setters for other fields if they are always loaded from DB
}