package com.example.demo.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bom_headers")
public class BomHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bom_group")
    private String bomGroup;
    
    @Column(name = "bom_code", unique = true)
    private String bomCode;
    
    private String status;
    
    @Column(name = "material_code")
    private String materialCode;
    
    @Column(name = "material_name")
    private String materialName;
    
    private String specification;
    private String unit;
    
    private BigDecimal quantity;
    private BigDecimal cost;
    
    private String remark;
    
    @Column(name = "material_property")
    private String materialProperty;
    
    @Column(name = "auxiliary_property")
    private String auxiliaryProperty;
    
    private String creator;
    
    @Column(name = "created_date")
    private LocalDate createdDate;
    
    private String auditor;
    
    @Column(name = "audit_date")
    private LocalDate auditDate;
    
    @Column(name = "last_updater")
    private String lastUpdater;
    
    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;
    
    // Constructors
    public BomHeader() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBomGroup() {
        return bomGroup;
    }
    
    public void setBomGroup(String bomGroup) {
        this.bomGroup = bomGroup;
    }
    
    public String getBomCode() {
        return bomCode;
    }
    
    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
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
    
    public String getCreator() {
        return creator;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getAuditor() {
        return auditor;
    }
    
    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }
    
    public LocalDate getAuditDate() {
        return auditDate;
    }
    
    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }
    
    public String getLastUpdater() {
        return lastUpdater;
    }
    
    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
    }
    
    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }
    
    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}