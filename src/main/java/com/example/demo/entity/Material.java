package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

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

    // 规格型号（可为空）
    @Column(name = "specification", nullable = true, length = 200, columnDefinition = "VARCHAR(200)")
    private String specification;

    // 物料属性（如材质、类型）
    @Column(name = "material_property", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String materialProperty;

    // 辅助属性（扩展描述）
    @Column(name = "auxiliary_property", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String auxiliaryProperty;

    // 单位（如件、个）
    @Column(name = "unit", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private String unit;

    // 使用状态（启用/停用）
    @Column(name = "status", nullable = true, length = 20, columnDefinition = "VARCHAR(20)")
    private String status;

    // 默认仓库位置
    @Column(name = "warehouse", nullable = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String warehouse;

    // 当前库存数量
    @Column(name = "current_stock", nullable = true, columnDefinition = "INT")
    private Integer currentStock;

    // 最低库存阈值（低于此值触发预警）
    @Column(name = "min_stock", nullable = true, columnDefinition = "INT")
    private Integer minStock;

    // 创建时间（自动生成）
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    // 更新时间（自动生成）
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;

    // 无参构造函数（JPA要求）
    public Material() {}

    // 全参构造函数（用于测试数据初始化）
    public Material(String materialCode, String materialName, String specification, String unit) {
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.specification = specification;
        this.unit = unit;
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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}