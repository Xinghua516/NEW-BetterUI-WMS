package com.example.demo.AI;

import com.example.demo.service.AISQLService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Component
public class OpenAIChatClient implements ChatClient {

    @Value("${spring.ai.openai.api-key:#{null}}")
    private String openAiApiKey;
    
    @Value("${spring.ai.openai.base-url:http://localhost:1234/v1}")
    private String openAiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String call(String message) {
        // 调用LMStudio的API
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
         request.put("messages", new Map[]{new HashMap<String, String>() {{
            put("role", "user");
            put("content", message);
        }}});
        request.put("temperature", 0.7);
        
        try {
            // 实际调用API
            Map response = restTemplate.postForObject(openAiBaseUrl + "/chat/completions", request, Map.class);
            if (response != null && response.containsKey("choices")) {
                // 正确处理API响应
                java.util.List<Map> choices = (java.util.List<Map>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = choices.get(0);
                    if (choice != null && choice.containsKey("message")) {
                        Map messageMap = (Map) choice.get("message");
                        if (messageMap != null && messageMap.containsKey("content")) {
                            return (String) messageMap.get("content");
                        }
                    }
                }
            }
            
            return "无法获取有效响应";
        } catch (Exception e) {
            e.printStackTrace();
            return "调用AI服务时发生错误: " + e.getMessage();
        }
    }

    @Override
    public Flux<String> stream(String message) {
        // 实现流式调用
        return Flux.just("流式响应暂未实现");
    }
    
    /**
     * 生成符合MySQL 9.4规范的查询语句
     * @param naturalLanguageQuery 用自然语言描述的查询需求
     * @return 符合MySQL 9.4规范的查询语句
     */
    @Override
    public String generateQuery(String naturalLanguageQuery) {
        String prompt = "你是一个专业的MySQL数据库专家，能够将自然语言转换为精确的MySQL 9.4查询语句。请将以下自然语言转换为MySQL查询：" + naturalLanguageQuery;
        
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("messages", new Map[]{new HashMap<String, String>() {{
            put("role", "user");
            put("content", prompt);
        }}});
        request.put("temperature", 0.3);
        
        try {
            // 调用API
            Map response = restTemplate.postForObject(openAiBaseUrl + "/chat/completions", request, Map.class);
            if (response != null && response.containsKey("choices")) {
                // 正确处理API响应
                java.util.List<Map> choices = (java.util.List<Map>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = choices.get(0);
                    if (choice != null && choice.containsKey("message")) {
                        Map messageMap = (Map) choice.get("message");
                        if (messageMap != null && messageMap.containsKey("content")) {
                            String sqlQuery = (String) messageMap.get("content");
                            
                            // 简单验证是否是有效的SQL语句
                            if (sqlQuery != null && sqlQuery.trim().toLowerCase().startsWith("select")) {
                                return sqlQuery;
                            }
                            
                            // 如果响应不是有效的SQL，返回原始响应
                            return sqlQuery;
                        }
                    }
                }
            }
            
            return "无法生成有效的SQL查询";
        } catch (Exception e) {
            e.printStackTrace();
            return "生成SQL查询时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 生成符合MySQL 9.4规范的增删改查语句
     * @param naturalLanguageQuery 用自然语言描述的数据库操作需求
     * @return 符合MySQL 9.4规范的SQL语句
     */
    @Override
    public String generateSQL(String naturalLanguageQuery) {
        String prompt = AISQLService.generateSQLPrompt(naturalLanguageQuery);
        
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("messages", new Map[]{new HashMap<String, String>() {{
            put("role", "user");
            put("content", prompt);
        }}});
        request.put("temperature", 0.3);
        
        try {
            // 调用API
            Map response = restTemplate.postForObject(openAiBaseUrl + "/chat/completions", request, Map.class);
            if (response != null && response.containsKey("choices")) {
                // 正确处理API响应
                java.util.List<Map> choices = (java.util.List<Map>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = choices.get(0);
                    if (choice != null && choice.containsKey("message")) {
                        Map messageMap = (Map) choice.get("message");
                        if (messageMap != null && messageMap.containsKey("content")) {
                            String sqlQuery = (String) messageMap.get("content");
                            
                            // 清理AI返回的内容，提取纯SQL语句
                            String cleanedSql = cleanSQLResponse(sqlQuery);
                            
                            // 简单验证是否是有效的SQL语句
                            String trimmedSql = cleanedSql.trim().toLowerCase();
                            if (trimmedSql.startsWith("select") || 
                                trimmedSql.startsWith("insert") || 
                                trimmedSql.startsWith("update") || 
                                trimmedSql.startsWith("delete")) {
                                return cleanedSql;
                            }
                            
                            // 如果响应不是有效的SQL，返回原始响应
                            return sqlQuery;
                        }
                    }
                }
            }
            
            return "无法生成有效的SQL语句";
        } catch (Exception e) {
            e.printStackTrace();
            return "生成SQL语句时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 清理AI返回的SQL响应，去除可能的额外文本
     * @param response AI返回的原始响应
     * @return 清理后的SQL语句
     */
    private String cleanSQLResponse(String response) {
        if (response == null || response.isEmpty()) {
            return response;
        }
        
        // 去除可能的代码块标记
        String cleaned = response.trim();
        if (cleaned.startsWith("```sql")) {
            cleaned = cleaned.substring(6);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        // 去除首尾空白字符
        cleaned = cleaned.trim();
        
        // 查找第一个SQL关键字的位置
        String lowerCase = cleaned.toLowerCase();
        int selectIndex = lowerCase.indexOf("select");
        int insertIndex = lowerCase.indexOf("insert");
        int updateIndex = lowerCase.indexOf("update");
        int deleteIndex = lowerCase.indexOf("delete");
        
        // 找到第一个有效的SQL语句开始位置
        int startIndex = -1;
        if (selectIndex >= 0) {
            startIndex = selectIndex;
        }
        if (insertIndex >= 0 && (startIndex == -1 || insertIndex < startIndex)) {
            startIndex = insertIndex;
        }
        if (updateIndex >= 0 && (startIndex == -1 || updateIndex < startIndex)) {
            startIndex = updateIndex;
        }
        if (deleteIndex >= 0 && (startIndex == -1 || deleteIndex < startIndex)) {
            startIndex = deleteIndex;
        }
        
        if (startIndex >= 0) {
            cleaned = cleaned.substring(startIndex);
        }
        
        // 查找SQL语句结束位置
        // 查找分号作为语句结束标志
        int semicolonIndex = cleaned.indexOf(";");
        if (semicolonIndex > 0) {
            // 只取分号之前的部分作为SQL语句
            cleaned = cleaned.substring(0, semicolonIndex + 1);
        } else {
            // 如果没有分号，查找第一个换行符后是否有非SQL内容
            int firstNewLine = cleaned.indexOf("\n");
            if (firstNewLine > 0) {
                String firstLine = cleaned.substring(0, firstNewLine).trim().toLowerCase();
                if (firstLine.startsWith("select") || 
                    firstLine.startsWith("insert") || 
                    firstLine.startsWith("update") || 
                    firstLine.startsWith("delete")) {
                    // 检查下一行是否是解释性文本
                    String rest = cleaned.substring(firstNewLine + 1).trim();
                    if (rest.startsWith("SELECT语句") || 
                        rest.startsWith("这将") || 
                        rest.startsWith("说明") ||
                        rest.startsWith("解释") ||
                        rest.startsWith("首先") ||
                        rest.contains("会更合适") ||
                        rest.contains("考虑到性能") ||
                        rest.contains("最好还是") ||
                        rest.contains("需要注意的是") ||
                        rest.contains("根据问题描述")) {
                        cleaned = cleaned.substring(0, firstNewLine).trim();
                        // 确保语句以分号结束
                        if (!cleaned.endsWith(";")) {
                            cleaned += ";";
                        }
                    }
                }
            }
        }
        
        // 最后验证并确保只返回一条语句
        String[] statements = cleaned.split(";");
        if (statements.length > 0) {
            String firstStatement = statements[0].trim();
            if (!firstStatement.isEmpty()) {
                // 确保语句以分号结束
                if (!firstStatement.endsWith(";")) {
                    firstStatement += ";";
                }
                cleaned = firstStatement;
            }
        }
        
        // 再次检查语句是否以SQL关键字开头
        String finalCheck = cleaned.toLowerCase().trim();
        if (!(finalCheck.startsWith("select") || 
              finalCheck.startsWith("insert") || 
              finalCheck.startsWith("update") || 
              finalCheck.startsWith("delete"))) {
            // 如果不是有效的SQL语句，尝试从语句中提取有效的部分
            int selectPos = cleaned.indexOf("select");
            int insertPos = cleaned.indexOf("insert");
            int updatePos = cleaned.indexOf("update");
            int deletePos = cleaned.indexOf("delete");
            
            int validStart = -1;
            if (selectPos >= 0) validStart = selectPos;
            if (insertPos >= 0 && (validStart == -1 || insertPos < validStart)) validStart = insertPos;
            if (updatePos >= 0 && (validStart == -1 || updatePos < validStart)) validStart = updatePos;
            if (deletePos >= 0 && (validStart == -1 || deletePos < validStart)) validStart = deletePos;
            
            if (validStart >= 0) {
                // 从有效的SQL关键字开始提取语句
                String extracted = cleaned.substring(validStart);
                
                // 查找语句结束位置（分号或换行符）
                int endPos = extracted.length();
                int semicolonPos = extracted.indexOf(";");
                int newlinePos = extracted.indexOf("\n");
                
                if (semicolonPos >= 0) {
                    endPos = semicolonPos + 1;
                } else if (newlinePos >= 0) {
                    endPos = newlinePos;
                }
                
                cleaned = extracted.substring(0, endPos).trim();
                // 确保语句以分号结束
                if (!cleaned.endsWith(";")) {
                    cleaned += ";";
                }
            }
        }
        
        // 最后清理：确保返回的语句只包含一个完整的SQL语句
        // 移除可能的额外说明文字
        if (cleaned.contains("\n")) {
            String[] lines = cleaned.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                String trimmedLine = line.trim();
                // 检查是否包含中文解释性文字
                if (trimmedLine.contains("是合适的") ||
                    trimmedLine.contains("接下来") ||
                    trimmedLine.contains("确认") ||
                    trimmedLine.contains("语法是否正确") ||
                    trimmedLine.contains("会更合适") ||
                    trimmedLine.contains("考虑到性能") ||
                    trimmedLine.contains("最好还是") ||
                    trimmedLine.contains("需要注意的是") ||
                    trimmedLine.contains("根据问题描述") ||
                    trimmedLine.contains("SELECT语句") ||
                    trimmedLine.contains("确保包含正确的WHERE子句") ||
                    trimmedLine.contains("检查生成的SQL语句") ||
                    trimmedLine.contains("符合所有要求") ||
                    trimmedLine.contains("确认无误后就可以输出结果")) {
                    // 跳过包含解释性文字的行
                    continue;
                }
                
                if (trimmedLine.startsWith("select") || 
                    trimmedLine.startsWith("insert") || 
                    trimmedLine.startsWith("update") || 
                    trimmedLine.startsWith("delete")) {
                    // 只取包含SQL关键字的第一行
                    if (sb.length() == 0) {
                        sb.append(trimmedLine);
                        if (!trimmedLine.endsWith(";")) {
                            sb.append(";");
                        }
                    }
                    break;
                }
            }
            if (sb.length() > 0) {
                cleaned = sb.toString();
            } else {
                // 如果没有找到有效的SQL语句，尝试查找最后一行有效的SQL
                for (int i = lines.length - 1; i >= 0; i--) {
                    String trimmedLine = lines[i].trim().toLowerCase();
                    if (trimmedLine.startsWith("select") || 
                        trimmedLine.startsWith("insert") || 
                        trimmedLine.startsWith("update") || 
                        trimmedLine.startsWith("delete")) {
                        cleaned = lines[i].trim();
                        if (!cleaned.endsWith(";")) {
                            cleaned += ";";
                        }
                        break;
                    }
                }
            }
        }
        
        // 最后确保返回的是一条有效的SQL语句
        String finalLowerCase = cleaned.toLowerCase().trim();
        if (!(finalLowerCase.startsWith("select") || 
              finalLowerCase.startsWith("insert") || 
              finalLowerCase.startsWith("update") || 
              finalLowerCase.startsWith("delete"))) {
            // 如果仍然不是有效的SQL语句，尝试从中提取
            
            int validStart = -1;
            String keyword = "";
            if (selectIndex >= 0) {
                validStart = selectIndex;
                keyword = "select";
            }
            if (insertIndex >= 0 && (validStart == -1 || insertIndex < validStart)) {
                validStart = insertIndex;
                keyword = "insert";
            }
            if (updateIndex >= 0 && (validStart == -1 || updateIndex < validStart)) {
                validStart = updateIndex;
                keyword = "update";
            }
            if (deleteIndex >= 0 && (validStart == -1 || deleteIndex < validStart)) {
                validStart = deleteIndex;
                keyword = "delete";
            }
            
            if (validStart >= 0) {
                // 提取从关键字开始到分号结束的部分
                int newStartIndex = finalLowerCase.indexOf(keyword);
                int endIndex = cleaned.indexOf(";", newStartIndex);
                if (endIndex > newStartIndex) {
                    cleaned = cleaned.substring(newStartIndex, endIndex + 1);
                } else {
                    // 如果没有找到分号，就提取到行尾
                    cleaned = cleaned.substring(newStartIndex);
                    if (!cleaned.endsWith(";")) {
                        cleaned += ";";
                    }
                }
            }
        }
        
        return cleaned;
    }
}