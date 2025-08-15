package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SQLExecutionService {

    @Autowired
    private DataSource dataSource;

    /**
     * 执行SQL查询并返回结果
     * @param sql SQL语句
     * @return 查询结果
     */
    public Map<String, Object> executeQuery(String sql) {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<String> columns = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
             
            // 记录实际执行的SQL语句，便于调试
            System.out.println("执行SQL查询: " + sql);
            
            // 验证SQL语句是否有效
            String trimmedSql = sql.trim();
            if (!isValidSQL(trimmedSql)) {
                throw new IllegalArgumentException("无效的SQL语句: " + trimmedSql);
            }
            
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // 获取列名
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(metaData.getColumnName(i));
                }

                // 获取数据行
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    rows.add(row);
                }
            }
        } catch (SQLSyntaxErrorException e) {
            // 捕获表不存在等语法错误并提供更友好的错误信息
            String message = e.getMessage();
            if (message.contains("doesn't exist")) {
                throw new RuntimeException("数据库表不存在，请检查SQL语句中的表名是否正确。原始错误: " + message, e);
            }
            throw new RuntimeException("SQL语法错误: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("执行SQL时发生错误: " + e.getMessage(), e);
        }

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("columns", columns);
        result.put("rows", rows);
        return result;
    }

    /**
     * 执行SQL更新操作（INSERT, UPDATE, DELETE）
     * @param sql SQL语句
     * @return 执行结果
     */
    public Map<String, Object> executeUpdate(String sql) {
        Map<String, Object> result = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
             
            // 记录实际执行的SQL语句，便于调试
            System.out.println("执行SQL更新: " + sql);
            
            // 验证SQL语句是否有效
            String trimmedSql = sql.trim();
            if (!isValidSQL(trimmedSql)) {
                throw new IllegalArgumentException("无效的SQL语句: " + trimmedSql);
            }
             
            int affectedRows = statement.executeUpdate(sql);
            result.put("success", true);
            result.put("message", "操作执行成功，影响行数: " + affectedRows);

        } catch (SQLSyntaxErrorException e) {
            // 捕获表不存在等语法错误并提供更友好的错误信息
            String message = e.getMessage();
            if (message.contains("doesn't exist")) {
                result.put("success", false);
                result.put("message", "数据库表不存在，请检查SQL语句中的表名是否正确。原始错误: " + message);
            } else {
                result.put("success", false);
                result.put("message", "SQL语法错误: " + e.getMessage());
            }
            result.put("executedSql", sql);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "执行SQL时发生错误: " + e.getMessage());
            result.put("executedSql", sql); // 添加执行的SQL语句到结果中，便于调试
            e.printStackTrace();
        }

        return result;
    }
    
    /**
     * 验证SQL语句是否有效
     * @param sql SQL语句
     * @return 是否有效
     */
    private boolean isValidSQL(String sql) {
        if (sql == null || sql.isEmpty()) {
            return false;
        }
        
        String lowerSql = sql.toLowerCase().trim();
        return lowerSql.startsWith("select") || 
               lowerSql.startsWith("insert") || 
               lowerSql.startsWith("update") || 
               lowerSql.startsWith("delete");
    }
}