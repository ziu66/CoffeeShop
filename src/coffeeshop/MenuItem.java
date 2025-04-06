/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

public class MenuItem {
    private int productId;
    private String name;
    private double price;
    private String description;

    public MenuItem(int productId, String name, double price, String description) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
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
}