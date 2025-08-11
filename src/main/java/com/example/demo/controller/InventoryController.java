package com.example.demo.controller;

import com.example.demo.entity.Inventory;
import com.example.demo.entity.InventoryTransaction;
import com.example.demo.entity.InventoryTransactionType;
import com.example.demo.entity.Material;
import com.example.demo.entity.Warehouse;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.InventoryTransactionRepository;
import com.example.demo.repository.MaterialRepository;
import com.example.demo.repository.WarehouseRepository;
import com.example.demo.repository.InventoryTransactionTypeRepository;
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

        Page<InventoryTransaction> inventoryTransactions = inventoryTransactionRepository.findAll(spec, pageable);

        long totalRecords = inventoryTransactionRepository.count();
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

    @GetMapping("/new-inventory")
    public String newInventory() {
        return "new-inventory";
    }

    @PostMapping("/inventory/save")
    public String saveInventoryTransaction(
            @RequestParam String transactionType,
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
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setTransactionType(inventoryTransactionTypeRepository.findByDirection(transactionType).orElse(null));
        inventoryTransaction.setTransactionTime(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Material material = null;
        if (materialCode != null && !materialCode.isEmpty()) {
            material = materialRepository.findByMaterialCode(materialCode).orElse(null);
        } else if (inventoryId != null) {
            Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
            if (inventory != null) {
                material = inventory.getMaterial();
                // 出库时从库存中获取批次号
                batchNumber = inventory.getBatchNumber();
            }
        }

        if (material == null) {
            throw new IllegalArgumentException("物料信息不存在");
        }

        inventoryTransaction.setMaterial(material);
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setRemark(remark);

        if ("IN".equals(transactionType)) {
            // 入库操作
            Warehouse targetWarehouse = warehouseRepository.findByWarehouseName(warehouse).orElse(null);
            if (targetWarehouse == null) {
                throw new IllegalArgumentException("仓库信息不存在");
            }
            inventoryTransaction.setWarehouse(targetWarehouse);
            inventoryTransaction.setSupplier(supplier);
            inventoryTransaction.setBatchNumber(batchNumber); // 入库时使用用户输入的批次号
        } else if ("OUT".equals(transactionType)) {
            // 出库操作
            Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
            if (inventory == null || inventory.getQuantity() < quantity) {
                throw new IllegalArgumentException("库存不足");
            }
            inventoryTransaction.setWarehouse(inventory.getWarehouse());
            inventoryTransaction.setBatchNumber(batchNumber); // 出库时使用从库存中获取的批次号
            // 更新库存数量
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
        }

        inventoryTransactionRepository.save(inventoryTransaction);

        return "redirect:/inventory";
    } catch (Exception e) {
        model.addAttribute("error", "保存出入库记录失败：" + e.getMessage());
        return "new-inventory";
    }
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
}