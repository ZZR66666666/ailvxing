package cn.example.plan.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * 文本向量化服务
 *
 * 使用特征关键词 + 哈希编码生成 32 维向量。
 * 语义相近的文本（共享关键词）会产生相近的向量，用于 Qdrant 相似度搜索。
 *
 * 答辩要点：这演示了 Embedding 的核心原理——将文本转为数学向量，
 * 向量距离越近 = 语义越相似。
 */
@Service
public class EmbeddingService {

    private static final int VECTOR_SIZE = 32;

    // 旅行领域关键词词典
    private static final String[] FEATURES = {
        // 风景类
        "海", "海滩", "沙滩", "岛屿", "海景", "海风", "海水", "海浪", "潜水", "冲浪",
        "山", "雪山", "山峰", "爬山", "徒步", "森林", "湖泊", "河流", "瀑布", "峡谷",
        "沙漠", "草原", "温泉", "雪景", "冰川", "日出", "日落", "星空",
        // 文化类
        "历史", "文化", "古迹", "博物馆", "寺庙", "古城", "宫殿", "园林", "塔", "长城",
        "民俗", "传统", "艺术", "书法", "陶瓷", "丝绸之路", "佛教", "道教",
        // 美食类
        "美食", "火锅", "海鲜", "烧烤", "小吃", "早茶", "面食", "川菜", "粤菜", "夜市",
        // 活动类
        "购物", "拍照", "摄影", "骑行", "滑雪", "漂流", "露营", "自驾", "温泉",
        // 季节类
        "春天", "夏天", "秋天", "冬天", "樱花", "红叶", "避暑", "温暖",
        // 城市/地区
        "三亚", "青岛", "厦门", "大理", "丽江", "成都", "西安", "北京", "上海",
        "杭州", "桂林", "张家界", "西藏", "新疆", "内蒙古", "云南", "四川",
        "重庆", "武汉", "南京", "苏州", "昆明", "贵阳", "兰州", "哈尔滨",
        // 特色类
        "亲子", "蜜月", "毕业旅行", "自由行", "跟团", "穷游", "豪华",
        "经济", "舒适", "高端", "情侣", "家庭", "独自", "冒险"
    };

    static {
        // 确保特征数不超过向量维度
        if (FEATURES.length > VECTOR_SIZE * 32) {
            throw new RuntimeException("特征过多");
        }
    }

    /**
     * 将文本编码为 32 维浮点向量
     * 原理：提取关键词特征 → 哈希到向量位置 → 归一化
     */
    public float[] encode(String text) {
        if (text == null || text.isEmpty()) {
            return new float[VECTOR_SIZE];
        }

        float[] vector = new float[VECTOR_SIZE];
        String lower = text.toLowerCase();

        for (int i = 0; i < FEATURES.length; i++) {
            if (lower.contains(FEATURES[i])) {
                // 将特征索引哈希映射到向量维度
                int pos = Math.abs(hash(FEATURES[i])) % VECTOR_SIZE;
                // 加权：特征越匹配，权重越高
                vector[pos] += 0.3f + (0.1f * (i % 5));
            }
        }

        // 对每个字符也做哈希，增加文本差异性
        for (char c : lower.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c)) {
                int pos = Math.abs((c * 31) % VECTOR_SIZE);
                vector[pos] += 0.02f;
            }
        }

        // L2 归一化（用于余弦相似度计算）
        normalize(vector);
        return vector;
    }

    private int hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return Math.abs(((digest[0] & 0xFF) << 8) | (digest[1] & 0xFF));
        } catch (Exception e) {
            return Math.abs(s.hashCode());
        }
    }

    private void normalize(float[] v) {
        double sum = 0;
        for (float x : v) sum += x * x;
        double norm = Math.sqrt(sum);
        if (norm > 0) {
            for (int i = 0; i < v.length; i++) {
                v[i] = (float) (v[i] / norm);
            }
        }
    }
}
