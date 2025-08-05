package com.example.demo.entity;

public class InventoryAlertDTO {
    private String itemCode;
    private String itemName;
    private int currentStock;
    private int thresholdValue;
    private String alertType;
    
    public InventoryAlertDTO(InventoryAlert alert) {
        if (alert.getMaterial() != null) {
            this.itemCode = alert.getMaterial().getMaterialCode();
            this.itemName = alert.getMaterial().getMaterialName();
        }
        
        this.currentStock = alert.getCurrentQuantity() != null ? alert.getCurrentQuantity() : 0;
        this.thresholdValue = alert.getThresholdValue() != null ? alert.getThresholdValue() : 0;
        this.alertType = alert.getAlertType();
    }
    
    // Getters and setters
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public int getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }
    
    public int getThresholdValue() {
        return thresholdValue;
    }
    
    public void setThresholdValue(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
}