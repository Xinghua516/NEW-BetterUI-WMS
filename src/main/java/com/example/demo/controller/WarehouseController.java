package com.example.demo.controller;

import com.example.demo.entity.Inventory;
import com.example.demo.entity.InventoryAlert;
import com.example.demo.entity.Material;
import com.example.demo.entity.MaterialCategory;
import com.example.demo.repository.InventoryAlertRepository;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.MaterialCategoryRepository;
import com.example.demo.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class WarehouseController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private InventoryAlertRepository inventoryAlertRepository;

    @Autowired
    private InventoryRepository inventoryRepository; // 新增：库存仓库

    @Autowired
    private MaterialCategoryRepository materialCategoryRepository; // 新增：物料分类仓库

    @GetMapping("/warehouse")
    public String warehouse(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 获取物料库存数据
        Page<Material> materialsPage = materialRepository.findAll(pageable);

        // 为每个物料添加供应商信息（物料来源）
        materialsPage.getContent().forEach(material -> {
            material.setMaterialProperty(material.getSupplier()); // 绑定供应商信息
            // 获取物料分类名称（零件类型）
            if (material.getCategoryId() != null) {
                Optional<MaterialCategory> categoryOptional = materialCategoryRepository.findById(material.getCategoryId());
                categoryOptional.ifPresent(category -> material.setWarehouse(category.getCategoryName())); // 绑定分类名称
            }
            // 获取物料的当前库存数量
            Optional<Inventory> inventoryOptional = inventoryRepository.findByMaterialId(material.getId());
            inventoryOptional.ifPresent(inventory -> material.setCurrentStock(inventory.getQuantity())); // 绑定库存数量
        });

        // 获取库存预警数据
        Page<InventoryAlert> inventoryAlertsPage = inventoryAlertRepository.findByOrderByCreatedAtDesc(pageable);

        // 计算统计信息
        long totalStock = materialsPage.getContent().stream()
                .mapToLong(material -> material.getCurrentStock() != null ? material.getCurrentStock() : 0)
                .sum();

        long lowStockCount = inventoryAlertsPage.getTotalElements();

        // 计算库存状态
        long severeShortage = inventoryAlertsPage.getContent().stream()
                .filter(alert -> "LOW_STOCK".equals(alert.getAlertType()) &&
                        alert.getCurrentQuantity() != null &&
                        alert.getCurrentQuantity() <= 5)
                .count();

        long stockShortage = inventoryAlertsPage.getContent().stream()
                .filter(alert -> "LOW_STOCK".equals(alert.getAlertType()) &&
                        alert.getCurrentQuantity() != null &&
                        alert.getCurrentQuantity() > 5 &&
                        alert.getCurrentQuantity() <= (alert.getThresholdValue() != null ? alert.getThresholdValue() : 10))
                .count();

        long emptyStock = inventoryAlertsPage.getContent().stream()
                .filter(alert -> "LOW_STOCK".equals(alert.getAlertType()) &&
                        alert.getCurrentQuantity() != null &&
                        alert.getCurrentQuantity() <= 0)
                .count();

        model.addAttribute("materialsPage", materialsPage);
        model.addAttribute("inventoryAlertsPage", inventoryAlertsPage);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("lowStockItemsSize", lowStockCount);
        model.addAttribute("severeShortage", severeShortage);
        model.addAttribute("stockShortage", stockShortage);
        model.addAttribute("emptyStock", emptyStock);

        return "warehouse";
    }
}