package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class BaiduSpeechService {

    private static final Logger logger = LoggerFactory.getLogger(BaiduSpeechService.class);

    @Value("${baidu.speech.api-key:}")
    private String apiKey;

    @Value("${baidu.speech.secret-key:}")
    private String secretKey;

    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String ASR_URL = "https://vop.baidu.com/server_api";

    private String accessToken;
    private long tokenExpireTime;

    /**
     * 获取百度API访问令牌
     *
     * @return 访问令牌
     */
    private String getAccessToken() {
        // 检查令牌是否有效
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");
            map.add("client_id", apiKey);
            map.add("client_secret", secretKey);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            Map<String, Object> response = restTemplate.postForObject(TOKEN_URL, request, Map.class);

            if (response != null && response.containsKey("access_token")) {
                accessToken = (String) response.get("access_token");
                Integer expiresIn = (Integer) response.get("expires_in");
                tokenExpireTime = System.currentTimeMillis() + (expiresIn != null ? expiresIn * 1000L : 0);
                return accessToken;
            }
        } catch (Exception e) {
            logger.error("获取百度API访问令牌失败", e);
        }

        return null;
    }

    /**
     * 语音识别
     *
     * @param audioData 音频数据
     * @param format    音频格式 (pcm, wav, opus, speex, amr)
     * @param rate      采样率 (8000, 16000)
     * @param devPid    语言模型 (1537: 普通话, 1536: 普通话(纯中文识别))
     * @return 识别结果
     */
    public String recognizeSpeech(byte[] audioData, String format, int rate, int devPid) {
        try {
            String token = getAccessToken();
            if (token == null) {
                logger.error("无法获取百度API访问令牌");
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String base64Audio = Base64.getEncoder().encodeToString(audioData);

            Map<String, Object> requestBody = Map.of(
                    "format", format,
                    "rate", rate,
                    "channel", 1,
                    "cuid", "demo_app",
                    "token", token,
                    "dev_pid", devPid,
                    "speech", base64Audio,
                    "len", audioData.length
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(ASR_URL, request, Map.class);

            if (response != null && response.containsKey("result")) {
                Object resultObj = response.get("result");
                if (resultObj instanceof java.util.List) {
                    java.util.List<?> resultList = (java.util.List<?>) resultObj;
                    if (!resultList.isEmpty()) {
                        return resultList.get(0).toString();
                    }
                }
            }

            logger.warn("百度语音识别未返回有效结果: " + response);
        } catch (Exception e) {
            logger.error("百度语音识别失败", e);
        }

        return null;
    }

    /**
     * 检查配置是否有效
     *
     * @return 配置是否有效
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty() && secretKey != null && !secretKey.isEmpty();
    }
}