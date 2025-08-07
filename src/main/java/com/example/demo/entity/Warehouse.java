package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 仓库信息实体类（对应数据库表 warehouses）
 */
@Entity
@Table(name = "warehouses")
public class Warehouse {

    // 主键字段
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 仓库编码
    @Column(name = "warehouse_code", unique = true)
    private String warehouseCode;

    // 仓库名称
    @Column(name = "warehouse_name")
    private String warehouseName;

    // 仓库位置
    @Column(name = "location")
    private String location;

    // 联系人
    @Column(name = "contact_person")
    private String contactPerson;

    // 联系电话
    @Column(name = "contact_phone")
    private String contactPhone;

    // 状态
    @Column(name = "status")
    private String status;

    // 描述
    @Column(name = "description")
    private String description;

    // 创建人
    @Column(name = "created_by")
    private String createdBy;

    // 创建时间
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新人
    @Column(name = "updated_by")
    private String updatedBy;

    // 更新时间
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 无参构造函数
    public Warehouse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}