package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PartsController {
    
    @Autowired
    private BomHeaderRepository bomHeaderRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private LowStockItemRepository lowStockItemRepository;
    
    @Autowired
    private InventoryAlertRepository inventoryAlertRepository;
    
    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;
    
    @Autowired
    private MaterialCategoryRepository materialCategoryRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @GetMapping("/parts")
    public String parts(Model model) {
        // 获取BOM清单头信息
        List<BomHeader> bomHeaders = bomHeaderRepository.findAll();
        model.addAttribute("bomHeaders", bomHeaders);
        return "parts-query";
    }
    
    @GetMapping("/parts-query")
    public String partsQuery(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            // 库存预警数据现在通过前端调用 /api/inventory-alerts 接口获取，无需在此查询
            
            // 分页查询materials数据
            Pageable pageable = PageRequest.of(page, size);
            Page<Material> materialsPage;
            
            if (keyword != null && !keyword.isEmpty()) {
                // 如果有搜索关键词，执行搜索
                materialsPage = materialRepository.findByMaterialCodeContainingOrMaterialNameContaining(
                        keyword, keyword, pageable);
            } else {
                // 否则获取所有零件信息
                materialsPage = materialRepository.findAll(pageable);
            }
            
            model.addAttribute("materialsPage", materialsPage);
            model.addAttribute("keyword", keyword);
            
            // 同时添加一个标志表示是否查询成功
            model.addAttribute("querySuccess", true);
        } catch (Exception e) {
            // 添加错误处理
            e.printStackTrace();
            model.addAttribute("error", "数据库查询出现错误：" + e.getMessage());
            model.addAttribute("lowStockItems", new ArrayList<InventoryAlert>()); // 添加空列表避免模板出错
            model.addAttribute("querySuccess", false);
        }
        
        return "parts-query"; // 返回对应的 Thymeleaf 模板
    }
        /**
     * API端点：获取分页的库存预警数据
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页的库存预警数据
     */
    @GetMapping("/api/inventory-alerts")
    @ResponseBody
    public Page<InventoryAlert> getInventoryAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "14") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryAlertRepository.findByOrderByCreatedAtDesc(pageable);
    }

    @GetMapping("/material/{id}")
    public String materialDetail(@PathVariable Long id, Model model) {
        // 获取零件详细信息
        Optional<Material> materialOptional = materialRepository.findById(id);
        if (materialOptional.isPresent()) {
            Material material = materialOptional.get();
            model.addAttribute("material", material);

            // 获取该零件的出入库记录
            Pageable pageable = PageRequest.of(0, 10); // 默认分页参数
            Page<InventoryTransaction> inventoryTransactions = inventoryTransactionRepository.findByItemCode(material.getMaterialCode(), pageable);
            // 确保即使没有记录也传递一个空列表而不是null
            model.addAttribute("inventoryTransactions", inventoryTransactions != null ? inventoryTransactions.getContent() : new ArrayList<>());

            // 新增：获取所属分类名称
            if (material.getCategoryId() != null) {
                Optional<MaterialCategory> categoryOptional = materialCategoryRepository.findById(material.getCategoryId());
                if (categoryOptional.isPresent()) {
                    model.addAttribute("categoryName", categoryOptional.get().getCategoryName());
                } else {
                    model.addAttribute("categoryName", "-");
                }
            } else {
                model.addAttribute("categoryName", "-");
            }
            
            // 新增：获取默认仓库名称
            if (material.getDefaultWarehouseId() != null) {
                Optional<Warehouse> warehouseOptional = warehouseRepository.findById(material.getDefaultWarehouseId());
                if (warehouseOptional.isPresent()) {
                    model.addAttribute("defaultWarehouseName", warehouseOptional.get().getWarehouseName());
                } else {
                    model.addAttribute("defaultWarehouseName", "-");
                }
            } else {
                model.addAttribute("defaultWarehouseName", "-");
            }
            
            // 新增：获取当前库存数量
            Optional<Inventory> inventoryOptional = inventoryRepository.findByMaterialId(material.getId());
            if (inventoryOptional.isPresent()) {
                model.addAttribute("currentStock", inventoryOptional.get().getQuantity());
            } else {
                model.addAttribute("currentStock", 0);
            }
        } else {
            // 处理零件不存在的情况
            return "redirect:/parts-query"; // 重定向到零件查询页面
        }

        return "material-detail";
    }
    
    @GetMapping("/api/low-stock-items")
    @ResponseBody
    public List<LowStockItem> getLowStockItems() {
        return lowStockItemRepository.findTop5ByOrderByCurrentStockAsc();
    }
}