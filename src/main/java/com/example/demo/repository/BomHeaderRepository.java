package com.example.demo.repository;

import com.example.demo.entity.BomHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BomHeaderRepository extends JpaRepository<BomHeader, Long> {
    Optional<BomHeader> findByBomCode(String bomCode);
}