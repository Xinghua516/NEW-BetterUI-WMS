package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bom_items")
public class BomItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_header_id")
    private BomHeader bomHeader;
    
    @Column(name = "seq_no")
    private Integer seqNo;
    
    @Column(name = "material_code")
    private String materialCode;
    
    @Column(name = "material_name")
    private String materialName;
    
    private String specification;
    
    @Column(name = "material_property")
    private String materialProperty;
    
    @Column(name = "auxiliary_property")
    private String auxiliaryProperty;
    
    private String unit;
    
    private BigDecimal quantity;
    
    @Column(name = "loss_rate")
    private BigDecimal lossRate;
    
    private String status;
    
    private String warehouse;
    
    // 库存相关字段
    private Integer currentStock;
    private Integer minStock;
    
    // 新增字段的getter和setter
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

    // Constructors
    public BomItem() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BomHeader getBomHeader() {
        return bomHeader;
    }
    
    public void setBomHeader(BomHeader bomHeader) {
        this.bomHeader = bomHeader;
    }
    
    public Integer getSeqNo() {
        return seqNo;
    }
    
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
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
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getLossRate() {
        return lossRate;
    }
    
    public void setLossRate(BigDecimal lossRate) {
        this.lossRate = lossRate;
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
}