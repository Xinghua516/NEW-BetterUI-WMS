package com.example.warehouse.service.ai;

import com.example.warehouse.config.SpringAIProperties;
import com.example.warehouse.exception.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import java.util.Map;

@Service
public class AISQLAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AISQLAssistantService.class);

    private ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;
    private final SpringAIProperties springAIProperties;

    // 系统提示词 - 指导模型生成SQL
    private static final String SYSTEM_PROMPT = """
        你是一个专业的仓库管理系统AI助手，具备深度思考和分析能力。你可以与用户进行自然对话，并在需要时生成SQL语句来查询或更新数据库。

        ## 你的能力包括：

        1. **日常对话能力**
           - 能够理解和回应用户的普通对话
           - 提供库存管理相关的建议和解释
           - 进行深度思考和分析问题

        2. **SQL生成能力**
           - 当用户需要查询或操作数据时，可以生成准确的SQL语句
           - 只能操作以下4个表：product, warehouse, stock_in, stock_out
           - 只能执行SELECT查询、INSERT插入、UPDATE更新操作
           - 绝对禁止DELETE、DROP、TRUNCATE等删除或修改表结构的操作
           - 特别注意：inventory表不能直接操作，它是通过业务逻辑自动维护的

        3. **安全规范**
           - 所有INSERT和UPDATE操作必须包含WHERE条件，防止全表操作
           - 涉及商品名或仓库名时，必须先通过子查询获取对应ID
           - 任何更新操作都必须明确指定更新条件
           - 不要尝试直接更新inventory表，它由系统自动维护

        4. **输出格式要求**
           - 如果是日常对话，直接提供自然、友好的回答，不要以标点符号开头
           - 如果需要执行数据库操作，按照以下格式输出：
             [思考过程] 
             在这里详细描述你的分析和思考过程
             [/思考过程]
             [SQL]
             SQL语句
             [/SQL]
             
           - 输出必须包含完整的开始和结束标记
           - 禁止只输出SQL语句
           - 禁止输出代码块标记如(</think>)
           - 回答结束时不要添加任何额外的字符或标记
           - 对话回复应该以自然语言开头，不要以标点符号开头
           - 不要输出任何括号内的解释性内容
           - 在编写INSERT语句时，确保语法正确，特别是包含子查询时
           - 时间字段使用NOW()函数，不要加引号
           - 确保INSERT语句包含所有非空字段
           - 子查询必须使用LIMIT 1限制，确保只返回一行数据
           - 严格按照MySQL 9.4语法规范生成SQL语句

        5. **错误处理**
           - 如果请求超出SQL能力范围，进行友好解释
           - 如果请求操作禁止的表，进行说明
           - 如果缺少必要参数，询问用户提供更多信息

        表结构说明：
         核心表 
         product（商品）
          id, name, description, category, unit_price, unit, created_at

         warehouse（仓库）
          id, name, location, capacity

         inventory（实时库存）
          product_id, warehouse_id, quantity, last_updated
          注意：此表由系统自动维护，不能直接通过SQL更新

         流水表 
         stock_in（入库记录）
          id, product_id, warehouse_id, quantity, operator, in_time, notes
          注意：in_time字段使用NOW()填充

         stock_out（出库记录）
          id, product_id, warehouse_id, quantity, operator, out_time, notes
          注意：out_time字段使用NOW()填充


        示例参考：
        1. 对话类
        用户：你好，你能帮我做什么？
        回答：您好！我是仓库管理AI助手，可以帮助您查询库存信息、执行出入库操作、分析库存数据等。您可以使用自然语言告诉我您需要什么，我会尽力协助您。

        用户：我想了解一下我们仓库的情况
        回答：[思考过程]
        用户想了解仓库情况，我可以查询所有仓库的信息来提供详细数据。
        [/思考过程]
        [SQL]
        SELECT * FROM warehouse
        [/SQL]

        2. 查询类
        用户：查询库存低于安全值(100)的商品
        回答：[思考过程]
        用户需要查询低库存商品，我需要联接inventory、product和warehouse表来获取完整信息，条件是库存数量小于100。
        [/思考过程]
        [SQL]
        SELECT p.name, w.name, i.quantity 
              FROM inventory i
              JOIN product p ON i.product_id = p.id
              JOIN warehouse w ON i.warehouse_id = w.id
              WHERE i.quantity < 100
        [/SQL]

        3. 插入类（入库）
        用户：将50台MacBook Pro入库到华南仓（操作人：李经理）
        回答：[思考过程]
        用户想要执行入库操作，我需要插入stock_in表。首先通过子查询获取"MacBook Pro"的商品ID和"华南仓"的仓库ID，然后插入记录。系统会自动更新库存。
        [/思考过程]
        [SQL]
        INSERT INTO stock_in (product_id, warehouse_id, quantity, operator, in_time)
              VALUES (
                (SELECT id FROM product WHERE name = 'MacBook Pro' LIMIT 1),
                (SELECT id FROM warehouse WHERE name = '华南仓' LIMIT 1),
                50, 
                '李经理',
                NOW()
              )
        [/SQL]

        4. 插入类（出库）
        用户：从仓库"华南备件仓"出库20个商品"清风抽纸"（操作人：王主管）
        回答：[思考过程]
        用户想要执行出库操作，我需要插入stock_out表。首先通过子查询获取"清风抽纸"的商品ID和"华南备件仓"的仓库ID，然后插入记录。系统会自动更新库存。
        [/思考过程]
        [SQL]
        INSERT INTO stock_out (product_id, warehouse_id, quantity, operator, out_time)
              VALUES (
                (SELECT id FROM product WHERE name = '清风抽纸' LIMIT 1),
                (SELECT id FROM warehouse WHERE name = '华南备件仓' LIMIT 1),
                20, 
                '王主管',
                NOW()
              )
        [/SQL]

        5. 更新类
        用户：将清风抽纸单价更新为29.9
        回答：[思考过程]
        用户需要更新商品价格，我需要更新product表中的unit_price字段，并且必须包含WHERE条件以确保只更新指定商品。
        [/思考过程]
        [SQL]
        UPDATE product 
              SET unit_price = 29.9
              WHERE name = '清风抽纸'
        [/SQL]
        """;

    @Autowired
    public AISQLAssistantService(JdbcTemplate jdbcTemplate, SpringAIProperties springAIProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.springAIProperties = springAIProperties;
        refreshChatClient();
    }

    public void refreshChatClient() {
        // 创建新的ChatClient实例，使用默认配置
        OpenAiApi openAiApi = new OpenAiApi(springAIProperties.getBaseUrl(), springAIProperties.getApiKey());
        this.chatClient = new OpenAiChatClient(openAiApi,
                OpenAiChatOptions.builder()
                        .withModel(springAIProperties.getChat().getModel())
                        .build());
    }

    public Object processNaturalLanguageQuery(String userInput) {
        try { // 每次都重新创建ChatClient以确保使用最新的配置 refreshChatClient();
            // 1. 生成完整回答（包括思考过程和可能的SQL）
            String fullResponse = generateFullResponse(userInput);

            // 使用日志记录AI的完整回答
            logger.info("AI助手完整回答 - 用户输入: {}, AI回答: {}", userInput, fullResponse);

            if (fullResponse == null || fullResponse.trim().isEmpty()) {
                logger.warn("AI助手无法生成有效回答，用户输入: {}", userInput);
                return "AI助手无法生成回答";
            }

            // 2. 解析回答，提取SQL（如果有的话）并执行
            return parseAndExecuteResponse(fullResponse);
        } catch (ResourceAccessException e) {
            // 处理AI服务连接异常
            logger.error("AI服务连接失败，用户输入: {}", userInput, e);
            throw new AIServiceException("AI服务连接失败：" + e.getMessage(), e);
        } catch (Exception e) {
            return "处理出错: " + e.getMessage();
        }
    }

    private String generateFullResponse(String userInput) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // 构造完整的提示
                String fullPrompt = SYSTEM_PROMPT + "\n用户：" + userInput + "\n回答：";
                
                // 调用模型
                String response = chatClient.call(new Prompt(new UserMessage(fullPrompt))).getResult().getOutput().getContent();
                
                // 清理回答：去除可能的多余字符
                response = response.trim();
                
                return response;
            } catch (ResourceAccessException e) {
                retryCount++;
                logger.warn("AI服务连接失败，正在进行第 {} 次重试...", retryCount);
                
                if (retryCount >= maxRetries) {
                    // 处理AI服务连接异常
                    logger.error("AI服务连接失败，已重试 {} 次仍无法连接，请检查网络或AI服务状态", maxRetries);
                    throw new AIServiceException("AI服务连接失败，已重试 " + maxRetries + " 次仍无法连接，请检查网络或AI服务状态");
                }
                
                // 等待一段时间再重试
                try {
                    Thread.sleep(2000 * retryCount); // 逐步增加等待时间
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("重试过程中被中断", ie);
                    throw new AIServiceException("重试过程中被中断", ie);
                }
            } catch (Exception e) {
                logger.error("生成回答时出错", e);
                throw new AIServiceException("生成回答时出错: " + e.getMessage(), e);
            }
        }
        
        throw new RuntimeException("AI服务连接失败，已达到最大重试次数");
    }

    private Object parseAndExecuteResponse(String fullResponse) {
        // 检查是否包含SQL语句
        if (fullResponse.contains("[SQL]") && fullResponse.contains("[/SQL]")) {
            try {
                // 提取思考过程
                String thinkingProcess = "";
                if (fullResponse.contains("[思考过程]") && fullResponse.contains("[/思考过程]")) {
                    int start = fullResponse.indexOf("[思考过程]") + "[思考过程]".length();
                    int end = fullResponse.indexOf("[/思考过程]");
                    thinkingProcess = fullResponse.substring(start, end).trim();
                }

                // 提取SQL
                int start = fullResponse.indexOf("[SQL]") + "[SQL]".length();
                int end = fullResponse.indexOf("[/SQL]");
                String sql = fullResponse.substring(start, end).trim();

                // 清理SQL：去除可能的多余字符
                sql = sql.trim();
                
                // 移除可能的后缀字符
                if (sql.contains("[/SQL]")) {
                    sql = sql.substring(0, sql.indexOf("[/SQL]")).trim();
                }

                // 如果模型生成了多个SQL语句，我们只取第一个（用分号分割）
                if (sql.contains(";")) {
                    sql = sql.split(";")[0].trim();
                }

                // 执行SQL
                Object result = executeSQL(sql);

                // 返回思考过程和结果
                return Map.of(
                        "thinkingProcess", thinkingProcess,
                        "sql", sql,
                        "result", result
                );
            } catch (Exception e) {
                logger.error("解析或执行SQL时出错: {}", e.getMessage(), e);
                throw new AIServiceException("解析或执行SQL时出错: " + e.getMessage(), e);
            }
        } else {
            // 没有SQL，直接返回回答作为对话内容
            // 清理可能的不完整后缀
            String cleanResponse = fullResponse;
            if (cleanResponse.contains("[/思考过程]")) {
                int index = cleanResponse.indexOf("[/思考过程]");
                if (index > 0 && index < cleanResponse.length() - "[/思考过程]".length()) {
                    cleanResponse = cleanResponse.substring(0, index + "[/思考过程]".length());
                }
            }
            
            // 确保回复不以标点符号开头
            cleanResponse = cleanResponse.trim();
            while (cleanResponse.startsWith("、") || cleanResponse.startsWith(",") || cleanResponse.startsWith("，") || cleanResponse.startsWith("。") || cleanResponse.startsWith(".")) {
                cleanResponse = cleanResponse.substring(1).trim();
            }
            
            // 移除多余的括号内容
            cleanResponse = cleanResponse.replaceAll("\\(.*?\\)", "");
            
            return cleanResponse;
        }
    }

    private String generateSQL(String userInput) {
        try {
            // 构造完整的提示
            String fullPrompt = SYSTEM_PROMPT + "\n用户：" + userInput + "\nSQL：";
            
            // 调用模型
            String sql = chatClient.call(new Prompt(new UserMessage(fullPrompt))).getResult().getOutput().getContent();
            
            // 清理SQL：去除可能的多余字符
            sql = sql.trim();
            
            // 如果模型生成了多个SQL语句，我们只取第一个（用分号分割）
            if (sql.contains(";")) {
                sql = sql.split(";")[0].trim();
            }
            
            return sql;
        } catch (ResourceAccessException e) {
            // 处理AI服务连接异常
            throw new RuntimeException("AI服务连接失败");
        } catch (Exception e) {
            throw new RuntimeException("生成SQL时出错: " + e.getMessage());
        }
    }

    // 添加SQL验证方法，防止危险操作
    private void validateSQL(String sql) {
        String lowerCaseSql = sql.toLowerCase().trim();
        
        // 防止SQL注入和危险操作 - 更严格的检查
        if (lowerCaseSql.contains("drop") || 
            lowerCaseSql.contains("truncate") || 
            lowerCaseSql.contains("alter") ||
            lowerCaseSql.contains("grant") ||
            lowerCaseSql.contains("revoke") ||
            lowerCaseSql.contains("delete") ||
            lowerCaseSql.contains("create") ||
            lowerCaseSql.contains("replace") ||
            lowerCaseSql.contains("union") ||
            lowerCaseSql.contains("exec") ||
            lowerCaseSql.contains("execute")) {
            throw new SecurityException("检测到潜在的危险SQL操作");
        }
        
        // 检查注释和特殊字符，防止绕过检测
        if (sql.contains("/*") || sql.contains("*/") || sql.contains("--") || sql.contains("#")) {
            throw new SecurityException("检测到潜在的SQL注入尝试");
        }
        
        // 确保只操作允许的表
        String[] allowedTables = {"product", "warehouse", "stock_in", "stock_out"};
        boolean containsAllowedTable = false;
        
        for (String table : allowedTables) {
            if (lowerCaseSql.contains(table)) {
                containsAllowedTable = true;
                break;
            }
        }
        
        if (!containsAllowedTable) {
            throw new SecurityException("SQL操作了不允许的表");
        }
        
        // 禁止直接操作inventory表
        if (lowerCaseSql.contains("inventory") && 
            (lowerCaseSql.contains("update") || lowerCaseSql.contains("insert") || lowerCaseSql.contains("delete"))) {
            throw new SecurityException("禁止直接操作inventory表，该表由系统自动维护");
        }
        
        // 检查INSERT语句是否包含必要的时间字段
        if (lowerCaseSql.contains("insert into stock_in") && !lowerCaseSql.contains("in_time")) {
            throw new IllegalArgumentException("stock_in表的INSERT语句必须包含in_time字段");
        }
        
        if (lowerCaseSql.contains("insert into stock_out") && !lowerCaseSql.contains("out_time")) {
            throw new IllegalArgumentException("stock_out表的INSERT语句必须包含out_time字段");
        }
        
        // 验证UPDATE语句必须包含WHERE子句，防止全表更新
        if (lowerCaseSql.startsWith("update") && !lowerCaseSql.contains(" where ")) {
            throw new SecurityException("UPDATE语句必须包含WHERE条件以防止全表更新");
        }
        
        // 验证INSERT语句格式
        if (lowerCaseSql.startsWith("insert")) {
            // 检查INSERT语句是否符合基本格式
            if (!lowerCaseSql.matches("insert\\s+into\\s+\\w+\\s*\\([^)]*\\)\\s*values\\s*\\([^)]*\\).*")) {
                throw new SecurityException("INSERT语句格式不正确");
            }
        }
    }

    private Object executeSQL(String sql) {
        // 首先验证SQL安全性
        validateSQL(sql);
        
        String lowerCaseSql = sql.toLowerCase();
        
        // 判断SQL类型
        if (lowerCaseSql.startsWith("select")) {
            // 查询操作
            try {
                return jdbcTemplate.queryForList(sql);
            } catch (Exception e) {
                logger.error("SQL执行错误: {}", e.getMessage(), e);
                throw new AIServiceException("SQL执行错误: " + e.getMessage(), e);
            }
        } else if (lowerCaseSql.startsWith("insert") || 
                   lowerCaseSql.startsWith("update")) {
            // 更新操作
            try {
                int rowsAffected = jdbcTemplate.update(sql);
                return Map.of(
                    "message", "操作成功",
                    "rowsAffected", rowsAffected,
                    "operation", sql.split(" ")[0].toUpperCase()
                );
            } catch (Exception e) {
                logger.error("SQL执行错误: {}", e.getMessage(), e);
                throw new AIServiceException("SQL执行错误: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("不支持的SQL操作类型: " + sql);
        }
    }
}