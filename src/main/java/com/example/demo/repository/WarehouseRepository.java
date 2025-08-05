package com.example.demo.repository;

import com.example.demo.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);
    
    Optional<Warehouse> findByWarehouseName(String warehouseName);
}