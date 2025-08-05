package com.example.demo.repository;

import com.example.demo.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    
    /**
     * 获取最近的3条出入库记录，按时间倒序排列
     * @return 最近的3条出入库记录
     */
    @Query(value = "SELECT t FROM InventoryTransaction t " +
                   "LEFT JOIN FETCH t.material " +
                   "LEFT JOIN FETCH t.transactionType " +
                   "ORDER BY t.transactionTime DESC")
    List<InventoryTransaction> findTop3ByOrderByTransactionTimeDesc();
    
    /**
     * 获取最近1天的出入库记录，按时间倒序排列，最多4条
     * @param oneDayAgo 一天前的时间点
     * @return 最近1天的出入库记录
     */
    List<InventoryTransaction> findTop4ByTransactionTimeAfterOrderByTransactionTimeDesc(LocalDateTime oneDayAgo);
}