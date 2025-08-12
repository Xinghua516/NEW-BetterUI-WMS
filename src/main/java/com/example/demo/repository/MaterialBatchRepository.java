package com.example.demo.repository;

import com.example.demo.entity.MaterialBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialBatchRepository extends JpaRepository<MaterialBatch, Long> {
    /**
     * 根据批次号查找批次
     * @param batchNumber 批次号
     * @return 物料批次对象
     */
    Optional<MaterialBatch> findByBatchNumber(String batchNumber);
    
    /**
     * 根据物料ID查找所有批次
     * @param materialId 物料ID
     * @return 物料批次列表
     */
    List<MaterialBatch> findByMaterialId(Long materialId);
    
    /**
     * 根据物料ID和仓库ID查找所有批次
     * @param materialId 物料ID
     * @param warehouseId 仓库ID
     * @return 物料批次列表
     */
    List<MaterialBatch> findByMaterialIdAndWarehouseId(Long materialId, Long warehouseId);
    
    /**
     * 根据物料ID和仓库ID查找有库存的批次
     * @param materialId 物料ID
     * @param warehouseId 仓库ID
     * @return 有库存的物料批次列表
     */
    @Query("SELECT mb FROM MaterialBatch mb WHERE mb.material.id = ?1 AND mb.warehouse.id = ?2 AND mb.quantity > 0 AND mb.isActive = true")
    List<MaterialBatch> findAvailableByMaterialIdAndWarehouseId(Long materialId, Long warehouseId);
    
    /**
     * 根据物料ID查找有库存的批次
     * @param materialId 物料ID
     * @return 有库存的物料批次列表
     */
    @Query("SELECT mb FROM MaterialBatch mb WHERE mb.material.id = ?1 AND mb.quantity > 0 AND mb.isActive = true")
    List<MaterialBatch> findAvailableByMaterialId(Long materialId);
}