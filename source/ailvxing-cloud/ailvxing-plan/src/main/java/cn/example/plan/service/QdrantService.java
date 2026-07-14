package cn.example.plan.service;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Qdrant 向量数据库服务 —— 通过 REST API 操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QdrantService {

    private final RestClient qdrantRestClient;
    private final EmbeddingService embeddingService;

    private static final String COLLECTION = "travel_destinations";
    private static final int VECTOR_SIZE = 32;

    /** 将字符串转为 UUID 作为 Point ID */
    private String toPointId(String s) {
        UUID uuid = UUID.nameUUIDFromBytes(s.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        // 取低 64 位作为 unsigned long
        long id = uuid.getMostSignificantBits() & 0x7FFFFFFFFFFFFFFFL;
        return String.valueOf(id);
    }

    @PostConstruct
    public void init() {
        try {
            // 创建集合（幂等，已存在则跳过）
            Map<String, Object> body = Map.of(
                "vectors", Map.of(
                    "size", VECTOR_SIZE,
                    "distance", "Cosine"
                )
            );
            String resp = qdrantRestClient.put()
                .uri("/collections/" + COLLECTION)
                .body(body)
                .retrieve()
                .body(String.class);
            log.info("Qdrant collection created: {}", resp);
        } catch (Exception e) {
            log.info("Qdrant collection may already exist: {}", e.getMessage());
        }
    }

    /**
     * 插入向量点
     */
    public void upsert(String name, float[] vector, Map<String, Object> payload) {
        String pointId = toPointId(name);
        try {
            // 手动构造 JSON 避免序列化问题
            StringBuilder json = new StringBuilder();
            json.append("{\"points\":[{\"id\":").append(pointId).append(",\"vector\":[");
            for (int i = 0; i < vector.length; i++) {
                if (i > 0) json.append(",");
                json.append(vector[i]);
            }
            json.append("],\"payload\":{");
            boolean first = true;
            for (Map.Entry<String, Object> e : payload.entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(e.getKey()).append("\":\"")
                    .append(String.valueOf(e.getValue()).replace("\"", "\\\"")).append("\"");
                first = false;
            }
            json.append("}}]}");

            String resp = qdrantRestClient.put()
                .uri("/collections/" + COLLECTION + "/points?wait=true")
                .body(json.toString())
                .retrieve()
                .body(String.class);
            log.debug("Upserted {}: {}", pointId, resp);
        } catch (Exception e) {
            log.warn("Qdrant upsert failed for {} (id={}): {}", name, pointId, e.getMessage());
        }
    }

    /**
     * 语义搜索
     */
    public List<SearchResult> search(String queryText, int limit) {
        float[] vector = embeddingService.encode(queryText);
        try {
            // 手动构造请求 JSON
            StringBuilder reqJson = new StringBuilder("{\"vector\":[");
            for (int i = 0; i < vector.length; i++) {
                if (i > 0) reqJson.append(",");
                reqJson.append(vector[i]);
            }
            reqJson.append("],\"limit\":").append(limit).append(",\"with_payload\":true}");

            // 获取原始响应 bytes，手动转字符串
            byte[] respBytes = qdrantRestClient.post()
                .uri("/collections/" + COLLECTION + "/points/search")
                .body(reqJson.toString())
                .retrieve()
                .body(byte[].class);
            String respStr = new String(respBytes != null ? respBytes : new byte[0], "UTF-8");

            // 简单解析 JSON（避免 Jackson 反序列化中文编码问题）
            List<SearchResult> results = new ArrayList<>();
            if (respStr != null && respStr.contains("\"result\"")) {
                results = parseSearchResults(respStr);
            }
            return results;
        } catch (Exception e) {
            log.error("Qdrant search failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /** 简单 JSON 解析，提取搜索结果 */
    private List<SearchResult> parseSearchResults(String json) {
        List<SearchResult> results = new ArrayList<>();
        try {
            // 找到 result 数组
            int resultStart = json.indexOf("\"result\":[");
            if (resultStart < 0) return results;
            int pos = resultStart + 10;
            // 逐个解析结果对象
            while (pos < json.length()) {
                if (json.charAt(pos) == ']') break;
                if (json.charAt(pos) == '{') {
                    // 提取一个对象
                    int objEnd = findMatchingBrace(json, pos);
                    String obj = json.substring(pos, objEnd + 1);
                    SearchResult sr = new SearchResult();

                    sr.setScore(1.0f - extractFloat(obj, "score"));

                    // 提取 payload
                    int payloadStart = obj.indexOf("\"payload\":{");
                    if (payloadStart >= 0) {
                        int payloadEnd = findMatchingBrace(obj, payloadStart + 10);
                        String payloadStr = obj.substring(payloadStart + 10, payloadEnd + 1);
                        sr.setName(extractString(payloadStr, "name"));
                        sr.setDescription(extractString(payloadStr, "description"));
                        sr.setTags(extractString(payloadStr, "tags"));
                    }
                    results.add(sr);
                    pos = objEnd + 1;
                } else {
                    pos++;
                }
            }
        } catch (Exception e) {
            log.warn("Parse search results failed: {}", e.getMessage());
        }
        return results;
    }

    private float extractFloat(String json, String key) {
        int keyIdx = json.indexOf("\"" + key + "\":");
        if (keyIdx < 0) return 0;
        int valStart = keyIdx + key.length() + 3;
        int valEnd = valStart;
        while (valEnd < json.length() && (Character.isDigit(json.charAt(valEnd)) || json.charAt(valEnd) == '.' || json.charAt(valEnd) == '-' || json.charAt(valEnd) == 'e' || json.charAt(valEnd) == 'E')) {
            valEnd++;
        }
        try { return Float.parseFloat(json.substring(valStart, valEnd)); } catch (Exception e) { return 0; }
    }

    private String extractString(String json, String key) {
        int keyIdx = json.indexOf("\"" + key + "\":\"");
        if (keyIdx < 0) return "";
        int valStart = keyIdx + key.length() + 4;
        int valEnd = valStart;
        while (valEnd < json.length()) {
            if (json.charAt(valEnd) == '"' && (valEnd == valStart || json.charAt(valEnd - 1) != '\\')) break;
            valEnd++;
        }
        return json.substring(valStart, valEnd).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private int findMatchingBrace(String s, int start) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '{') depth++;
            else if (s.charAt(i) == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return s.length() - 1;
    }

    @Data
    public static class SearchResult {
        private String name;
        private String description;
        private String tags;
        private float score;
    }
}
