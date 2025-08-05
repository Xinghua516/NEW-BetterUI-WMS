package com.example.demo.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryTransactionDTO {
    private String type;
    private String itemCode;
    private String itemName;
    private int quantity;
    private String time;
    
    public InventoryTransactionDTO(InventoryTransaction transaction) {
        if (transaction.getTransactionType() != null) {
            this.type = transaction.getTransactionType().getDirection();
        }
        
        if (transaction.getMaterial() != null) {
            this.itemCode = transaction.getMaterial().getMaterialCode();
            this.itemName = transaction.getMaterial().getMaterialName();
        }
        
        this.quantity = transaction.getQuantity() != null ? transaction.getQuantity() : 0;
        
        if (transaction.getTransactionTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.time = transaction.getTransactionTime().format(formatter);
        }
    }
    
    // Getters and setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
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
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
}