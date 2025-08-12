package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * 物料主数据实体类（对应数据库表 materials）
 * 核心作用：工业零件数据的核心存储位置，提供唯一主键和唯一物料代码约束
 */
@Entity
@Table(name = "materials")
public class Material {

    // 主键字段
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long id;

    // 物料代码（唯一标识）
    @Column(name = "material_code", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String materialCode;

    // 物料名称（必填）
    @Column(name = "material_name", nullable = false, length = 200, columnDefinition = "VARCHAR(200)")
    private String materialName;

    // 物料分类ID
    @Column(name = "category_id", nullable = true, columnDefinition = "BIGINT")
    private Long categoryId;

    // 规格型号（可为空）
    @Column(name = "specification", nullable = true, length = 200, columnDefinition = "VARCHAR(200)")
    private String specification;

    // 单位（如件、个）
    @Column(name = "unit", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private String unit;

    // 条形码
    @Column(name = "barcode", nullable = true, length = 100, columnDefinition = "VARCHAR(100)")
    private String barcode;

    // 品牌
    @Column(name = "brand", nullable = true, length = 100, columnDefinition = "VARCHAR(100)")
    private String brand;

    // 供应商
    @Column(name = "supplier", nullable = true, length = 100, columnDefinition = "VARCHAR(100)")
    private String supplier;

    // 使用状态（启用/停用）
    @Column(name = "status", nullable = true, length = 20, columnDefinition = "VARCHAR(20)")
    private String status;

    // 默认仓库ID
    @Column(name = "default_warehouse_id", nullable = true, columnDefinition = "BIGINT")
    private Long defaultWarehouseId;

    // 物料描述
    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    private String description;

    // 创建人
    @Column(name = "created_by", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String createdBy;

    // 创建时间（自动生成）
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新人
    @Column(name = "updated_by", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String updatedBy;

    // 最后更新时间
    @UpdateTimestamp
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;
    
    // 临时属性：批次数（不映射到数据库）
    @Transient
    private Integer batchCount = 0;

    // Constructors
    public Material() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDefaultWarehouseId() {
        return defaultWarehouseId;
    }

    public void setDefaultWarehouseId(Long defaultWarehouseId) {
        this.defaultWarehouseId = defaultWarehouseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    // 临时属性的getter和setter方法
    public Integer getBatchCount() {
        return batchCount;
    }
    
    public void setBatchCount(Integer batchCount) {
        this.batchCount = batchCount;
    }
    
    // 其他临时属性（用于前端显示）
    @Transient
    private String materialProperty; // 物料属性（用于前端显示供应商信息）

    @Transient
    private String warehouse; // 仓库/分类（用于前端显示分类名称）

    @Transient
    private Integer currentStock; // 当前库存（用于前端显示库存数量）

    @Transient
    private Integer minStock; // 最低库存预警值（用于前端显示库存状态）

    public String getMaterialProperty() {
        return materialProperty;
    }

    public void setMaterialProperty(String materialProperty) {
        this.materialProperty = materialProperty;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }
}