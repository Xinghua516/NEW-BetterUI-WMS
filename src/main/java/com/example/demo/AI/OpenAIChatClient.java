package com.example.demo.AI;

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
}