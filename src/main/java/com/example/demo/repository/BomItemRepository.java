package com.example.demo.repository;

import com.example.demo.entity.BomItem;
import com.example.demo.entity.BomHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BomItemRepository extends JpaRepository<BomItem, Long> {
    List<BomItem> findByBomHeaderOrderBySeqNoAsc(BomHeader bomHeader);
    List<BomItem> findByMaterialCode(String materialCode);
}