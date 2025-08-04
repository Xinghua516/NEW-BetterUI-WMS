package com.example.demo.controller;

import com.example.demo.entity.InventoryRecord;
import com.example.demo.repository.InventoryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InventoryController {
    
    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;
    
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

    /**
     * 添加POST支持
     */
    @PostMapping("/inventory")
    public String handleInventoryPost() {
        // 重定向到GET方法
        return "redirect:/inventory";
    }
}