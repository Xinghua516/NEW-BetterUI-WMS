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
    
    @Query("SELECT m FROM Material m")
    Page<Material> findAllWithStock(Pageable pageable);
    
    Optional<Material> findByMaterialCode(String materialCode);
    
    Page<Material> findByMaterialCodeContainingOrMaterialNameContaining(
        String materialCode, String materialName, Pageable pageable);
}