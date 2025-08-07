package com.example.demo.controller;

import com.example.demo.entity.InventoryAlert;
import com.example.demo.entity.InventoryAlertDTO;
import com.example.demo.entity.InventoryTransaction;
import com.example.demo.entity.InventoryTransactionDTO;
import com.example.demo.entity.Material;
import com.example.demo.entity.MaterialCategory;
import com.example.demo.entity.Warehouse;

import com.example.demo.repository.InventoryAlertRepository;
import com.example.demo.repository.InventoryTransactionRepository;
import com.example.demo.repository.MaterialRepository;
import com.example.demo.repository.MaterialCategoryRepository;
import com.example.demo.repository.WarehouseRepository;
import com.example.demo.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;
    
    @Autowired
    private InventoryAlertRepository inventoryAlertRepository;

    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private MaterialCategoryRepository materialCategoryRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            Model model) {
        try {
            // 添加当前时间
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            model.addAttribute("currentTime", now.format(formatter));

            // 从数据库获取最近1天出入库记录
            LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
            List<InventoryTransaction> recentTransactions = inventoryTransactionRepository
                .findTop4ByTransactionTimeAfterOrderByTransactionTimeDesc(oneDayAgo);
            List<InventoryTransactionDTO> recentRecords = recentTransactions.stream()
                .map(InventoryTransactionDTO::new)
                .collect(Collectors.toList());
            model.addAttribute("recentRecords", recentRecords != null ? recentRecords : List.of());
            
            // 从数据库获取库存预警（包括库存过低和过高）- 分页（用于右下角组件）
            Pageable pageable = PageRequest.of(page, size);
            Page<InventoryAlert> inventoryAlertsPage = inventoryAlertRepository.findByOrderByCreatedAtDesc(pageable);
            List<InventoryAlertDTO> inventoryAlertDTOs = inventoryAlertsPage.getContent().stream()
                .map(InventoryAlertDTO::new)
                .collect(Collectors.toList());
            model.addAttribute("lowStockItems", inventoryAlertDTOs != null ? inventoryAlertDTOs : List.of());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", inventoryAlertsPage.getTotalPages());
            model.addAttribute("totalItemsCount", inventoryAlertsPage.getTotalElements());
            
            // 从数据库获取库存预警（包括库存过低和过高）- 前4条记录（用于左下角组件）
            List<InventoryAlert> top4InventoryAlerts = inventoryAlertRepository.findTop4ByOrderByCreatedAtDesc();
            List<InventoryAlertDTO> top4InventoryAlertDTOs = top4InventoryAlerts.stream()
                .map(InventoryAlertDTO::new)
                .collect(Collectors.toList());
            model.addAttribute("top4LowStockItems", top4InventoryAlertDTOs != null ? top4InventoryAlertDTOs : List.of());
            
            // 添加物料类型统计（基于实际数据计算）
            Map<String, Integer> materialTypeCount = calculateMaterialTypeStatistics();
            model.addAttribute("materialTypeCount", materialTypeCount);
            model.addAttribute("materialTypePercentage", materialTypeCount);

            // 添加库存空间使用情况
            Map<String, Integer> spaceUsage = calculateSpaceUsage();
            model.addAttribute("spaceUsage", spaceUsage);
            
            // 添加天气信息
            Map<String, Object> weatherData = weatherService.getWeatherData();
            model.addAttribute("weatherData", weatherData);
            model.addAttribute("weatherIconClass", weatherService.getWeatherIconClass((String) weatherData.get("code")));
            
            // 添加库存柱状图数据
            Map<String, Object> stockBarData = new HashMap<>();
            stockBarData.put("categories", new String[]{"原材料", "半成品", "成品"});
            stockBarData.put("stockCap", new int[]{300, 500, 200});
            model.addAttribute("stockBarData", stockBarData);
            
        } catch (Exception e) {
            // 如果出现任何异常，提供默认值确保页面可以正常显示
            model.addAttribute("totalItems", 0L);
            model.addAttribute("recentRecords", List.of());
            model.addAttribute("lowStockItems", List.of());
            model.addAttribute("top4LowStockItems", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItemsCount", 0L);
            
            Map<String, Integer> defaultMaterialType = new HashMap<>();
            defaultMaterialType.put("默认仓库", 100);
            model.addAttribute("materialTypeCount", defaultMaterialType);
            model.addAttribute("materialTypePercentage", defaultMaterialType);
            
            Map<String, Integer> defaultSpaceUsage = new HashMap<>();
            defaultSpaceUsage.put("usedSpace", 300);
            defaultSpaceUsage.put("availableSpace", 1200);
            model.addAttribute("spaceUsage", defaultSpaceUsage);
            
            Map<String, Object> defaultWeatherData = new HashMap<>();
            defaultWeatherData.put("text", "晴");
            defaultWeatherData.put("temperature", "25");
            defaultWeatherData.put("wind", "3级");
            defaultWeatherData.put("humidity", "60%");
            defaultWeatherData.put("uv", "中等");
            defaultWeatherData.put("location", "中国浙江省杭州市滨江区滨文路");
            defaultWeatherData.put("updateTime", "刚刚");
            defaultWeatherData.put("code", "100");
            model.addAttribute("weatherData", defaultWeatherData);
            model.addAttribute("weatherIconClass", "bi-brightness-high-fill");
            
            Map<String, Object> defaultStockBarData = new HashMap<>();
            defaultStockBarData.put("categories", new String[]{"原材料", "半成品", "成品"});
            defaultStockBarData.put("stockCap", new int[]{300, 500, 200});
            model.addAttribute("stockBarData", defaultStockBarData);
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
            // 从数据库获取所有物料分类
            List<MaterialCategory> categories = materialCategoryRepository.findAll();
            
            // 从数据库获取所有物料
            List<Material> materials = materialRepository.findAll();
            
            // 按物料分类统计物料数量
            Map<Long, Long> categoryCount = materials.stream()
                .filter(m -> m.getCategoryId() != null)
                .collect(Collectors.groupingBy(Material::getCategoryId, Collectors.counting()));
            
            // 计算总数
            long total = categoryCount.values().stream().mapToLong(Long::longValue).sum();
            
            // 转换为百分比（基于总数量）
            if (total > 0) {
                for (Map.Entry<Long, Long> entry : categoryCount.entrySet()) {
                    // 获取分类名称
                    String categoryName = categories.stream()
                        .filter(c -> c.getId().equals(entry.getKey()))
                        .map(MaterialCategory::getCategoryName)
                        .findFirst()
                        .orElse("未知分类");
                    
                    int percentage = Math.round((float) entry.getValue() / total * 100);
                    typeCount.put(categoryName, percentage);
                }
            } else {
                // 如果没有数据，添加默认值避免前端错误
                typeCount.put("默认分类", 100);
            }
        } catch (Exception e) {
            // 如果计算过程中出现异常，返回默认值
            typeCount.put("默认分类", 100);
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
            // 获取所有仓库
            List<Warehouse> warehouses = warehouseRepository.findAll();
            
            // 获取所有物料
            List<Material> materials = materialRepository.findAll();
            
            // 按仓库统计物料数量
            Map<Long, Long> warehouseMaterialCount = materials.stream()
                .filter(m -> m.getDefaultWarehouseId() != null)
                .collect(Collectors.groupingBy(Material::getDefaultWarehouseId, Collectors.counting()));
            
            // 每个仓库的总存储空间
            int totalSpacePerWarehouse = 1500;
            
            // 计算总的已用空间和可用空间
            int totalUsedSpace = 0;
            int totalAvailableSpace = 0;
            
            // 计算各仓库的使用情况
            for (Warehouse warehouse : warehouses) {
                Long warehouseId = warehouse.getId();
                
                // 获取该仓库的物料数量
                Long materialCount = warehouseMaterialCount.getOrDefault(warehouseId, 0L);
                
                // 计算使用空间（基于物料数量）
                int usedSpace = Math.min(materialCount.intValue(), totalSpacePerWarehouse);
                totalUsedSpace += usedSpace;
            }
            
            // 计算总可用空间
            totalAvailableSpace = warehouses.size() * totalSpacePerWarehouse - totalUsedSpace;
            
            // 添加到结果中
            spaceUsage.put("usedSpace", totalUsedSpace);
            spaceUsage.put("availableSpace", totalAvailableSpace);
            
        } catch (Exception e) {
            // 如果计算过程中出现异常，返回默认值
            spaceUsage.put("usedSpace", 0);
            spaceUsage.put("availableSpace", 1500);
        }

        return spaceUsage;
    }
}