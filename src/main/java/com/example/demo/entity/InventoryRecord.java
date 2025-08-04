package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_records")
public class InventoryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type;
    
    @Column(name = "material_code")
    private String itemCode;
    
    @Column(name = "material_name")
    private String itemName;
    
    private String specification;
    private int quantity;
    
    private String warehouse;
    private String operator;
    
    private LocalDateTime time;
    
    // Constructors
    public InventoryRecord() {
    }
    
    public InventoryRecord(String type, String itemCode, String itemName, String specification, int quantity, String warehouse, String operator, LocalDateTime time) {
        this.type = type;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.specification = specification;
        this.quantity = quantity;
        this.warehouse = warehouse;
        this.operator = operator;
        this.time = time;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getWarehouse() {
        return warehouse;
    }
    
    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}