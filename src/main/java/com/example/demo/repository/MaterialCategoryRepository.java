package com.example.demo.repository;

import com.example.demo.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long> {
    
}