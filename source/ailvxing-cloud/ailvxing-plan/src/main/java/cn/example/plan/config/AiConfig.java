package cn.example.plan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.deepseek")
public class AiConfig {

    private String apiKey;
    private String baseUrl;
    private String model;
}