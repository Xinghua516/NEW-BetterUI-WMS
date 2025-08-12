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
            String response = chatClient.call(message);
            return createSuccessResponse("response", response);
        } catch (Exception e) {
            return createErrorResponse("AI服务暂时不可用: " + e.getMessage());
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
            String sqlQuery = chatClient.generateSQL(naturalLanguageQuery);
            return createSuccessResponse("sqlQuery", sqlQuery);
        } catch (Exception e) {
            return createErrorResponse("SQL生成失败: " + e.getMessage());
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
            
            // 验证SQL
            ValidationResult validation = validateSQL(sql);
            if (!validation.isValid()) {
                return createErrorResult(validation.getErrorMessage());
            }
            
            // 执行SQL
            Map<String, Object> result = executeSQLBasedOnType(sql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return createErrorResult("SQL执行失败: " + e.getMessage());
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

            // 验证输入
            ValidationResult validation = validateInput(naturalLanguageQuery);
            if (!validation.isValid()) {
                return createErrorResult(validation.getErrorMessage());
            }

            logger.info("处理自然语言查询: " + naturalLanguageQuery);
            
            // 检查是否为SQL相关查询
            if (!isSQLQuery(naturalLanguageQuery)) {
                // 如果不是SQL相关查询，返回普通对话响应
                String response = chatClient.call(naturalLanguageQuery);
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", response);
                return ResponseEntity.ok(result);
            }
            
            // 生成SQL
            String sql = chatClient.generateSQL(naturalLanguageQuery);
            logger.info("AI生成的原始SQL: " + sql);
            
            // 验证生成的SQL
            ValidationResult sqlValidation = validateGeneratedSQL(sql);
            if (!sqlValidation.isValid()) {
                return createErrorResult(sqlValidation.getErrorMessage());
            }
            
            // 执行SQL
            Map<String, Object> result = executeSQLBasedOnType(sql);
            
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
            logger.error("处理自然语言查询时发生错误", e);
            return createErrorResult("处理自然语言查询时发生错误: " + e.getMessage());
        }
    }

    // 新增方法：判断是否为SQL相关查询
    private boolean isSQLQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        
        // 转换为小写进行比较
        String lowerQuery = query.toLowerCase();
        
        // 定义触发SQL查询的关键词
        String[] sqlKeywords = {
            "查询", "库存", "入库", "出库", "存货", "物料", "bom", "零件", "仓库", "统计", "记录", "明细",
            "select", "inventory", "stock", "warehouse", "material", "bom", "part", "record"
        };
        
        // 检查是否包含任何关键词
        for (String keyword : sqlKeywords) {
            if (lowerQuery.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        return false;
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
            
            return createSuccessResponse("message", "配置更新成功");
        } catch (Exception e) {
            return createErrorResponse("配置更新失败: " + e.getMessage());
        }
    }
    
    // 辅助方法：创建成功响应
    private <T> ResponseEntity<Map<String, T>> createSuccessResponse(String key, T value) {
        Map<String, T> result = new HashMap<>();
        result.put(key, value);
        return ResponseEntity.ok(result);
    }
    
    // 辅助方法：创建错误响应
    private ResponseEntity<Map<String, String>> createErrorResponse(String errorMessage) {
        Map<String, String> error = new HashMap<>();
        error.put("error", errorMessage);
        return ResponseEntity.status(500).body(error);
    }
    
    // 辅助方法：创建错误结果
    private ResponseEntity<Map<String, Object>> createErrorResult(String errorMessage) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", errorMessage);
        return ResponseEntity.badRequest().body(error);
    }
    
    // 辅助方法：验证输入
    private ValidationResult validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, "查询语句不能为空");
        }
        return ValidationResult.VALID;
    }
    
    // 辅助方法：验证SQL
    private ValidationResult validateSQL(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new ValidationResult(false, "SQL语句不能为空");
        }
        return ValidationResult.VALID;
    }
    
    // 辅助方法：验证生成的SQL
    private ValidationResult validateGeneratedSQL(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new ValidationResult(false, "AI无法生成有效的SQL语句");
        }
        
        if (sql.contains("无法生成有效的SQL语句") || sql.contains("生成SQL语句时发生错误")) {
            return new ValidationResult(false, "AI无法生成有效的SQL语句: " + sql);
        }
        
        return ValidationResult.VALID;
    }
    
    // 辅助方法：根据SQL类型执行不同的方法
    private Map<String, Object> executeSQLBasedOnType(String sql) throws Exception {
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
            return error;
        }
        
        return result;
    }
    
    // 内部类：验证结果
    private static class ValidationResult {
        private static final ValidationResult VALID = new ValidationResult(true, null);
        
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}