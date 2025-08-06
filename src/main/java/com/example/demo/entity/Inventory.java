package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 库存实体类（对应数据库表 inventory）
 * 核心作用：存储各物料在各仓库的当前库存数量
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    // 主键字段
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long id;

    // 物料ID（外键关联materials表）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // 仓库ID（外键关联warehouses表）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    // 当前库存数量
    @Column(name = "quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer quantity = 0;

    // 可用库存数量
    @Column(name = "available_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer availableQuantity = 0;

    // 锁定库存数量
    @Column(name = "locked_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer lockedQuantity = 0;

    // 最后盘点时间
    @Column(name = "last_stocktake_time")
    private LocalDateTime lastStocktakeTime;

    // 最后更新时间
    @UpdateTimestamp
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    // 无参构造函数
    public Inventory() {
    }

    // 带参数的构造函数
    public Inventory(Material material, Warehouse warehouse, Integer quantity) {
        this.material = material;
        this.warehouse = warehouse;
        this.quantity = quantity != null ? quantity : 0;
        this.availableQuantity = quantity != null ? quantity : 0;
        this.lockedQuantity = 0;
    }

    // Getter和Setter方法
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getLockedQuantity() {
        return lockedQuantity;
    }

    public void setLockedQuantity(Integer lockedQuantity) {
        this.lockedQuantity = lockedQuantity;
    }

    public LocalDateTime getLastStocktakeTime() {
        return lastStocktakeTime;
    }

    public void setLastStocktakeTime(LocalDateTime lastStocktakeTime) {
        this.lastStocktakeTime = lastStocktakeTime;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}