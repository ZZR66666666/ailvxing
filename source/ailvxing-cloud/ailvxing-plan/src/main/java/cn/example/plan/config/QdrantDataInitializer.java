package cn.example.plan.config;

import cn.example.plan.service.EmbeddingService;
import cn.example.plan.service.QdrantService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component("qdrantDataInit")
@RequiredArgsConstructor
public class QdrantDataInitializer {

    private final QdrantService qdrantService;
    private final EmbeddingService embeddingService;

    @PostConstruct
    public void initData() {
        // 等待 Qdrant 集合初始化完成
        try { Thread.sleep(3000); } catch (Exception ignored) {}
        log.info("开始导入旅游目的地数据到 Qdrant...");

        add("三亚", "热带海滨城市，亚龙湾天涯海角，阳光沙滩椰林，适合冬季避寒度假，海鲜美食丰富",
            "海 海滩 热带 海鲜 潜水 亲子 蜜月 温暖 冬天");

        add("青岛", "红瓦绿树碧海蓝天，栈桥八大关崂山，啤酒海鲜闻名，夏季避暑胜地",
            "海 海滩 海鲜 啤酒 夏天 避暑 拍照 欧式建筑");

        add("厦门", "鼓浪屿万国建筑，环岛路骑行，曾厝垵文艺小店，闽南美食沙茶面海蛎煎",
            "海 海滩 岛屿 美食 小吃 文艺 拍照 骑行 情侣");

        add("丽江", "大研古城束河古镇，玉龙雪山泸沽湖，纳西族文化，适合慢生活和文艺旅拍",
            "古城 雪山 文化 民俗 徒步 拍照 历史 情侣 蜜月");

        add("大理", "苍山洱海风花雪月，白族民居三道茶，环海骑行，四季如春的浪漫之地",
            "湖 山 民俗 骑行 拍照 文艺 情侣 蜜月 春天");

        add("成都", "大熊猫基地宽窄巷子锦里，火锅串串麻辣烫，都江堰青城山，悠闲慢生活",
            "美食 火锅 川菜 小吃 夜市 熊猫 文化 历史 博物馆");

        add("西安", "兵马俑华清池大雁塔，回民街美食，城墙骑行，十三朝古都深厚历史",
            "历史 文化 古迹 博物馆 寺庙 美食 小吃 面食");

        add("北京", "故宫天安门长城颐和园，烤鸭涮羊肉，798艺术区胡同文化",
            "历史 文化 宫殿 长城 博物馆 购物 美食 寺庙");

        add("杭州", "西湖十景灵隐寺，龙井茶园西溪湿地，江南水乡的精致与优雅",
            "湖 园林 寺庙 茶 美食 拍照 骑行 春天 秋天");

        add("桂林", "漓江竹筏阳朔西街，象鼻山龙脊梯田，喀斯特地貌山水画卷",
            "山 河流 峡谷 拍照 骑行 徒步 春天 秋天");

        add("张家界", "天门山玻璃栈道，袁家界哈利路亚山，大峡谷黄龙洞，阿凡达取景地",
            "山 峡谷 森林 徒步 拍照 冒险 秋天");

        add("西藏", "布达拉宫大昭寺，纳木错羊卓雍措，珠峰大本营，雪域高原心灵之旅",
            "雪山 高原 寺庙 佛教 文化 徒步 冒险 拍照");

        add("新疆", "喀纳斯禾木天山天池，吐鲁番葡萄沟，大巴扎烤羊肉手抓饭",
            "草原 湖 山 沙漠 美食 烧烤 民俗 秋天 自驾");

        add("内蒙古", "呼伦贝尔大草原额尔古纳，蒙古包骑马射箭，烤全羊手把肉",
            "草原 骑马 露营 民俗 烧烤 自驾 夏天 秋天");

        add("云南", "昆明春城大理丽江香格里拉，西双版纳热带雨林，过桥米线汽锅鸡",
            "民俗 美食 寺庙 古城 山 湖 拍照 春天 秋天");

        add("上海", "外滩陆家嘴迪士尼，田子坊新天地，本帮菜小笼包，时尚购物之都",
            "购物 美食 现代 建筑 历史 亲子");

        add("重庆", "洪崖洞解放碑磁器口，长江索道武隆天坑，火锅小面麻辣鲜香",
            "美食 火锅 山 夜景 拍照 小吃 夜市");

        add("苏州", "拙政园虎丘寒山寺，周庄同里古镇，苏绣评弹松鼠桂鱼",
            "园林 古镇 湖 文化 历史 美食 春天 秋天");

        add("昆明", "石林滇池翠湖，斗南花市鲜花论斤卖，过桥米线发源地，四季如春",
            "花 湖 山 拍照 美食 春天 避暑 温暖");

        add("哈尔滨", "冰雪大世界中央大街，索菲亚教堂松花江，红肠锅包肉马迭尔冰棍",
            "冰雪 建筑 冬天 美食 拍照 滑雪 亲子");

        log.info("Qdrant 目的地数据导入完成（20个热门目的地）");
    }

    private void add(String name, String desc, String tags) {
        try {
            float[] vector = embeddingService.encode(desc + " " + tags);
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("name", name);
            payload.put("description", desc);
            payload.put("tags", tags);
            qdrantService.upsert(name, vector, payload);
        } catch (Exception e) {
            log.warn("导入失败 {}: {}", name, e.getMessage());
        }
    }
}
