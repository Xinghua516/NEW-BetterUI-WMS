package com.example.demo.AI;

import reactor.core.publisher.Flux;

public interface ChatClient {
    String call(String message);
    Flux<String> stream(String message);
    
    /**
     * 生成符合MySQL 9.4规范的查询语句
     * @param naturalLanguageQuery 用自然语言描述的查询需求
     * @return 符合MySQL 9.4规范的查询语句
     */
    String generateQuery(String naturalLanguageQuery);
}