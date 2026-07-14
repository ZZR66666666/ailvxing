package cn.example.plan.service;

import cn.example.plan.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final AiConfig aiConfig;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public String chat(String userMessage) {
        return chat(userMessage, "你是一个专业的旅行规划师，请根据用户需求生成详细的旅行规划。");
    }

    public String chat(String userMessage, String systemPrompt) {
        try {
            String body = """
                    {
                        "model": "%s",
                        "temperature": 0.7,
                        "max_tokens": 4096,
                        "messages": [
                            {"role": "system", "content": %s},
                            {"role": "user", "content": %s}
                        ]
                    }
                    """.formatted(
                    aiConfig.getModel(),
                    escapeJson(systemPrompt),
                    escapeJson(userMessage)
            );

            log.info("DeepSeek API请求: model={}, url={}", aiConfig.getModel(), aiConfig.getBaseUrl());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiConfig.getBaseUrl() + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + aiConfig.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(120))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("DeepSeek API响应: status={}", response.statusCode());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                int choicesIdx = responseBody.indexOf("\"choices\"");
                if (choicesIdx == -1) {
                    log.error("DeepSeek API返回格式异常: {}", responseBody);
                    return null;
                }
                int contentIdx = responseBody.indexOf("\"content\"", choicesIdx);
                if (contentIdx == -1) {
                    log.error("DeepSeek API返回无content: {}", responseBody);
                    return null;
                }
                String content = extractContent(responseBody, contentIdx);
                log.info("DeepSeek API生成成功, 内容长度={}", content.length());
                return content;
            }

            log.error("DeepSeek API调用失败: status={}, body={}", response.statusCode(), response.body());
            return null;
        } catch (Exception e) {
            log.error("DeepSeek API调用异常: {}", e.getMessage(), e);
            return null;
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "\"\"";
        String escaped = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }

    private String extractContent(String json, int contentIdx) {
        int start = json.indexOf("\"", contentIdx + 9) + 1;
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}