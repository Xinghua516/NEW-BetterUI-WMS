package com.example.demo.controller;

import com.example.demo.entity.BomHeader;
import com.example.demo.entity.BomItem;
import com.example.demo.entity.InventoryRecord;
import com.example.demo.entity.LowStockItem;
import com.example.demo.entity.Material;
import com.example.demo.repository.BomHeaderRepository;
import com.example.demo.repository.BomItemRepository;
import com.example.demo.repository.InventoryRecordRepository;
import com.example.demo.repository.LowStockItemRepository;
import com.example.demo.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;

    @Autowired
    private LowStockItemRepository lowStockItemRepository;
    
    @Autowired
    private BomHeaderRepository bomHeaderRepository;
    
    @Autowired
    private BomItemRepository bomItemRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @GetMapping("/")
    public String index(Model model) {
        // 添加当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("currentTime", now.format(formatter));
        
        // 从数据库获取仓库统计数据
        long totalItems = inventoryRecordRepository.count();
        model.addAttribute("totalItems", totalItems);
        
        List<LowStockItem> allLowStockItems = lowStockItemRepository.findAll();
        long totalWarehouses = allLowStockItems.stream()
            .map(LowStockItem::getWarehouse)
            .distinct()
            .count();
        model.addAttribute("warehouses", totalWarehouses);
        
        // 从数据库获取最近3小时出入库记录
        List<InventoryRecord> recentRecords = inventoryRecordRepository.findTop3ByOrderByTimeDesc();
        model.addAttribute("recentRecords", recentRecords);
        
        // 从数据库获取低库存报警
        List<LowStockItem> lowStockItems = lowStockItemRepository.findTop3ByOrderByCurrentStockAsc();
        model.addAttribute("lowStockItems", lowStockItems);
        
        // 添加物料类型统计（基于实际数据计算）
        Map<String, Integer> materialTypeCount = calculateMaterialTypeStatistics(allLowStockItems);
        model.addAttribute("materialTypeCount", materialTypeCount);
        model.addAttribute("materialTypePercentage", materialTypeCount);
        
        // 添加库存空间使用情况
        Map<String, Integer> spaceUsage = new HashMap<>();
        spaceUsage.put("usedSpace", 35); // 示例数据，实际应从数据库获取
        spaceUsage.put("availableSpace", 65); // 示例数据，实际应从数据库获取
        model.addAttribute("spaceUsage", spaceUsage);
        
        return "index";
    }
    
    /**
     * 计算物料类型统计数据
     * @param items 低库存项目列表
     * @return 物料类型统计映射
     */
    private Map<String, Integer> calculateMaterialTypeStatistics(List<LowStockItem> items) {
        Map<String, Integer> typeCount = new HashMap<>();
        
        // 初始化各类别计数
        typeCount.put("原材料", 0);
        typeCount.put("半成品", 0);
        typeCount.put("成品", 0);
        typeCount.put("辅料", 0);
        typeCount.put("包装材料", 0);
        
        // 统计各类别数量
        for (LowStockItem item : items) {
            String type = item.getWarehouse(); // 使用仓库字段作为类型示例
            if (type != null) {
                // 简化处理，根据仓库名称判断物料类型
                if (type.contains("原料") || type.contains("材料")) {
                    typeCount.put("原材料", typeCount.get("原材料") + 1);
                } else if (type.contains("半成品")) {
                    typeCount.put("半成品", typeCount.get("半成品") + 1);
                } else if (type.contains("成品")) {
                    typeCount.put("成品", typeCount.get("成品") + 1);
                } else if (type.contains("辅料")) {
                    typeCount.put("辅料", typeCount.get("辅料") + 1);
                } else if (type.contains("包装")) {
                    typeCount.put("包装材料", typeCount.get("包装材料") + 1);
                } else {
                    // 默认归类为原材料
                    typeCount.put("原材料", typeCount.get("原材料") + 1);
                }
            } else {
                // 默认归类为原材料
                typeCount.put("原材料", typeCount.get("原材料") + 1);
            }
        }
        
        // 转换为百分比（基于总数量）
        int total = items.size();
        if (total > 0) {
            for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
                int percentage = Math.round((float) entry.getValue() / total * 100);
                typeCount.put(entry.getKey(), percentage);
            }
        }
        
        return typeCount;
    }
    
    @GetMapping("/parts")
    public String parts(Model model) {
        // 获取BOM清单头信息
        List<BomHeader> bomHeaders = bomHeaderRepository.findAll();
        model.addAttribute("bomHeaders", bomHeaders);
        return "parts-query";
    }
    
    @GetMapping("/parts-query")
    public String partsQuery() {
        return "parts-query";
    }
    
    @GetMapping("/warehouse")
    public String warehouse(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 获取物料库存数据（使用计算后的库存数据）
        Page<Material> materialsPage = materialRepository.findAllWithCalculatedStock(pageable);
        
        // 获取低库存预警数据
        Page<LowStockItem> lowStockPage = lowStockItemRepository.findAll(pageable);
        
        // 计算统计信息
        int totalStock = calculateTotalStock(materialsPage);
        int lowStockCount = (int) lowStockItemRepository.count();
        
        // 计算库存状态
        int severeShortage = lowStockItemRepository.countByCurrentStockLessThanEqual(5);
        int stockShortage = lowStockItemRepository.countByCurrentStockGreaterThanAndCurrentStockLessThanEqual(5, 10);
        int emptyStock = lowStockItemRepository.countByCurrentStockLessThanEqual(0);
        
        model.addAttribute("materialsPage", materialsPage);
        model.addAttribute("lowStockPage", lowStockPage);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("lowStockItemsSize", lowStockCount);
        model.addAttribute("severeShortage", severeShortage);
        model.addAttribute("stockShortage", stockShortage);
        model.addAttribute("emptyStock", emptyStock);
        
        return "warehouse";
    }
    
    private int calculateTotalStock(Page<Material> materialsPage) {
        // 直接使用Material对象中的currentStock属性计算总库存
        return materialsPage.getContent().stream()
            .mapToInt(material -> {
                Integer stock = material.getCurrentStock();
                return stock != null ? stock : 0;
            })
            .sum();
    }
    
    @GetMapping("/ai-assistant")
    public String aiAssistant() {
        return "ai-assistant.html";
    }
    
    @GetMapping("/bom/{bomCode}")
    public String bomDetail(@PathVariable String bomCode, Model model) {
        // 获取BOM清单详情
        BomHeader bomHeader = bomHeaderRepository.findByBomCode(bomCode).orElse(null);
        if (bomHeader != null) {
            List<BomItem> bomItems = bomItemRepository.findByBomHeaderOrderBySeqNoAsc(bomHeader);
            model.addAttribute("bomHeader", bomHeader);
            model.addAttribute("bomItems", bomItems);
        }
        return "bom-detail";
    }
    
    @GetMapping("/inventory")
    public String inventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            Model model) {
        
        // 创建分页请求，按时间倒序排列
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));
        
        // 根据类型筛选记录，如果类型为空则获取所有记录
        Page<InventoryRecord> inventoryRecords;
        if (type == null || type.isEmpty()) {
            inventoryRecords = inventoryRecordRepository.findAll(pageable);
        } else {
            inventoryRecords = inventoryRecordRepository.findByType(type, pageable);
        }
        
        // 计算统计信息
        long totalRecords = inventoryRecordRepository.count();
        long inRecords = inventoryRecordRepository.countByType("IN");
        long outRecords = inventoryRecordRepository.countByType("OUT");
        
        model.addAttribute("inventoryRecords", inventoryRecords);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("inRecords", inRecords);
        model.addAttribute("outRecords", outRecords);
        model.addAttribute("selectedType", type);
        
        return "inventory";
    }
    
    // 添加获取BOM明细的方法
    @GetMapping("/bom-items")
    public String getBomItems(Model model, Pageable pageable) {
        Page<BomItem> bomItemsPage = bomItemRepository.findAll(pageable);
        model.addAttribute("bomItems", bomItemsPage);
        return "warehouse :: #stockTableBody";
    }
}