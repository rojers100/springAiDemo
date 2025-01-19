package org.example.springaidemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.example.springaidemo.dao.mapper")
public class MyBatisConfig {
} 