package com.example.demo.AI;

import reactor.core.publisher.Flux;

public interface ChatClient {
    String call(String message);
    Flux<String> stream(String message);
    
    /**
     * 生成符合MySQL 9.4规范的增删改查语句
     * @param naturalLanguageQuery 用自然语言描述的数据库操作需求
     * @return 符合MySQL 9.4规范的SQL语句
     */
    String generateSQL(String naturalLanguageQuery);
}