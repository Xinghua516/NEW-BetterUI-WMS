package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "low_stock_items")
public class LowStockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material; // 关联物料实体

    @Column(name = "material_code", nullable = false, unique = true)
    private String materialCode;

    @Column(name = "material_name", nullable = false)
    private String materialName;

    @Column(name = "specification")
    private String specification;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;

    @Column(name = "warehouse")
    private String warehouse;

    @Column(name = "status")
    private String status;

    // Getter and Setter methods

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    // Constructors
    public LowStockItem() {
    }

    public LowStockItem(String materialCode, String materialName, String specification, int currentStock, int minStock, String warehouse, String status) {
        this.materialCode = materialCode;
        this.materialName = materialName;
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
        return materialCode;
    }

    public void setItemCode(String itemCode) {
        this.materialCode = itemCode;
    }

    public String getItemName() {
        return materialName;
    }

    public void setItemName(String itemName) {
        this.materialName = itemName;
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