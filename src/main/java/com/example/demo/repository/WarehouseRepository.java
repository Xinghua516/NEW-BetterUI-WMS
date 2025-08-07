package com.example.demo.repository;

import com.example.demo.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    /**
     * 根据仓库名称查找仓库
     * @param warehouseName 仓库名称
     * @return 仓库对象
     */
    Optional<Warehouse> findByWarehouseName(String warehouseName);
}