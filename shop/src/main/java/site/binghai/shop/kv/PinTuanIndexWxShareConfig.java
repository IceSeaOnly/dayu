package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @author icesea
 * @date 2020/3/3 下午4:03
 **/
@Data
@Conf("拼团首页微信分享配置")
public class PinTuanIndexWxShareConfig implements KvSupport {
    @Conf("微信关键参数获取链接，不清楚不要改")
    private String configGetUrl;
    @Conf("分享标题")
    private String shareTitle;
    @Conf("分享链接")
    private String shareLink;
    @Conf(value = "分享图片", img = true)
    private String shareImgUrl;
    @Conf("分享描述")
    private String shareDesc;
}
