package com.example.demo.repository;

import com.example.demo.entity.LowStockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LowStockItemRepository extends JpaRepository<LowStockItem, Long> {

    // 查询所有低库存零件
    List<LowStockItem> findAllByOrderByCurrentStockAsc();

    // 查询前3个低库存零件
    List<LowStockItem> findTop3ByOrderByCurrentStockAsc();

    // 查询前5个低库存零件
    List<LowStockItem> findTop5ByOrderByCurrentStockAsc();

    // 根据物料ID查询低库存零件
    Optional<LowStockItem> findByMaterialId(Long materialId);

    // 添加用于库存状态统计的查询方法
    @Query("SELECT COUNT(l) FROM LowStockItem l WHERE l.currentStock <= ?1")
    long countByCurrentStockLessThanEqual(int stock);

    @Query("SELECT COUNT(l) FROM LowStockItem l WHERE l.currentStock > ?1 AND l.currentStock <= ?2")
    long countByCurrentStockGreaterThanAndCurrentStockLessThanEqual(int lowStock, int highStock);

    @Query("SELECT COUNT(l) FROM LowStockItem l WHERE l.currentStock <= 0")
    long countOutOfStockItems();
}