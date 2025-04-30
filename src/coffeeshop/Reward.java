/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import java.util.Date;

public class Reward {
    private String name;
    private double discountAmount;
    private boolean isRedeemed;
    private String redemptionCode; // Not currently used for validation in CartManager, but kept
    private Date expiresAt;        // Not currently used for validation in CartManager, but kept
    private boolean isExpired;      // Not currently used for validation in CartManager, but kept
    private int redemptionId; // <-- ADD THIS FIELD

    public Reward(String name, double discountAmount) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.isRedeemed = false; // Initial state when created from DB row
        this.isExpired = false;  // Initial state when created from DB row
        // isRedeemed and isExpired should be set based on DB data *after* loading, not in constructor
        // Consider removing isExpired field and calculating it from expiresAt when needed.
        // For now, keeping it but noting its potential issues with state freshness.
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    // This status should be set when loading from DB
    public boolean isRedeemed() {
        return isRedeemed;
    }

    // This setter should be used when loading from DB
    public void setRedeemed(boolean redeemed) {
        isRedeemed = redeemed;
    }

    public String getRedemptionCode() {
        return redemptionCode; // This is currently not loaded from the DB in CartManager
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public Date getExpiresAt() {
        return expiresAt; // This is currently not loaded from the DB in CartManager
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        // NOTE: This calculation makes isExpired dependent on when setExpiresAt is called,
        // not necessarily on the current time. It's better to calculate it on the fly.
        this.isExpired = new Date().after(expiresAt);
    }

    public boolean isExpired() {
         if (expiresAt == null) return false; // Or true, depending on default policy
         return new Date().after(expiresAt);
    }

    public boolean isActive() {
        return !isRedeemed() && !isExpired();
    }
    
    // ADD THIS GETTER
    public int getRedemptionId() {
        return redemptionId;
    }

    // ADD THIS SETTER
    public void setRedemptionId(int redemptionId) {
        this.redemptionId = redemptionId;
    }
}