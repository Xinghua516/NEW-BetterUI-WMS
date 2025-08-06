package com.example.demo.repository;

import com.example.demo.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * 根据物料ID查找库存记录
     * @param materialId 物料ID
     * @return 库存记录
     */
    @Query("SELECT i FROM Inventory i WHERE i.material.id = ?1")
    Optional<Inventory> findByMaterialId(Long materialId);
    
    /**
     * 根据物料ID和仓库ID查找库存记录
     * @param materialId 物料ID
     * @param warehouseId 仓库ID
     * @return 库存记录
     */
    @Query("SELECT i FROM Inventory i WHERE i.material.id = ?1 AND i.warehouse.id = ?2")
    Optional<Inventory> findByMaterialIdAndWarehouseId(Long materialId, Long warehouseId);
}