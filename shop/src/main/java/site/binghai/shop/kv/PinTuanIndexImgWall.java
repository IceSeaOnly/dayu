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
@Conf(value = "首页拼团图墙")
public class PinTuanIndexImgWall implements KvSupport {
    @Conf("标题红色部分")
    private String redText;
    @Conf("标题黑色部分")
    private String blackText;
    @Conf("标题小字部分")
    private String smallText;
    @Conf("更多按钮链接")
    private String moreUrl;

    @Conf(value = "左侧大图",img = true)
    private String leftImg;
    @Conf("左侧大图跳转链接")
    private String leftImgTarget;
    @Conf(value = "右侧图1",img = true)
    private String right01;
    @Conf("右侧图1跳转链接")
    private String right01Target;
    @Conf(value = "右侧图2",img = true)
    private String right02;
    @Conf("右侧图2跳转链接")
    private String right02Target;
    @Conf(value = "右侧图3",img = true)
    private String right03;
    @Conf("右侧图3跳转链接")
    private String right03Target;
    @Conf(value = "右侧图4",img = true)
    private String right04;
    @Conf("右侧图4跳转链接")
    private String right04Target;

}
