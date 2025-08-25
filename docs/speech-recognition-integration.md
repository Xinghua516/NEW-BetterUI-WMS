# 语音识别引擎集成指南

## 当前实现

项目当前使用浏览器原生的Web Speech API实现语音识别功能，该API在不同浏览器和环境下的识别准确率可能有所不同。

## 更高准确率的替代方案

### 1. 百度语音识别

百度语音识别提供了业界领先的中文识别准确率，支持多种语言和方言。

#### 集成步骤：

1. **注册百度云账号**
   - 访问[百度云控制台](https://console.bce.baidu.com/)
   - 注册并登录账号

2. **创建应用并获取API密钥**
   - 在百度云控制台中创建语音识别应用
   - 获取API Key和Secret Key

3. **引入百度语音识别SDK**
   - 下载或通过CDN引入百度语音识别JavaScript SDK
   - 在ai-assistant.html页面中添加SDK引用

4. **实现百度语音识别功能**
   - 替换现有的`initBaiduASR()`函数实现
   - 使用百度API进行语音识别

#### 示例代码：

```javascript
// 初始化百度语音识别
function initBaiduASR() {
    // 配置百度语音识别参数
    const APP_ID = 'your_app_id';
    const API_KEY = 'your_api_key';
    const SECRET_KEY = 'your_secret_key';
    
    // 初始化百度语音识别客户端
    // 注意：这需要引入百度语音识别SDK
    const client = new baidu.voice.Recognition({
        appId: APP_ID,
        apiKey: API_KEY,
        secretKey: SECRET_KEY
    });
    
    // 设置识别参数
    client.setOption({
        lang: 'zh-CN',
        rate: 16000,
        devPid: 1537 // 中文普通话
    });
    
    // 绑定事件
    client.on('result', function(result) {
        document.getElementById('userInput').value = result;
        stopListening();
    });
    
    client.on('error', function(error) {
        console.error('百度语音识别错误:', error);
        stopListening();
        alert('语音识别出错: ' + error);
    });
    
    recognition = client;
}
```

### 2. 讯飞语音识别

科大讯飞语音识别在中文识别方面也有很高的准确率。

#### 集成步骤：

1. 注册讯飞开放平台账号
2. 创建应用并获取APPID和API Key
3. 下载并集成讯飞语音识别SDK
4. 实现讯飞语音识别功能

### 3. 阿里云语音识别

阿里云智能语音交互服务提供高准确率的语音识别能力。

#### 集成步骤：

1. 注册阿里云账号
2. 开通智能语音交互服务
3. 获取AccessKey ID和AccessKey Secret
4. 集成阿里云语音识别SDK
5. 实现语音识别功能

## 配置选项

项目已在设置界面中添加了"使用百度语音识别"选项，用户可以选择使用更高准确率的语音识别引擎。

## 注意事项

1. 第三方语音识别服务通常需要网络连接
2. 大部分商业语音识别服务都有调用次数限制
3. 需要妥善保管API密钥，避免泄露
4. 在使用第三方服务时，应处理网络异常和超时情况
5. 建议提供降级方案，当第三方服务不可用时回退到原生API