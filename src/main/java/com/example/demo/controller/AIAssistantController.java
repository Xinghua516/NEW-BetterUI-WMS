package com.example.demo.controller;

import com.example.demo.AI.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * AI助手控制器，处理AI相关请求
 * 提供同步和流式两种AI响应模式
 */
@Controller
@RequestMapping("/")
public class AIAssistantController {
    private static final Logger logger = LoggerFactory.getLogger(AIAssistantController.class);

    @GetMapping("/ai-assistant")
    public String aiAssistant() {
        logger.info("访问AI助手页面");
        return "ai-assistant";
    }

    private final ChatClient chatClient;
    
    @Value("${spring.ai.openai.api-key:#{null}}")
    private String openAiApiKey;
    
    @Value("${spring.ai.openai.base-url:http://localhost:1234/v1}")
    private String openAiBaseUrl;

    @Autowired
    public AIAssistantController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    /**
     * 处理用户消息并返回AI响应
     * @param request 请求参数，包含message字段
     * @return AI处理后的响应
     */
    @PostMapping("/ai/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            
            // 发送消息并获取响应
            String response = chatClient.call(message);
            
            // 构造响应
            Map<String, String> result = new HashMap<>();
            result.put("response", response);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 错误处理
            Map<String, String> error = new HashMap<>();
            error.put("error", "AI服务暂时不可用: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * 流式处理用户消息并返回AI响应
     * @param request 请求参数，包含message字段
     * @return AI响应流
     */
    @PostMapping("/ai/chat-stream")
    public ResponseEntity<Flux<String>> chatStream(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            
            // 发送消息并获取响应流
            Flux<String> response = chatClient.stream(message);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 将自然语言转换为MySQL 9.4查询语句
     * @param request 请求参数，包含naturalLanguageQuery字段
     * @return 生成的MySQL查询语句
     */
    @PostMapping("/ai/generate-sql")
    public ResponseEntity<Map<String, String>> generateSql(@RequestBody Map<String, String> request) {
        try {
            String naturalLanguageQuery = request.get("naturalLanguageQuery");
            
            // 生成MySQL查询
            String sqlQuery = chatClient.generateQuery(naturalLanguageQuery);
            
            // 构造响应
            Map<String, String> result = new HashMap<>();
            result.put("sqlQuery", sqlQuery);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 错误处理
            Map<String, String> error = new HashMap<>();
            error.put("error", "SQL生成失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * 获取当前AI配置
     * @return 当前配置信息
     */
    @GetMapping("/ai/get-config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("baseUrl", openAiBaseUrl);
        config.put("apiKey", openAiApiKey != null ? openAiApiKey : "");
        return ResponseEntity.ok(config);
    }
    
    /**
     * 更新AI配置
     * @param config 新的配置信息
     * @return 更新结果
     */
    @PostMapping("/ai/update-config")
    public ResponseEntity<Map<String, String>> updateConfig(@RequestBody Map<String, String> config) {
        try {
            // 更新配置
            String newBaseUrl = config.get("baseUrl");
            String newApiKey = config.get("apiKey");
            
            if (newBaseUrl != null && !newBaseUrl.isEmpty()) {
                openAiBaseUrl = newBaseUrl;
            }
            
            if (newApiKey != null) {
                openAiApiKey = newApiKey;
            }
            
            // 构造响应
            Map<String, String> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "配置更新成功");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 错误处理
            Map<String, String> error = new HashMap<>();
            error.put("error", "配置更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}