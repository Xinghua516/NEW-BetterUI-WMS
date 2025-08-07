package com.example.demo.controller;

import com.example.demo.entity.InventoryTransaction;
import com.example.demo.entity.InventoryTransactionType;
import com.example.demo.entity.Material;
import com.example.demo.entity.Warehouse;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/inventory")
    public String inventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String materialName, // 新增物料名称参数
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionTime"));

        Specification<InventoryTransaction> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null && !type.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType").get("direction"), type));
            }
            if (materialName != null && !materialName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("material").get("materialName"), "%" + materialName + "%"));
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

        return "inventory";
    }

    @GetMapping("/new-inventory")
    public String newInventory() {
        return "new-inventory";
    }

    @PostMapping("/inventory/save")
    public String saveInventory(
            @RequestParam String transactionType,
            @RequestParam String transactionDate,
            @RequestParam String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String specification,
            @RequestParam(required = false) String unit,
            @RequestParam int quantity,
            @RequestParam String warehouse,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) String remark,
            Model model) {

        try {
            // 解析日期时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime transactionTime = LocalDateTime.parse(transactionDate, formatter);

            // 创建交易记录
            InventoryTransaction inventoryTransaction = new InventoryTransaction();
            
            // 设置交易单号
            if (batchNumber != null && !batchNumber.isEmpty()) {
                inventoryTransaction.setTransactionNo(batchNumber);
            } else {
                inventoryTransaction.setTransactionNo("TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            
            // 查找或创建交易类型
            List<InventoryTransactionType> types = inventoryTransactionTypeRepository.findByDirection(transactionType);
            InventoryTransactionType type;
            if (types.isEmpty()) {
                // 如果找不到对应方向的交易类型，创建一个默认的
                type = new InventoryTransactionType();
                type.setDirection(transactionType);
                type.setTypeName(transactionType.equals("IN") ? "入库" : "出库");
                type.setTypeCode(transactionType.equals("IN") ? "STOCK_IN" : "STOCK_OUT");
                type = inventoryTransactionTypeRepository.save(type);
            } else {
                // 使用第一个找到的类型
                type = types.get(0);
            }
            inventoryTransaction.setTransactionType(type);
            
            // 查找或创建物料
            Optional<Material> materialOpt = materialRepository.findByMaterialCode(materialCode);
            Material material;
            if (!materialOpt.isPresent()) {
                material = new Material();
                material.setMaterialCode(materialCode);
                if (materialName != null && !materialName.isEmpty()) {
                    material.setMaterialName(materialName);
                } else {
                    material.setMaterialName(materialCode); // 默认使用物料代码作为名称
                }
                if (specification != null && !specification.isEmpty()) {
                    material.setSpecification(specification);
                }
                if (unit != null && !unit.isEmpty()) {
                    material.setUnit(unit);
                } else {
                    material.setUnit("件"); // 默认单位
                }
                if (supplier != null && !supplier.isEmpty()) {
                    material.setSupplier(supplier);
                }
                material.setStatus("ACTIVE");
                material = materialRepository.save(material);
            } else {
                material = materialOpt.get();
            }
            inventoryTransaction.setMaterial(material);
            
            // 设置数量（根据交易类型调整正负号）
            if ("OUT".equals(transactionType)) {
                inventoryTransaction.setQuantity(-Math.abs(quantity)); // 出库为负数
            } else {
                inventoryTransaction.setQuantity(Math.abs(quantity)); // 入库为正数
            }
            
            // 查找或创建仓库
            Optional<Warehouse> warehouseOpt = warehouseRepository.findByWarehouseName(warehouse);
            Warehouse wh;
            if (!warehouseOpt.isPresent()) {
                wh = new Warehouse();
                wh.setWarehouseName(warehouse);
                wh.setStatus("ACTIVE");
                wh = warehouseRepository.save(wh);
            } else {
                wh = warehouseOpt.get();
            }
            inventoryTransaction.setWarehouse(wh);
            
            // 设置交易时间
            inventoryTransaction.setTransactionTime(transactionTime);
            
            // 设置备注
            inventoryTransaction.setNotes(remark);
            
            // 设置创建人（这里使用默认值，实际应用中应该从会话中获取）
            inventoryTransaction.setCreatedBy("System");
            
            inventoryTransactionRepository.save(inventoryTransaction);

            // 保存成功后重定向到出入库记录查询页面
            return "redirect:/inventory";
        } catch (Exception e) {
            // 出现错误时返回错误信息并重新显示表单
            e.printStackTrace();
            model.addAttribute("error", "保存失败: " + e.getMessage());
            return "new-inventory";
        }
    }
}