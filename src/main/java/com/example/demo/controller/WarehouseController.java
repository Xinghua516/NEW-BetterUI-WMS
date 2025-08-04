package com.example.demo.controller;

import com.example.demo.entity.BomItem;
import com.example.demo.entity.LowStockItem;
import com.example.demo.entity.Material;
import com.example.demo.repository.BomItemRepository;
import com.example.demo.repository.LowStockItemRepository;
import com.example.demo.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WarehouseController {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private LowStockItemRepository lowStockItemRepository;
    
    @Autowired
    private BomItemRepository bomItemRepository;
    
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
        long severeShortage = lowStockItemRepository.countByCurrentStockLessThanEqual(5);
        long stockShortage = lowStockItemRepository.countByCurrentStockGreaterThanAndCurrentStockLessThanEqual(5, 10);
        long emptyStock = lowStockItemRepository.countByCurrentStockLessThanEqual(0);
        
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
    
    // 添加获取BOM明细的方法
    @GetMapping("/bom-items")
    public String getBomItems(Model model, Pageable pageable) {
        Page<BomItem> bomItemsPage = bomItemRepository.findAll(pageable);
        model.addAttribute("bomItems", bomItemsPage);
        return "warehouse :: #stockTableBody";
    }
}