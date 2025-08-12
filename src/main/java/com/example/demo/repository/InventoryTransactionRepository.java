package com.example.demo.repository;

import com.example.demo.entity.InventoryTransaction;
import com.example.demo.entity.InventoryTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long>, JpaSpecificationExecutor<InventoryTransaction> {
    
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
    
    /**
     * 根据交易类型统计记录数量
     * @param direction 交易方向(IN/OUT)
     * @return 记录数量
     */
    @Query("SELECT COUNT(t) FROM InventoryTransaction t WHERE t.transactionType.direction = ?1")
    long countByTransactionTypeDirection(String direction);
    
    /**
     * 根据交易类型方向分页查询记录
     * @param direction 交易方向(IN/OUT)
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.material " +
           "LEFT JOIN FETCH t.transactionType " +
           "LEFT JOIN FETCH t.warehouse " +
           "WHERE t.transactionType.direction = ?1 " +
           "ORDER BY t.transactionTime DESC")
    Page<InventoryTransaction> findByTransactionTypeDirection(String direction, Pageable pageable);
    
    /**
     * 根据物料代码查询交易记录
     * @param materialCode 物料代码
     * @param pageable 分页参数
     * @return 交易记录分页结果
     */
    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.material " +
           "LEFT JOIN FETCH t.transactionType " +
           "LEFT JOIN FETCH t.warehouse " +
           "WHERE t.material.materialCode = ?1 " +
           "ORDER BY t.transactionTime DESC")
    Page<InventoryTransaction> findByItemCode(String materialCode, Pageable pageable);

    /**
     * 根据批次ID查找交易记录
     * @param batchId 批次ID
     * @return 交易记录列表
     */
    List<InventoryTransaction> findByBatchId(Long batchId);
}