package cn.example.plan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Data
@Configuration
@ConfigurationProperties(prefix = "qdrant")
public class QdrantConfig {

    private String url;      // REST API URL
    private String apiKey;

    @Bean
    public RestClient qdrantRestClient() {
        return RestClient.builder()
            .baseUrl(url)
            .defaultHeader("api-key", apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
