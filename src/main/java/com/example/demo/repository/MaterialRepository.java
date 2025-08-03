package com.example.demo.repository;

import com.example.demo.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    
    @Query(value = "SELECT m.* FROM `materials` m " +
               "LEFT JOIN `low_stock_items` l ON m.id = l.material_id", nativeQuery = true)
    Page<Material> findAllWithLowStock(Pageable pageable);
    
    @Query(value = "SELECT m.* FROM `materials` m", nativeQuery = true)
    Page<Material> findAllWithStock(Pageable pageable);
    
    @Query(value = "SELECT m.* FROM `materials` m WHERE m.material_code = ?1", nativeQuery = true)
    Optional<Material> findByMaterialCode(String materialCode);
    
    @Query(value = "SELECT COALESCE(SUM(CASE WHEN ir.type = 'IN' THEN ir.quantity ELSE -ir.quantity END), 0) " +
               "FROM `inventory_records` ir WHERE ir.material_id = ?1", nativeQuery = true)
    Integer calculateStockByMaterialId(Long materialId);
    
    @Query(value = "SELECT m.* FROM `materials` m " +
               "JOIN (SELECT ir.material_id, SUM(CASE WHEN ir.type = 'IN' THEN ir.quantity ELSE -ir.quantity END) AS stock " +
               "FROM `inventory_records` ir GROUP BY ir.material_id) AS stock_data (material_id, stock) " +
               "ON m.id = stock_data.material_id " +
               "ORDER BY stock_data.stock DESC", nativeQuery = true)
    Page<Material> findAllWithCalculatedStock(Pageable pageable);
}