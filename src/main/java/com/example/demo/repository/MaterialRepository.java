package com.example.demo.repository;

import com.example.demo.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    /**
     * 根据物料代码查找物料
     * @param materialCode 物料代码
     * @return 物料对象
     */
    Optional<Material> findByMaterialCode(String materialCode);
    
    /**
     * 根据物料代码或物料名称查找物料（分页）
     * @param materialCode 物料代码
     * @param materialName 物料名称
     * @param pageable 分页参数
     * @return 物料分页结果
     */
    Page<Material> findByMaterialCodeContainingOrMaterialNameContaining(
            String materialCode, String materialName, Pageable pageable);
}