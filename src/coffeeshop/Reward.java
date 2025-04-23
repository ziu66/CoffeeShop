/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

public class Reward {
    private String name;
    private double discountAmount;

    public Reward(String name, double discountAmount) {
        this.name = name;
        this.discountAmount = discountAmount;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}