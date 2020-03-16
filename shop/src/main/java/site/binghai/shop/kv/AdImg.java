package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @author icesea
 * @date 2020/3/1 下午11:45
 **/
@Data
@Conf("广告模块")
public class AdImg implements KvSupport {
    @Conf(value = "首页中间广告",img = true)
    private String indexMiddleAdImg;
    @Conf("首页中间广告跳转链接")
    private String indexMiddleAdImgTarget;
    @Conf(value = "分类-热门推荐上广告",img = true)
    private String categoryPageAdImg;
    @Conf("分类-热门推荐上广告跳转链接")
    private String categoryPageAdImgTarget;
}
