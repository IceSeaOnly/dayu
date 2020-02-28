package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * 首页拼团图墙
 *
 * @date 2020/2/10 上午11:48
 **/
@Data
@Conf(value = "首页拼团图墙", exampleImg = "http://cdn.binghai.site/ca6f2a2b-6a99-59d7-31bd-8441f71c47f4.png")
public class PinTuanIndexImgWall implements KvSupport {
    @Conf("标题红色部分")
    private String redText;
    @Conf("标题黑色部分")
    private String blackText;
    @Conf("标题小字部分")
    private String smallText;
    @Conf("更多按钮链接")
    private String moreUrl;

    @Conf("左侧大图链接")
    private String leftImg;
    @Conf("右侧图1")
    private String right01;
    @Conf("右侧图2")
    private String right02;
    @Conf("右侧图3")
    private String right03;
    @Conf("右侧图4")
    private String right04;

}
