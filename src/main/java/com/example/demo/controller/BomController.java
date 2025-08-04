package com.example.demo.controller;

import com.example.demo.entity.BomHeader;
import com.example.demo.entity.BomItem;
import com.example.demo.repository.BomHeaderRepository;
import com.example.demo.repository.BomItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class BomController {
    
    @Autowired
    private BomHeaderRepository bomHeaderRepository;
    
    @Autowired
    private BomItemRepository bomItemRepository;
    
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
}