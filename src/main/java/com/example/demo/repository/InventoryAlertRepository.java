package com.example.demo.repository;

import com.example.demo.entity.InventoryAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAlertRepository extends JpaRepository<InventoryAlert, Long> {
    
    /**
     * 公共查询片段
     */
    String BASE_QUERY = "SELECT ia FROM InventoryAlert ia " +
                        "LEFT JOIN FETCH ia.material " +
                        "LEFT JOIN FETCH ia.warehouse " +
                        "WHERE ia.isProcessed = false " +
                        "ORDER BY ia.createdAt DESC";
    
    /**
     * 获取指定数量的最近库存预警记录，按创建时间倒序排列
     * @param limit 返回记录数量限制
     * @return 最近的库存预警记录
     */
    @Query(value = BASE_QUERY)
    List<InventoryAlert> findTopByOrderByCreatedAtDesc(int limit);
    
    /**
     * 获取最近的3条库存预警记录，按创建时间倒序排列
     * @return 最近的3条库存预警记录
     */
    default List<InventoryAlert> findTop3ByOrderByCreatedAtDesc() {
        return findTopByOrderByCreatedAtDesc(3);
    }
    
    /**
     * 获取最近的4条库存预警记录，按创建时间倒序排列
     * @return 最近的4条库存预警记录
     */
    default List<InventoryAlert> findTop4ByOrderByCreatedAtDesc() {
        return findTopByOrderByCreatedAtDesc(4);
    }
    
    /**
     * 分页获取库存预警记录，按创建时间倒序排列
     * @param pageable 分页参数
     * @return 库存预警记录分页结果
     */
    @Query(value = BASE_QUERY)
    Page<InventoryAlert> findByOrderByCreatedAtDesc(Pageable pageable);
}