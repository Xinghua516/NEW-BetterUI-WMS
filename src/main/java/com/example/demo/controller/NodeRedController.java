package com.example.demo.controller;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

@Controller
public class NodeRedController {

    // 存储接收到的数据
    private Map<String, Object> receivedData = new HashMap<>();
    private Date lastReceivedTime = null;

    @GetMapping("/nodered")
    public String nodeRedPage() {
        return "nodered-dispatch";
    }

    @PostMapping("/nodered/check-connection")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkNodeRedConnection(@RequestParam("targetUrl") String targetUrl) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 尝试发送一个请求来检查连接
            restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);
            
            response.put("connected", true);
            response.put("message", "连接成功");
        } catch (Exception e) {
            response.put("connected", false);
            response.put("message", "连接失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nodered/send-signal")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendSignalToNodeRed(@RequestParam("targetUrl") String targetUrl,
                                                                  @RequestParam("payload") String payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            
            // 创建请求实体，使用用户输入的内容
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            
            // 发送请求到Node-RED
            ResponseEntity<String> restResponse = restTemplate.postForEntity(targetUrl, entity, String.class);
            
            response.put("success", true);
            response.put("message", "信号发送成功");
            response.put("response", restResponse.getBody());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "信号发送失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nodered/send")
    @ResponseBody
    public ResponseEntity<String> sendToNodeRed(@RequestBody(required = false) String payload,
                                                @RequestHeader("nodered-url") String nodeRedUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            // 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            // 发送请求到Node-RED
            ResponseEntity<String> response = restTemplate.postForEntity(nodeRedUrl, entity, String.class);

            // 打印Node-RED返回的响应内容到控制台
            System.out.println("从Node-RED返回的响应数据类型: " + response.getClass().getName());
            System.out.println("从Node-RED返回的响应HTTP状态码: " + response.getStatusCode());
            System.out.println("从Node-RED返回的响应内容: " + response.getBody());
            System.out.println("从Node-RED返回的响应头部: " + response.getHeaders());

            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain")
                    .body(response.getBody());
        } catch (Exception e) {
            System.err.println("发送到Node-RED时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // 接收来自Node-RED的数据端点
    @PostMapping("/nodered/receive")
    @ResponseBody
    public ResponseEntity<String> receiveFromNodeRed(@RequestBody(required = false) String payload) {
        try {
            // 记录接收到的数据和时间
            receivedData.put("payload", payload);
            receivedData.put("timestamp", new Date());
            lastReceivedTime = new Date();

            System.out.println("从Node-RED接收到数据: " + payload);
            System.out.println("接收时间: " + lastReceivedTime);
            System.out.println("数据类型: " + (payload != null ? payload.getClass().getName() : "null"));

            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain")
                    .body("数据接收成功");
        } catch (Exception e) {
            System.err.println("接收数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("接收数据时出错: " + e.getMessage());
        }
    }

    // 接收来自Node-RED的JSON数据端点
    @PostMapping("/nodered/receive/json")
    @ResponseBody
    public ResponseEntity<String> receiveJsonFromNodeRed(@RequestBody(required = false) Map<String, Object> payload) {
        try {
            // 记录接收到的数据和时间
            receivedData.put("jsonPayload", payload);
            receivedData.put("timestamp", new Date());
            lastReceivedTime = new Date();

            System.out.println("从Node-RED接收到JSON数据: " + payload);
            System.out.println("接收时间: " + lastReceivedTime);
            System.out.println("数据类型: " + (payload != null ? payload.getClass().getName() : "null"));
            if (payload != null) {
                System.out.println("JSON键数量: " + payload.size());
                System.out.println("JSON键列表: " + payload.keySet());
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body("{\"message\": \"JSON数据接收成功\"}");
        } catch (Exception e) {
            System.err.println("接收JSON数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"接收JSON数据时出错: " + e.getMessage() + "\"}");
        }
    }

    // 获取最后接收的数据
    @GetMapping("/nodered/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLastReceivedData() {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(receivedData);
    }
}