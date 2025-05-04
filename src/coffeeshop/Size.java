/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop; // Make sure the package is correct

public class Size {
    private int id;
    private String name;
    private double price; // Price for this specific size of a product

    public Size(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    // Optional: Override equals and hashCode if you need to compare Size objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return id == size.id; // Compare based on ID
    }

    @Override
    public int hashCode() {
        return id;
    }

     @Override
     public String toString() {
         return name + " (₱" + String.format("%.2f", price) + ")";
     }
     
}