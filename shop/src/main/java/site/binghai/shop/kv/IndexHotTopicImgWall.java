package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @date 2020/2/10 下午12:15
 **/
@Data
@Conf(value = "首页主题热卖配置", exampleImg = "http://cdn.binghai.site/9bef103f-904e-4696-3c66-435d0778176a.png")
public class IndexHotTopicImgWall implements KvSupport {
    @Conf("标题红色部分")
    private String redText;
    @Conf("标题黑色部分")
    private String blackText;
    @Conf("标题小字部分")
    private String smallText;
    @Conf("更多超链接地址")
    private String moreUrl;

    @Conf("左侧大图链接地址")
    private String leftBig;
    @Conf("右1图链接地址")
    private String right1;
    @Conf("右2图链接地址")
    private String right2;
    @Conf("右3图链接地址")
    private String right3;
    @Conf("右4图链接地址")
    private String right4;

    @Conf("下1图链接地址")
    private String down1;
    @Conf("下2图链接地址")
    private String down2;
    @Conf("下3图链接地址")
    private String down3;
    @Conf("下4图链接地址")
    private String down4;
}
