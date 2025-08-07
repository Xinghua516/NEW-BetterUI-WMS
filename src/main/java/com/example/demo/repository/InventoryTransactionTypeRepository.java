package com.example.demo.repository;

import com.example.demo.entity.InventoryTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryTransactionTypeRepository extends JpaRepository<InventoryTransactionType, Long> {
    List<InventoryTransactionType> findByDirection(String direction);
}