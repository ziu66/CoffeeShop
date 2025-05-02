/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop; // Make sure the package is correct

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private MenuItem item; // The base product (e.g., "Plain Brewed Coffee")
    private int quantity;
    private boolean selected = true; // Default to selected in the cart
    private Size selectedSize; // *** NEW FIELD: The selected size for this item ***
    // private List<AddOn> selectedAddOns = new ArrayList<>(); // *** Future: Add list for selected add-ons ***

    public CartItem(MenuItem item) {
        this.item = item;
    }

     // *** Constructor including Size ***
     public CartItem(MenuItem item, Size selectedSize) {
         this.item = item;
         this.selectedSize = selectedSize;
         this.quantity = 1; // Default quantity is 1 when added
     }


    public MenuItem getItem() { return item; }
    public int getQuantity() { return quantity; }
    public boolean isSelected() { return selected; }
    public Size getSelectedSize() { return selectedSize; } // *** NEW GETTER for Size ***
    // public List<AddOn> getSelectedAddOns() { return selectedAddOns; } // *** Future: Add getter for AddOns ***

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public void setSelectedSize(Size selectedSize) { this.selectedSize = selectedSize; } // *** NEW SETTER for Size ***
    // public void addAddOn(AddOn addOn) { this.selectedAddOns.add(addOn); } // *** Future: Add method for AddOns ***

    // *** NEW: Calculate the price for THIS specific CartItem instance ***
    // This price considers the size and quantity (and future add-ons)
    public double getCalculatedPrice() {
        double basePrice = 0.0;
        if (item.getCategory().equals("DRINK") && selectedSize != null) {
            basePrice = selectedSize.getPrice(); // Use the size's price for drinks
        } else {
            basePrice = item.getPrice(); // Use the base product price for non-drinks
        }

        double addOnsTotal = 0.0;
        // *** Future: Calculate total price of selected add-ons ***
        // for (AddOn addOn : selectedAddOns) {
        //     addOnsTotal += addOn.getPrice();
        // }

        return (basePrice + addOnsTotal) * quantity;
    }

    // Helper method to check if two CartItem instances are the same *logical* item (same product and same size)
     public boolean isSameItem(MenuItem product, Size size) {
         if (product == null || this.item == null) return false;
         boolean sameProduct = this.item.getId() == product.getId();

         if (item.getCategory().equals("DRINK")) {
             // For drinks, they are the same item only if product AND size match
             if (this.selectedSize == null || size == null) return false; // Must both have sizes
             return sameProduct && this.selectedSize.getId() == size.getId();
         } else {
             // For non-drinks, they are the same item just if the product matches (size is ignored or implicit)
             return sameProduct; // Ignore size for non-drinks
         }
     }

     // Helper method to get a unique identifier for this cart item (product + size)
     // Useful for maps or comparisons in UI/Manager logic
     public String getUniqueKey() {
         String key = String.valueOf(item.getId());
         if (item.getCategory().equals("DRINK") && selectedSize != null) {
             key += "_" + selectedSize.getId();
         }
         // Future: add add-on IDs to the key if add-ons affect uniqueness
         return key;
     }
}