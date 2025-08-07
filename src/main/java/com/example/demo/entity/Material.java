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

    // 更新时间（自动生成）
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 当前库存数量
    @Column(name = "current_stock", nullable = true, columnDefinition = "INT")
    private Integer currentStock;
    
    // 最低库存阈值（低于此值触发预警）
    @Column(name = "min_stock", nullable = true, columnDefinition = "INT")
    private Integer minStock;
    
    // 默认仓库位置
    @Column(name = "warehouse", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String warehouse;
    
    // 物料属性（如材质、类型）
    @Column(name = "material_property", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String materialProperty;
    
    // 辅助属性（扩展描述）
    @Column(name = "auxiliary_property", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String auxiliaryProperty;

    // 无参构造函数（JPA要求）
    public Material() {}
    
    // 有参构造函数
    public Material(String materialCode) {
        this.materialCode = materialCode;
    }

    // Getter/Setter（完整生成）

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getMaterialProperty() {
        return materialProperty;
    }

    public void setMaterialProperty(String materialProperty) {
        this.materialProperty = materialProperty;
    }

    public String getAuxiliaryProperty() {
        return auxiliaryProperty;
    }

    public void setAuxiliaryProperty(String auxiliaryProperty) {
        this.auxiliaryProperty = auxiliaryProperty;
    }
}