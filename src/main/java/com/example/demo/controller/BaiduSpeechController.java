package com.example.demo.controller;

import com.example.demo.service.BaiduSpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/speech")
public class BaiduSpeechController {

    private static final Logger logger = LoggerFactory.getLogger(BaiduSpeechController.class);

    @Autowired
    private BaiduSpeechService baiduSpeechService;

    /**
     * 语音识别接口
     *
     * @param file 音频文件
     * @param format 音频格式 (pcm, wav, opus, speex, amr)
     * @param rate 采样率 (8000, 16000)
     * @param devPid 语言模型 (1537: 普通话, 1536: 普通话(纯中文识别))
     * @return 识别结果
     */
    @PostMapping("/recognize")
    public ResponseEntity<Map<String, Object>> recognizeSpeech(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "wav") String format,
            @RequestParam(value = "rate", defaultValue = "16000") int rate,
            @RequestParam(value = "devPid", defaultValue = "1537") int devPid) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 检查服务是否已配置
            if (!baiduSpeechService.isConfigured()) {
                response.put("success", false);
                response.put("message", "百度语音服务未正确配置，请检查API Key和Secret Key");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "音频文件为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 执行语音识别
            byte[] audioData = file.getBytes();
            String result = baiduSpeechService.recognizeSpeech(audioData, format, rate, devPid);

            if (result != null) {
                response.put("success", true);
                response.put("result", result);
            } else {
                response.put("success", false);
                response.put("message", "语音识别失败");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("语音识别失败", e);
            response.put("success", false);
            response.put("message", "语音识别过程中发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 检查服务配置状态
     *
     * @return 配置状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("configured", baiduSpeechService.isConfigured());
        response.put("message", baiduSpeechService.isConfigured() ? 
            "百度语音服务已正确配置" : 
            "百度语音服务未配置，请在application.properties中设置API Key和Secret Key");
        return ResponseEntity.ok(response);
    }
}