package com.example.demo.controller;

import com.example.demo.entity.InventoryRecord;
import com.example.demo.entity.LowStockItem;
import com.example.demo.entity.Material;
import com.example.demo.repository.InventoryRecordRepository;
import com.example.demo.repository.LowStockItemRepository;
import com.example.demo.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    
    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;

    @Autowired
    private LowStockItemRepository lowStockItemRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @GetMapping("/")
    public String index(Model model) {
        try {
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
            model.addAttribute("recentRecords", recentRecords != null ? recentRecords : List.of());
            
            // 从数据库获取低库存报警
            List<LowStockItem> lowStockItems = lowStockItemRepository.findTop3ByOrderByCurrentStockAsc();
            model.addAttribute("lowStockItems", lowStockItems != null ? lowStockItems : List.of());
            
            // 添加物料类型统计（基于实际数据计算）
            Map<String, Integer> materialTypeCount = calculateMaterialTypeStatistics();
            model.addAttribute("materialTypeCount", materialTypeCount);
            model.addAttribute("materialTypePercentage", materialTypeCount);

            // 添加库存空间使用情况
            Map<String, Integer> spaceUsage = calculateSpaceUsage();
            model.addAttribute("spaceUsage", spaceUsage);
        } catch (Exception e) {
            // 如果出现任何异常，提供默认值确保页面可以正常显示
            model.addAttribute("totalItems", 0L);
            model.addAttribute("warehouses", 0L);
            model.addAttribute("recentRecords", List.of());
            model.addAttribute("lowStockItems", List.of());
            
            Map<String, Integer> defaultMaterialType = new HashMap<>();
            defaultMaterialType.put("默认仓库", 100);
            model.addAttribute("materialTypeCount", defaultMaterialType);
            model.addAttribute("materialTypePercentage", defaultMaterialType);
            
            Map<String, Integer> defaultSpaceUsage = new HashMap<>();
            defaultSpaceUsage.put("usedSpace", 0);
            defaultSpaceUsage.put("availableSpace", 100);
            model.addAttribute("spaceUsage", defaultSpaceUsage);
        }
        
        return "index";
    }
    
    /**
     * 计算物料类型统计数据
     * @return 物料类型统计映射
     */
    private Map<String, Integer> calculateMaterialTypeStatistics() {
        Map<String, Integer> typeCount = new HashMap<>();
        
        try {
            // 从数据库获取所有物料
            List<Material> materials = materialRepository.findAll();
            
            // 按仓库分组统计
            Map<String, Long> warehouseCount = materials.stream()
                .filter(m -> m.getWarehouse() != null && !m.getWarehouse().trim().isEmpty())
                .collect(Collectors.groupingBy(Material::getWarehouse, Collectors.counting()));
            
            // 计算总数
            long total = warehouseCount.values().stream().mapToLong(Long::longValue).sum();
            
            // 转换为百分比（基于总数量）
            if (total > 0) {
                for (Map.Entry<String, Long> entry : warehouseCount.entrySet()) {
                    int percentage = Math.round((float) entry.getValue() / total * 100);
                    typeCount.put(entry.getKey(), percentage);
                }
            } else {
                // 如果没有数据，添加默认值避免前端错误
                typeCount.put("默认仓库", 100);
            }
        } catch (Exception e) {
            // 如果计算过程中出现异常，返回默认值
            typeCount.put("默认仓库", 100);
        }
        
        return typeCount;
    }
    
    /**
     * 计算库存空间使用情况
     * @return 库存空间使用情况映射
     */
    private Map<String, Integer> calculateSpaceUsage() {
        Map<String, Integer> spaceUsage = new HashMap<>();

        try {
            // 使用现有的count方法获取库存记录总数作为示例
            long totalRecords = inventoryRecordRepository.count();
            
            // 这里只是一个示例计算，实际应用中应该根据业务需求计算空间使用情况
            // 假设总空间为1000单位，每条记录占用1单位空间
            int totalSpace = 1000;
            int usedSpace = (int) Math.min(totalRecords, totalSpace);
            
            if (totalSpace > 0) {
                int availableSpace = totalSpace - usedSpace;
                spaceUsage.put("usedSpace", usedSpace);
                spaceUsage.put("availableSpace", availableSpace);
            } else {
                // 如果没有数据，添加默认值避免前端错误
                spaceUsage.put("usedSpace", 0);
                spaceUsage.put("availableSpace", 100);
            }
        } catch (Exception e) {
            // 如果计算过程中出现异常，返回默认值
            spaceUsage.put("usedSpace", 0);
            spaceUsage.put("availableSpace", 100);
        }

        return spaceUsage;
    }
}