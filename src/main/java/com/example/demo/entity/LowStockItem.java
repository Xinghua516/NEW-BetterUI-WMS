package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "low_stock_items")
public class LowStockItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_code", unique = true)
    private String itemCode;
    
    @Column(name = "item_name")
    private String itemName;
    
    private String specification;
    
    @Column(name = "current_stock")
    private int currentStock;
    
    @Column(name = "min_stock")
    private int minStock;
    
    private String warehouse;
    private String status;
    
    // Constructors
    public LowStockItem() {
    }
    
    public LowStockItem(String itemCode, String itemName, String specification, int currentStock, int minStock, String warehouse, String status) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.specification = specification;
        this.currentStock = currentStock;
        this.minStock = minStock;
        this.warehouse = warehouse;
        this.status = status;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public int getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }
    
    public int getMinStock() {
        return minStock;
    }
    
    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }
    
    public String getWarehouse() {
        return warehouse;
    }
    
    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}