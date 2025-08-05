package com.example.demo.controller;

import com.example.demo.AI.ChatClient;
import com.example.demo.service.SQLExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @Autowired
    private SQLExecutionService sqlExecutionService;
    
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
            String sqlQuery = chatClient.generateSQL(naturalLanguageQuery);
            
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
     * 执行SQL语句
     * @param request 请求参数，包含sql字段
     * @return SQL执行结果
     */
    @PostMapping("/ai/execute-sql")
    public ResponseEntity<Map<String, Object>> executeSql(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(error);
            }
            
            // 根据SQL类型执行不同的方法
            String trimmedSql = sql.trim().toLowerCase();
            Map<String, Object> result;
            
            if (trimmedSql.startsWith("select")) {
                result = sqlExecutionService.executeQuery(sql);
            } else if (trimmedSql.startsWith("insert") || 
                      trimmedSql.startsWith("update") || 
                      trimmedSql.startsWith("delete")) {
                result = sqlExecutionService.executeUpdate(sql);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "不支持的SQL操作类型，仅支持SELECT、INSERT、UPDATE、DELETE");
                return ResponseEntity.badRequest().body(error);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 错误处理
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "SQL执行失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 将自然语言转换为SQL并自动执行
     * @param request 请求参数，包含naturalLanguageQuery字段
     * @return SQL执行结果
     */
    @PostMapping("/ai/nl-to-sql-and-execute")
    public ResponseEntity<Map<String, Object>> naturalLanguageToSQLAndExecute(@RequestBody Map<String, String> request) {
        try {
            String naturalLanguageQuery = request.get("naturalLanguageQuery");

            if (naturalLanguageQuery == null || naturalLanguageQuery.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "查询语句不能为空");
                return ResponseEntity.badRequest().body(error);
            }

            logger.info("处理自然语言查询: " + naturalLanguageQuery);
            
            // 生成SQL
            String sql = chatClient.generateSQL(naturalLanguageQuery);
            logger.info("AI生成的原始SQL: " + sql);
            
            // 验证生成的SQL是否有效
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "AI无法生成有效的SQL语句");
                logger.warn("AI无法生成有效的SQL语句，返回空结果");
                return ResponseEntity.badRequest().body(error);
            }
            
            // 检查SQL是否包含错误信息
            if (sql.contains("无法生成有效的SQL语句") || sql.contains("生成SQL语句时发生错误")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "AI无法生成有效的SQL语句: " + sql);
                logger.warn("AI生成SQL语句包含错误信息: " + sql);
                return ResponseEntity.badRequest().body(error);
            }
            
            // 执行SQL
            String trimmedSql = sql.trim().toLowerCase();
            Map<String, Object> result;
            
            if (trimmedSql.startsWith("select")) {
                result = sqlExecutionService.executeQuery(sql);
                
                // 修改：优化查询结果展示，使用备注名称
                if (result.containsKey("columns") && result.containsKey("rows")) {
                    List<String> columns = (List<String>) result.get("columns");
                    List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");

                    // 获取备注名称
                    List<String> commentColumns = getCommentColumns(columns);

                    result.put("columns", commentColumns);

                    // 更新rows中的键为备注名称
                    for (Map<String, Object> row : rows) {
                        Map<String, Object> newRow = new HashMap<>();
                        for (int i = 0; i < columns.size(); i++) {
                            String originalKey = columns.get(i);
                            String commentKey = commentColumns.get(i);
                            newRow.put(commentKey, row.get(originalKey));
                        }
                        row.clear();
                        row.putAll(newRow);
                    }
                }
            } else if (trimmedSql.startsWith("insert") ||
                    trimmedSql.startsWith("update") ||
                    trimmedSql.startsWith("delete")) {
                result = sqlExecutionService.executeUpdate(sql);
            } else {
                // 记录生成的SQL语句以便调试
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "生成的SQL语句类型不支持，仅支持SELECT、INSERT、UPDATE、DELETE。实际生成的SQL: " + sql);
                error.put("generatedSql", sql);
                logger.warn("不支持的SQL语句类型: " + sql);
                return ResponseEntity.badRequest().body(error);
            }
            
            // 检查执行结果
            if (!(Boolean) result.get("success")) {
                logger.error("SQL执行失败: " + result.get("message") + ", SQL: " + sql);
            } else {
                logger.info("SQL执行成功: " + sql);
            }
            
            // 将生成的SQL添加到结果中
            result.put("generatedSql", sql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 错误处理
            logger.error("处理自然语言查询时发生错误", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "处理自然语言查询时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // 新增方法：获取备注名称
    private List<String> getCommentColumns(List<String> columns) {
        // 这里可以根据实际情况实现获取备注名称的逻辑
        // 示例：直接返回原列名（实际应替换为真实的备注名称）
        return columns.stream()
                .map(column -> "备注_" + column) // 示例：简单地在前面加上"备注_"
                .collect(Collectors.toList());
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