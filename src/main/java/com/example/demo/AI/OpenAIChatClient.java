package com.example.demo.AI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Component
public class OpenAIChatClient implements ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIChatClient.class);

    @Value("${spring.ai.openai.api-key:#{null}}")
    private String openAiApiKey;
    
    @Value("${spring.ai.openai.base-url:http://localhost:1234/v1}")
    private String openAiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 生成用于将自然语言转换为SQL的Prompt
     * @param naturalLanguageQuery 自然语言查询
     * @return 完整的Prompt
     */
    private String generateSQLPrompt(String naturalLanguageQuery) {
        return "你是一个专业的MySQL数据库专家，熟悉WMS仓库管理系统数据库结构。请根据用户需求生成准确的MySQL语句。\n\n" +
                "重要：只有当用户明确提到与库存、物料、入库、出库、查询、存货等相关内容时，才生成SQL语句。\n" +
                "如果用户只是进行普通问候或日常对话，请直接回复友好的对话内容，不要生成SQL语句。\n\n" +
                "需要生成SQL的关键词包括：查询、库存、入库、出库、存货、物料、BOM、零件、仓库、统计、记录、明细等。\n\n" +
                "数据库表结构如下：\n" +
                "1. materials: 物料主数据表\n" +
                "   - id: 主键\n" +
                "   - material_code: 物料编码\n" +
                "   - material_name: 物料名称\n" +
                "   - category_id: 物料分类ID\n" +
                "   - specification: 规格型号\n" +
                "   - unit: 计量单位\n" +
                "   - barcode: 条形码\n" +
                "   - brand: 品牌\n" +
                "   - supplier: 供应商\n" +
                "   - status: 状态\n" +
                "   - default_warehouse_id: 默认仓库ID\n" +
                "   - description: 物料描述\n" +
                "   - created_by: 创建人\n" +
                "   - created_at: 创建时间\n" +
                "   - updated_by: 更新人\n" +
                "   - updated_at: 更新时间\n\n" +
                "2. warehouses: 仓库信息表\n" +
                "   - id: 主键\n" +
                "   - warehouse_code: 仓库编码\n" +
                "   - warehouse_name: 仓库名称\n" +
                "   - location: 仓库位置\n" +
                "   - contact_person: 联系人\n" +
                "   - contact_phone: 联系电话\n" +
                "   - status: 状态\n" +
                "   - description: 仓库描述\n" +
                "   - created_by: 创建人\n" +
                "   - created_at: 创建时间\n" +
                "   - updated_by: 更新人\n" +
                "   - updated_at: 更新时间\n\n" +
                "3. inventory: 库存表（当前库存汇总）\n" +
                "   - id: 主键\n" +
                "   - material_id: 物料ID\n" +
                "   - warehouse_id: 仓库ID\n" +
                "   - quantity: 当前库存数量\n" +
                "   - available_quantity: 可用库存数量\n" +
                "   - locked_quantity: 锁定库存数量\n" +
                "   - last_stocktake_time: 最后盘点时间\n" +
                "   - last_update_time: 最后更新时间\n\n" +
                "4. inventory_transactions: 出入库记录表（事务记录）\n" +
                "   - id: 主键\n" +
                "   - transaction_no: 交易单号\n" +
                "   - transaction_type_id: 交易类型ID\n" +
                "   - material_id: 物料ID\n" +
                "   - warehouse_id: 仓库ID\n" +
                "   - batch_id: 批次ID\n" +
                "   - quantity: 数量（正数表示入库，负数表示出库）\n" +
                "   - unit_cost: 单位成本\n" +
                "   - total_cost: 总成本\n" +
                "   - reference_no: 参考单号\n" +
                "   - transaction_time: 交易时间\n" +
                "   - notes: 备注\n" +
                "   - created_by: 操作人\n" +
                "   - created_at: 创建时间\n\n" +
                "5. inventory_transaction_types: 交易类型表\n" +
                "   - id: 主键\n" +
                "   - type_code: 类型编码（如RK=入库，CK=出库）\n" +
                "   - type_name: 类型名称\n" +
                "   - direction: 方向（IN=入库，OUT=出库）\n" +
                "   - description: 描述\n" +
                "   - created_at: 创建时间\n\n" +
                "6. material_batches: 物料批次表\n" +
                "   - id: 主键\n" +
                "   - batch_number: 批次号\n" +
                "   - material_id: 物料ID\n" +
                "   - warehouse_id: 仓库ID\n" +
                "   - quantity: 当前批次库存数量\n" +
                "   - available_quantity: 可用批次库存数量\n" +
                "   - locked_quantity: 锁定批次库存数量\n" +
                "   - production_date: 生产日期\n" +
                "   - expiry_date: 过期日期\n" +
                "   - supplier: 供应商\n" +
                "   - manufacturer: 制造商\n" +
                "   - notes: 备注\n" +
                "   - is_active: 是否激活\n" +
                "   - created_by: 创建人\n" +
                "   - created_at: 创建时间\n" +
                "   - updated_by: 更新人\n" +
                "   - updated_at: 更新时间\n\n" +
                "7. inventory_alerts: 库存预警表\n" +
                "   - id: 主键\n" +
                "   - material_id: 物料ID\n" +
                "   - warehouse_id: 仓库ID\n" +
                "   - alert_type: 预警类型\n" +
                "   - current_quantity: 当前库存数量\n" +
                "   - threshold_value: 阈值\n" +
                "   - is_processed: 是否已处理\n" +
                "   - processed_by: 处理人\n" +
                "   - processed_time: 处理时间\n" +
                "   - created_at: 创建时间\n" +
                "   - updated_at: 更新时间\n\n" +
                "常见业务场景和SQL示例：\n" +
                "1. 查询某物料在某仓库的库存数量：\n" +
                "   SELECT i.quantity, m.material_name, w.warehouse_name FROM inventory i JOIN materials m ON i.material_id = m.id JOIN warehouses w ON i.warehouse_id = w.id WHERE m.material_code = 'MAT001' AND w.warehouse_code = 'WH001';\n\n" +
                "2. 查询某段时间的入库记录：\n" +
                "   SELECT t.transaction_no, m.material_name, w.warehouse_name, t.quantity, t.transaction_time FROM inventory_transactions t JOIN materials m ON t.material_id = m.id JOIN warehouses w ON t.warehouse_id = w.id JOIN inventory_transaction_types tt ON t.transaction_type_id = tt.id WHERE tt.direction = 'IN' AND t.transaction_time BETWEEN '2023-01-01' AND '2023-12-31';\n\n" +
                "3. 查询某段时间的出库记录：\n" +
                "   SELECT t.transaction_no, m.material_name, w.warehouse_name, t.quantity, t.transaction_time FROM inventory_transactions t JOIN materials m ON t.material_id = m.id JOIN warehouses w ON t.warehouse_id = w.id JOIN inventory_transaction_types tt ON t.transaction_type_id = tt.id WHERE tt.direction = 'OUT' AND t.transaction_time BETWEEN '2023-01-01' AND '2023-12-31';\n\n" +
                "4. 查询库存低于安全线的物料：\n" +
                "   SELECT m.material_name, w.warehouse_name, i.quantity FROM inventory i JOIN materials m ON i.material_id = m.id JOIN warehouses w ON i.warehouse_id = w.id WHERE i.quantity < 100;\n\n" +
                "输出要求：\n" +
                "1. 只有当用户请求涉及上述关键词时才返回SQL语句，否则返回友好的对话回复\n" +
                "2. 如果返回SQL语句，只返回一条纯净的SQL语句，不要包含任何解释、说明或其他文本\n" +
                "3. SQL语句必须以分号(;)结尾\n" +
                "4. 不要使用Markdown格式或其他格式化标记\n" +
                "5. 确保生成的SQL语句语法正确\n" +
                "6. 对于SELECT语句，如果涉及多个表，请使用适当的JOIN操作\n" +
                "7. 对于INSERT、UPDATE、DELETE语句，请确保符合表结构要求\n" +
                "8. 请确保WHERE条件准确，避免误操作数据\n" +
                "9. 不要在SQL语句后面添加任何解释性文字\n" +
                "10. 只返回一条SQL语句，不要返回多条\n" +
                "11. 不要包含任何中文说明或解释性文本\n" +
                "12. 不要包含'需要注意的是'、'根据问题描述'、'是合适的'、'接下来'、'确认'等解释性语句\n" +
                "13. 严格遵守以上规则，只返回纯净的SQL语句\n" +
                "14. 绝对不能在SQL语句中包含任何分析、解释或说明性的文字\n" +
                "15. 特别注意出入库相关查询，应当查询inventory_transactions表并关联inventory_transaction_types表以确定方向\n\n" +
                "示例输出（SQL）：\n" +
                "SELECT * FROM materials;\n\n" +
                "示例输出（对话）：\n" +
                "你好！我是仓库管理AI助手，有什么我可以帮你的吗？\n\n" +
                "用户请求: " + naturalLanguageQuery;
    }

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
            logger.error("调用AI服务时发生错误: ", e);
            return "调用AI服务时发生错误: " + e.getMessage();
        }
    }

    @Override
    public Flux<String> stream(String message) {
        // 实现流式调用
        return Flux.just("流式响应暂未实现");
    }
    
    /**
     * 生成符合MySQL 9.4规范的增删改查语句
     * @param naturalLanguageQuery 用自然语言描述的数据库操作需求
     * @return 符合MySQL 9.4规范的SQL语句
     */
    @Override
    public String generateSQL(String naturalLanguageQuery) {
        String prompt = generateSQLPrompt(naturalLanguageQuery);
        
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
            logger.error("生成SQL语句时发生错误: ", e);
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
        if (cleaned.startsWith("``sql")) {
            cleaned = cleaned.substring(6);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        // 去除首尾空白字符
        cleaned = cleaned.trim();
        
        // 过滤掉深度思考内容
        cleaned = removeDeepThoughtContent(cleaned);
        
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
                        trimmedLine.contains("可能会有的") ||
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
    
    /**
     * 移除AI响应中的深度思考内容
     * @param response AI响应
     * @return 过滤后的响应
     */
    private String removeDeepThoughtContent(String response) {
        if (response == null || response.isEmpty()) {
            return response;
        }
        
        String cleaned = response;
        
        // 移除常见的深度思考关键词和内容
        String[] thoughtPatterns = {
            "让我思考一下",
            "思考过程",
            "分析一下",
            "考虑以下因素",
            "首先需要理解",
            "接下来考虑",
            "需要注意的是",
            "根据问题描述",
            "是合适的",
            "会更合适",
            "考虑到性能",
            "最好还是",
            "确认",
            "确保包含正确的WHERE子句",
            "检查生成的SQL语句",
            "符合所有要求",
            "确认无误后就可以输出结果",
            "解释",
            "说明",
            "总而言之",
            "综上所述"
        };
        
        // 移除包含这些关键词的行
        String[] lines = cleaned.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String trimmedLine = line.trim();
            boolean shouldSkip = false;
            
            // 检查是否包含深度思考关键词
            for (String pattern : thoughtPatterns) {
                if (trimmedLine.contains(pattern)) {
                    shouldSkip = true;
                    break;
                }
            }
            
            // 检查是否是解释性语句
            if (!shouldSkip && (trimmedLine.startsWith("因为") || 
                               trimmedLine.startsWith("由于") ||
                               trimmedLine.startsWith("这将"))) {
                shouldSkip = true;
            }
            
            if (!shouldSkip) {
                sb.append(line).append("\n");
            }
        }
        
        cleaned = sb.toString().trim();
        
        // 移除常见的前缀说明
        if (cleaned.startsWith("好的，") || cleaned.startsWith("好的，我来")) {
            int commaIndex = cleaned.indexOf("，");
            if (commaIndex > 0) {
                cleaned = cleaned.substring(commaIndex + 1).trim();
            }
        }
        
        return cleaned;
    }
}