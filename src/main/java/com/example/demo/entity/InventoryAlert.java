package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_alerts")
public class InventoryAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @Column(name = "alert_type")
    private String alertType;
    
    @Column(name = "current_quantity")
    private Integer currentQuantity;
    
    @Column(name = "threshold_value")
    private Integer thresholdValue;
    
    @Column(name = "is_processed")
    private Boolean isProcessed;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public InventoryAlert() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public Integer getCurrentQuantity() {
        return currentQuantity;
    }
    
    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
    
    public Integer getThresholdValue() {
        return thresholdValue;
    }
    
    public void setThresholdValue(Integer thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
    
    public Boolean getProcessed() {
        return isProcessed;
    }
    
    public void setProcessed(Boolean processed) {
        isProcessed = processed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}