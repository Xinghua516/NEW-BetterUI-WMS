package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transaction_no")
    private String transactionNo;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
    
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private InventoryTransactionType transactionType;
    
    // Constructors
    public InventoryTransaction() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTransactionNo() {
        return transactionNo;
    }
    
    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }
    
    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public Warehouse getWarehouse() {
        return warehouse;
    }
    
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    
    public InventoryTransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(InventoryTransactionType transactionType) {
        this.transactionType = transactionType;
    }
}