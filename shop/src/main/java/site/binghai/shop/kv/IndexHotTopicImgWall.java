package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @date 2020/2/10 下午12:15
 **/
@Data
@Conf(value = "首页主题热卖配置")
public class IndexHotTopicImgWall implements KvSupport {
    @Conf("标题红色部分")
    private String redText;
    @Conf("标题黑色部分")
    private String blackText;
    @Conf("标题小字部分")
    private String smallText;
    @Conf("更多超链接")
    private String moreUrl;

    @Conf("左侧大图")
    private String leftBig;
    @Conf("右1图")
    private String right1;
    @Conf("右2图")
    private String right2;
    @Conf("右3图")
    private String right3;
    @Conf("右4图")
    private String right4;

    @Conf("下1图")
    private String down1;
    @Conf("下2图")
    private String down2;
    @Conf("下3图")
    private String down3;
    @Conf("下4图")
    private String down4;
}
