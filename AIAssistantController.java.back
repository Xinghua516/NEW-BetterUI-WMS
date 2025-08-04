package com.example.warehouse.controller.ai;

import com.example.warehouse.service.ai.AISQLAssistantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ai-assistant")
public class AIAssistantController {

    private final AISQLAssistantService aiAssistantService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AIAssistantController(AISQLAssistantService aiAssistantService, ObjectMapper objectMapper) {
        this.aiAssistantService = aiAssistantService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String aiAssistantPage() {
        return "ai-assistant";
    }

    @PostMapping("/query")
    @ResponseBody
    public ResponseEntity<String> handleUserQuery(@RequestParam("userInput") String userInput) {
        try {
            Object result = aiAssistantService.processNaturalLanguageQuery(userInput);
            return ResponseEntity.ok(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\": \"处理请求时出错: " + e.getMessage() + "\"}");
        }
    }
    
    // 用于页面初始加载时显示历史消息的处理方法（如果需要）
    @PostMapping
    public String handleUserQueryWithPageReload(@RequestParam("userInput") String userInput, Model model) {
        try {
            Object result = aiAssistantService.processNaturalLanguageQuery(userInput);
            model.addAttribute("result", result);
            model.addAttribute("userInput", userInput);
        } catch (Exception e) {
            model.addAttribute("result", "处理请求时出错: " + e.getMessage());
        }
        return "ai-assistant";
    }

    @GetMapping("/get-sql")
    @ResponseBody
    public String getGeneratedSql(@RequestParam("query") String userInput) {
        try {
            return aiAssistantService.processNaturalLanguageQuery(userInput).toString();
        } catch (Exception e) {
            return "无法生成SQL: " + e.getMessage();
        }
    }
}