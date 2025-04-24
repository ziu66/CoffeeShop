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
    private String redemptionCode;
    private Date expiresAt;
    private boolean isExpired;

    public Reward(String name, double discountAmount) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.isRedeemed = false;
        this.isExpired = false;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public boolean isRedeemed() {
        return isRedeemed;
    }

    public void setRedeemed(boolean redeemed) {
        isRedeemed = redeemed;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        this.isExpired = new Date().after(expiresAt);
    }

    public boolean isExpired() {
        return isExpired;
    }

    public boolean isActive() {
        return isRedeemed && !isExpired;
    }
}