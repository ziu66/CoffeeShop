/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package coffeeshop;

import javax.swing.ImageIcon;

public class MenuItem {
    private int productId;
    private String name;
    private double price;
    private String description;
    private String imageUrl;  // New field for image URL
    private ImageIcon imageIcon;

    // Updated constructor to include imageUrl
    public MenuItem(int productId, String name, double price, String description, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Original constructor for backward compatibility
    public MenuItem(int productId, String name, double price, String description) {
        this(productId, name, price, description, null);
    }

    public int getProductId() { 
        return productId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
     public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    
    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }
}