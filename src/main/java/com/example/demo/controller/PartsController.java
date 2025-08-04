package com.example.demo.controller;

import com.example.demo.entity.BomHeader;
import com.example.demo.entity.InventoryRecord;
import com.example.demo.entity.LowStockItem;
import com.example.demo.entity.Material;
import com.example.demo.repository.BomHeaderRepository;
import com.example.demo.repository.InventoryRecordRepository;
import com.example.demo.repository.LowStockItemRepository;
import com.example.demo.repository.MaterialRepository;
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

@Controller
public class PartsController {
    
    @Autowired
    private BomHeaderRepository bomHeaderRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;
    
    @Autowired
    private LowStockItemRepository lowStockItemRepository;
    
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
            // 查询所有低库存零件
            List<LowStockItem> lowStockItems = lowStockItemRepository.findAllByOrderByCurrentStockAsc();
            
            // 将低库存零件列表添加到模型中
            model.addAttribute("lowStockItems", lowStockItems);
            
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
            model.addAttribute("lowStockItems", new ArrayList<LowStockItem>()); // 添加空列表避免模板出错
            model.addAttribute("querySuccess", false);
        }
        
        return "parts-query"; // 返回对应的 Thymeleaf 模板
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
            Page<InventoryRecord> inventoryRecords = inventoryRecordRepository.findByItemCode(material.getMaterialCode(), pageable);
            model.addAttribute("inventoryRecords", inventoryRecords.getContent());
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