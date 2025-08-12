package com.example.demo.service;

import com.example.demo.entity.MaterialBatch;
import com.example.demo.repository.MaterialBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialBatchService {

    @Autowired
    private MaterialBatchRepository materialBatchRepository;

    /**
     * 根据批次号查找批次
     * @param batchNumber 批次号
     * @return 物料批次对象
     */
    public Optional<MaterialBatch> findByBatchNumber(String batchNumber) {
        return materialBatchRepository.findByBatchNumber(batchNumber);
    }

    /**
     * 根据物料ID查找所有批次
     * @param materialId 物料ID
     * @return 物料批次列表
     */
    public List<MaterialBatch> findByMaterialId(Long materialId) {
        return materialBatchRepository.findByMaterialId(materialId);
    }

    /**
     * 根据物料ID和仓库ID查找所有批次
     * @param materialId 物料ID
     * @param warehouseId 仓库ID
     * @return 物料批次列表
     */
    public List<MaterialBatch> findByMaterialIdAndWarehouseId(Long materialId, Long warehouseId) {
        return materialBatchRepository.findByMaterialIdAndWarehouseId(materialId, warehouseId);
    }

    /**
     * 根据物料ID和仓库ID查找有库存的批次
     * @param materialId 物料ID
     * @param warehouseId 仓库ID
     * @return 有库存的物料批次列表
     */
    public List<MaterialBatch> findAvailableByMaterialIdAndWarehouseId(Long materialId, Long warehouseId) {
        return materialBatchRepository.findAvailableByMaterialIdAndWarehouseId(materialId, warehouseId);
    }

    /**
     * 根据物料ID查找有库存的批次
     * @param materialId 物料ID
     * @return 有库存的物料批次列表
     */
    public List<MaterialBatch> findAvailableByMaterialId(Long materialId) {
        return materialBatchRepository.findAvailableByMaterialId(materialId);
    }

    /**
     * 保存物料批次
     * @param materialBatch 物料批次对象
     * @return 保存后的物料批次对象
     */
    public MaterialBatch save(MaterialBatch materialBatch) {
        return materialBatchRepository.save(materialBatch);
    }

    /**
     * 根据ID查找物料批次
     * @param id 批次ID
     * @return 物料批次对象
     */
    public Optional<MaterialBatch> findById(Long id) {
        return materialBatchRepository.findById(id);
    }

    /**
     * 删除物料批次
     * @param id 批次ID
     */
    public void deleteById(Long id) {
        materialBatchRepository.deleteById(id);
    }

    /**
     * 生成批次号
     * @param prefix 前缀
     * @return 生成的批次号
     */
    public String generateBatchNumber(String prefix) {
        // 生成批次号：前缀+年月日+4位随机数
        LocalDate now = LocalDate.now();
        String dateStr = now.toString().replace("-", "");
        int randomNum = (int) (Math.random() * 9000) + 1000; // 1000-9999的随机数
        return prefix.toUpperCase() + dateStr + randomNum;
    }
}