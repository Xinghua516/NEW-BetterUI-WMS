package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 获取天气数据
     * @return 天气信息Map
     */
    public Map<String, Object> getWeatherData() {
        Map<String, Object> weatherInfo = new HashMap<>();
        
        try {
            // 使用心知天气API获取天气信息
            String location = "杭州";
            String url = String.format(
                "https://api.seniverse.com/v3/weather/now.json?key=%s&location=%s&language=zh-Hans&unit=c",
                apiKey, location);

            // 调用真实API获取数据
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 调试信息输出
            logger.debug("天气API响应结构: " + response);
            
            // 解析API响应
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                Map<String, Object> result = results.get(0);
                
                // 从result中提取location和now数据
                Map<String, Object> locationData = (Map<String, Object>) result.get("location");
                Map<String, Object> now = (Map<String, Object>) result.get("now");
                
                weatherInfo.put("text", now.get("text"));
                weatherInfo.put("temperature", now.get("temperature"));
                weatherInfo.put("code", now.get("code"));
                weatherInfo.put("location", locationData.get("name"));
                weatherInfo.put("updateTime", result.get("last_update"));
                
                // 添加详细天气信息
                weatherInfo.put("wind", now.get("wind_direction") != null ? now.get("wind_direction") : "--");
                weatherInfo.put("humidity", now.get("humidity") != null ? now.get("humidity") : "--");
                weatherInfo.put("uv", "--"); // API响应中未包含UV数据
            } else {
                // API响应异常时的默认值
                logger.warn("API响应结构不符合预期或results为空");
                setDefaultWeatherInfo(weatherInfo);
            }
            
        } catch (Exception e) {
            // 记录异常信息并返回默认值
            // 使用DEBUG级别记录详细错误信息
            logger.error("天气API调用失败: ", e);
            setDefaultWeatherInfo(weatherInfo);
        }
        
        return weatherInfo;
    }
    
    /**
     * 设置默认天气信息
     * @param weatherInfo 天气信息Map
     */
    private void setDefaultWeatherInfo(Map<String, Object> weatherInfo) {
        weatherInfo.put("text", "无法获取天气信息");
        weatherInfo.put("temperature", "--");
        weatherInfo.put("code", "0");
        weatherInfo.put("location", "未知位置");
        weatherInfo.put("updateTime", "--");
        weatherInfo.put("wind", "--");
        weatherInfo.put("humidity", "--");
        weatherInfo.put("uv", "--");
    }
    
    /**
     * 根据天气代码获取对应的Bootstrap图标类名
     * @param weatherCode 天气代码
     * @return 图标类名
     */
    public String getWeatherIconClass(String weatherCode) {
        if (weatherCode == null) {
            return "bi-cloud-sun";
        }
        
        switch (weatherCode) {
            case "0":
                return "bi-sun"; // 晴天
            case "1":
            case "2":
            case "3":
                return "bi-cloud"; // 多云
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                return "bi-cloud-rain"; // 雨天
            case "10":
            case "11":
            case "12":
            case "13":
            case "14":
                return "bi-cloud-snow"; // 雪天
            case "15":
            case "16":
            case "17":
            case "18":
                return "bi-cloud-lightning"; // 雷电
            case "19":
            case "20":
            case "21":
            case "22":
            case "23":
            case "24":
            case "25":
                return "bi-cloud-fog"; // 雾霾
            default:
                return "bi-cloud-sun"; // 默认图标
        }
    }
}