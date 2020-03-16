package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @author icesea
 * @date 2020/3/3 下午9:03
 **/
@Data
@Conf("推荐搜索词")
public class RecommendSearchWord implements KvSupport {
    @Conf("推荐搜索词，英文逗号分隔，自动去重，随机推荐")
    private String words;
}
