package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_transaction_types")
public class InventoryTransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type_code")
    private String typeCode;
    
    @Column(name = "type_name")
    private String typeName;
    
    @Column(name = "direction")
    private String direction;
    
    // Constructors
    public InventoryTransactionType() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTypeCode() {
        return typeCode;
    }
    
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
}