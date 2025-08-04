package com.example.demo.repository;

import com.example.demo.entity.LowStockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LowStockItemRepository extends JpaRepository<LowStockItem, Long> {
    List<LowStockItem> findTop3ByOrderByCurrentStockAsc();
    List<LowStockItem> findTop5ByOrderByCurrentStockAsc();
    List<LowStockItem> findAllByOrderByCurrentStockAsc();

    // 添加用于库存状态统计的查询方法
    @Query("SELECT COUNT(l) FROM LowStockItem l WHERE l.currentStock <= ?1")
    long countByCurrentStockLessThanEqual(int stock);

    @Query("SELECT COUNT(l) FROM LowStockItem l WHERE l.currentStock > ?1 AND l.currentStock <= ?2")
    long countByCurrentStockGreaterThanAndCurrentStockLessThanEqual(int lowStock, int highStock);

    @Query("SELECT COUNT(l) FROM LowStockItem l WHERE l.currentStock <= 0")
    long countOutOfStockItems();
}