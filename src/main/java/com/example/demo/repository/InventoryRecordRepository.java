package com.example.demo.repository;

import com.example.demo.entity.InventoryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {
    List<InventoryRecord> findTop3ByOrderByTimeDesc();
    List<InventoryRecord> findAllByOrderByTimeDesc();
    List<InventoryRecord> findByType(String type);
    long countByType(String type);
    
    // 分页查询方法
    Page<InventoryRecord> findByType(String type, Pageable pageable);
    Page<InventoryRecord> findAll(Pageable pageable);
}