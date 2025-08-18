package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    @Autowired
    private MaterialBatchRepository materialBatchRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(PartsController.class);

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
            logger.error("数据库查询出现错误：", e);
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
    public String getMaterialDetails(@PathVariable Long id, Model model) {
        try {
            Optional<Material> materialOpt = materialRepository.findById(id);
            if (materialOpt.isPresent()) {
                Material material = materialOpt.get();
                model.addAttribute("material", material);
                
                // 获取库存信息
                Optional<Inventory> inventoryOpt = inventoryRepository.findByMaterialId(id);
                List<Inventory> inventoryList = inventoryOpt.map(List::of).orElse(List.of());
                model.addAttribute("inventoryList", inventoryList);
                
                // 获取交易记录
                Pageable pageable = PageRequest.of(0, 10);
                // 修复：使用正确的方法名
                Page<InventoryTransaction> transactionPage = inventoryTransactionRepository.findByItemCode(material.getMaterialCode(), pageable);
                model.addAttribute("transactionPage", transactionPage);
                
                // 获取批次信息
                List<MaterialBatch> batchList = materialBatchRepository.findByMaterialId(id);
                model.addAttribute("batchList", batchList);
            } else {
                model.addAttribute("error", "物料不存在");
            }
        } catch (Exception e) {
            logger.error("获取物料详情时发生错误: ", e);
            model.addAttribute("error", "获取物料详情失败: " + e.getMessage());
        }
        return "material-detail";
    }
    
    @GetMapping("/api/low-stock-items")
    @ResponseBody
    public List<LowStockItem> getLowStockItems() {
        return lowStockItemRepository.findTop5ByOrderByCurrentStockAsc();
    }
}