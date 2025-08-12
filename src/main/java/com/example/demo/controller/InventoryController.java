package com.example.demo.controller;

import com.example.demo.entity.Inventory;
import com.example.demo.entity.InventoryTransaction;
import com.example.demo.entity.InventoryTransactionType;
import com.example.demo.entity.Material;
import com.example.demo.entity.Warehouse;
import com.example.demo.entity.MaterialBatch;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.InventoryTransactionRepository;
import com.example.demo.repository.MaterialRepository;
import com.example.demo.repository.WarehouseRepository;
import com.example.demo.repository.InventoryTransactionTypeRepository;
import com.example.demo.service.MaterialBatchService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class InventoryController {

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private InventoryTransactionTypeRepository inventoryTransactionTypeRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private MaterialBatchService materialBatchService;

    @GetMapping("/inventory")
    public String inventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String materialName, // 新增物料名称参数
            @RequestParam(required = false) String warehouseName, // 新增仓库名称参数
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionTime"));
        
        // 获取所有仓库列表用于筛选下拉框
        List<Warehouse> warehouses = warehouseRepository.findAll();
        model.addAttribute("warehouses", warehouses);

        Specification<InventoryTransaction> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null && !type.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType").get("direction"), type));
            }
            if (materialName != null && !materialName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("material").get("materialName"), "%" + materialName + "%"));
            }
            if (warehouseName != null && !warehouseName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("warehouse").get("warehouseName"), warehouseName));
            }
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        // 更新查询，包含批次信息
        Page<InventoryTransaction> inventoryTransactions = inventoryTransactionRepository.findAll(
            (root, query, criteriaBuilder) -> {
                // 添加JOIN FETCH以获取关联的批次信息
                // 修复：在Hibernate 6中正确处理JOIN FETCH和分页查询
                // 只在非count查询中使用fetch join
                if (query.getResultType() == null || !query.getResultType().equals(Long.class)) {
                    query.distinct(true);
                    root.fetch("batch", jakarta.persistence.criteria.JoinType.LEFT);
                    root.fetch("material", jakarta.persistence.criteria.JoinType.LEFT);
                    root.fetch("warehouse", jakarta.persistence.criteria.JoinType.LEFT);
                    root.fetch("transactionType", jakarta.persistence.criteria.JoinType.LEFT);
                }
                return spec.toPredicate(root, query, criteriaBuilder);
            }, 
            pageable
        );

        long totalRecords = inventoryTransactionRepository.count(spec);
        long inRecords = inventoryTransactionRepository.countByTransactionTypeDirection("IN");
        long outRecords = inventoryTransactionRepository.countByTransactionTypeDirection("OUT");

        model.addAttribute("inventoryRecords", inventoryTransactions);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("inRecords", inRecords);
        model.addAttribute("outRecords", outRecords);
        model.addAttribute("selectedType", type);
        model.addAttribute("materialName", materialName); // 将物料名称传递给视图
        model.addAttribute("selectedWarehouse", warehouseName); // 将选中的仓库名称传递给视图

        return "inventory";
    }

    /**
     * 获取库存列表（用于new-inventory页面的Thymeleaf模板）
     */
    @GetMapping("/new-inventory")
    public String newInventory(Model model) {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<Inventory> availableInventories = new ArrayList<>();
        
        // 只返回有库存的物料
        for (Inventory inventory : inventories) {
            if (inventory.getQuantity() > 0) {
                availableInventories.add(inventory);
            }
        }
        
        // 获取所有库存交易类型
        List<InventoryTransactionType> transactionTypes = inventoryTransactionTypeRepository.findAll();
        
        model.addAttribute("inventoryList", availableInventories);
        model.addAttribute("transactionTypes", transactionTypes);
        return "new-inventory";
    }

    @PostMapping("/inventory/save")
    public String saveInventoryTransaction(
            @RequestParam(required = false) Long transactionTypeId, // 使用具体的交易类型ID
            @RequestParam String transactionType, // 保留原有的交易类型参数（IN/OUT）
            @RequestParam String transactionDate,
            @RequestParam(required = false) Long inventoryId, // 出库时使用
            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String specification,
            @RequestParam(required = false) String unit,
            @RequestParam int quantity,
            @RequestParam(required = false) String warehouse, // 入库时使用，出库时从库存中获取
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String batchNumber, // 入库时使用，出库时从库存中获取
            @RequestParam(required = false) String remark,
            Model model) {

    try {
        System.out.println("保存库存交易记录: transactionType=" + transactionType + ", inventoryId=" + inventoryId + ", quantity=" + quantity);
        
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        
        // 解析交易时间
        LocalDateTime transactionTime = LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 生成批次号（transactionNo）
        String transactionNo = generateTransactionNo(transactionTime);
        inventoryTransaction.setTransactionNo(transactionNo);
        System.out.println("生成的批次号: " + transactionNo);
        
        // 根据ID获取具体的交易类型
        if (transactionTypeId != null) {
            InventoryTransactionType transactionTypeEntity = inventoryTransactionTypeRepository.findById(transactionTypeId).orElse(null);
            inventoryTransaction.setTransactionType(transactionTypeEntity);
        } else {
            // 保持原有的逻辑，根据方向查找交易类型
            List<InventoryTransactionType> transactionTypes = inventoryTransactionTypeRepository.findByDirection(transactionType);
            if (!transactionTypes.isEmpty()) {
                inventoryTransaction.setTransactionType(transactionTypes.get(0));
            }
        }
        
        inventoryTransaction.setTransactionTime(transactionTime);

        Material material = null;
        
        if (materialCode != null && !materialCode.isEmpty()) {
            // 入库情况：根据物料代码查找物料
            material = materialRepository.findByMaterialCode(materialCode).orElse(null);
            System.out.println("入库物料: materialCode=" + materialCode);
        } else if (inventoryId != null) {
            // 出库情况：根据库存ID查找物料
            Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
            if (inventory != null) {
                material = inventory.getMaterial();
                System.out.println("出库物料: inventoryId=" + inventoryId);
            }
        }

        if (material == null) {
            throw new IllegalArgumentException("物料信息不存在");
        }

        inventoryTransaction.setMaterial(material);
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setNotes(remark);
        
        // 设置批次信息
        if ("IN".equals(transactionType)) {
            System.out.println("处理入库操作");
            // 入库操作
            Warehouse targetWarehouse = warehouseRepository.findByWarehouseName(warehouse).orElse(null);
            if (targetWarehouse == null) {
                throw new IllegalArgumentException("仓库信息不存在");
            }
            inventoryTransaction.setWarehouse(targetWarehouse);
            
            // 创建新的批次记录
            MaterialBatch newBatch = new MaterialBatch();
            newBatch.setBatchNumber(batchNumber != null ? batchNumber : generateTransactionNo(transactionTime));
            newBatch.setMaterial(material);
            newBatch.setWarehouse(targetWarehouse);
            newBatch.setQuantity(quantity);
            newBatch.setAvailableQuantity(quantity);
            newBatch.setCreatedBy("System");
            materialBatchService.save(newBatch);
            
            // 关联批次到交易记录
            inventoryTransaction.setBatch(newBatch);
        } else if ("OUT".equals(transactionType)) {
            System.out.println("处理出库操作: inventoryId=" + inventoryId + ", quantity=" + quantity);
            // 出库操作
            Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
            if (inventory == null) {
                throw new IllegalArgumentException("库存记录不存在");
            }
            if (inventory.getQuantity() < quantity) {
                throw new IllegalArgumentException("库存不足，当前库存: " + inventory.getQuantity());
            }
            inventoryTransaction.setWarehouse(inventory.getWarehouse());
            
            // 更新库存数量
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
            System.out.println("更新库存数量: 原数量=" + (inventory.getQuantity() + quantity) + ", 新数量=" + inventory.getQuantity());
        }

        inventoryTransactionRepository.save(inventoryTransaction);
        System.out.println("库存交易记录保存成功");

        return "redirect:/inventory";
    } catch (Exception e) {
        System.err.println("保存库存交易记录失败: " + e.getMessage());
        e.printStackTrace();
        
        // 获取库存列表和交易类型列表，以便在出错时重新显示表单
        List<Inventory> inventories = inventoryRepository.findAll();
        List<Inventory> availableInventories = new ArrayList<>();
        
        for (Inventory inventory : inventories) {
            if (inventory.getQuantity() > 0) {
                availableInventories.add(inventory);
            }
        }
        
        List<InventoryTransactionType> transactionTypes = inventoryTransactionTypeRepository.findAll();
        
        model.addAttribute("inventoryList", availableInventories);
        model.addAttribute("transactionTypes", transactionTypes);
        model.addAttribute("error", "保存出入库记录失败：" + e.getMessage());
        return "new-inventory";
    }
}

    /**
     * 生成交易批次号
     * 格式：TR+年月日（月和日不满两位就在前面+0）+（小时数字+25）+（分钟数字+15）+（秒数+秒数的1/2次方）
     * @param transactionTime 交易时间
     * @return 生成的交易号
     */
    private String generateTransactionNo(LocalDateTime transactionTime) {
        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
        
        // TR+年月日
        String datePart = "TR" + String.format("%04d%02d%02d", 
            transactionTime.getYear(), 
            transactionTime.getMonthValue(), 
            transactionTime.getDayOfMonth());
        
        // 小时数字+25
        int hour = transactionTime.getHour() + 25;
        
        // 分钟数字+15
        int minute = transactionTime.getMinute() + 15;
        
        // 秒数+秒数的1/2次方
        int second = transactionTime.getSecond();
        double secondWithSqrt = second + Math.sqrt(second);
        
        // 组合时间部分，格式化为整数部分（保留更多精度以符合要求）
        String timePart = String.format("%02d%02d%03d", hour, minute, (int) Math.round(secondWithSqrt * 10)); // 保留1位小数的精度
        
        return datePart + timePart;
    }

    /**
     * 生成交易批次号的API接口
     */
    @GetMapping("/api/generate-transaction-no")
    @ResponseBody
    public String generateTransactionNo(@RequestParam String transactionDate) {
        LocalDateTime transactionTime = LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        return generateTransactionNo(transactionTime);
    }

    /**
     * 获取库存列表的API接口（用于出库选择）
     */
    @GetMapping("/api/inventory")
    @ResponseBody
    public List<Map<String, Object>> getInventoryList() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Inventory inventory : inventories) {
            if (inventory.getQuantity() > 0) { // 只返回有库存的物料
                Map<String, Object> item = new HashMap<>();
                item.put("id", inventory.getId());
                
                Material material = inventory.getMaterial();
                Map<String, Object> materialMap = new HashMap<>();
                materialMap.put("id", material.getId());
                materialMap.put("materialCode", material.getMaterialCode());
                materialMap.put("materialName", material.getMaterialName());
                materialMap.put("specification", material.getSpecification());
                materialMap.put("unit", material.getUnit());
                item.put("material", materialMap);
                
                Warehouse warehouse = inventory.getWarehouse();
                Map<String, Object> warehouseMap = new HashMap<>();
                warehouseMap.put("id", warehouse.getId());
                warehouseMap.put("warehouseName", warehouse.getWarehouseName());
                item.put("warehouse", warehouseMap);
                
                item.put("quantity", inventory.getQuantity());
                item.put("availableQuantity", inventory.getAvailableQuantity());
                
                result.add(item);
            }
        }
        
        return result;
    }
    
    /*
    /**
     * 根据库存ID获取可用批次的API接口
     */
    /*
    @GetMapping("/api/batches")
    @ResponseBody
    public List<Map<String, Object>> getAvailableBatches(@RequestParam Long inventoryId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
        if (inventory != null) {
            List<MaterialBatch> batches = materialBatchService.findAvailableByMaterialIdAndWarehouseId(
                inventory.getMaterial().getId(), 
                inventory.getWarehouse().getId()
            );
            
            for (MaterialBatch batch : batches) {
                Map<String, Object> batchMap = new HashMap<>();
                batchMap.put("id", batch.getId());
                batchMap.put("batchNumber", batch.getBatchNumber());
                batchMap.put("quantity", batch.getQuantity());
                batchMap.put("availableQuantity", batch.getAvailableQuantity());
                batchMap.put("productionDate", batch.getProductionDate());
                batchMap.put("expiryDate", batch.getExpiryDate());
                result.add(batchMap);
            }
        }
        
        return result;
    }
    */
}
