package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

@Data
@Conf("首页按钮配置")
public class IndexButtonConfig implements KvSupport {
    @Conf(value = "左1 icon", img = true)
    private String btn1Img;
    @Conf("左1链接")
    private String btn1Url;
    @Conf("左1文案")
    private String btn1Text;
    @Conf(value = "左2 icon", img = true)
    private String btn2Img;
    @Conf("左2链接")
    private String btn2Url;
    @Conf("左2文案")
    private String btn2Text;
    @Conf(value = "左3 icon", img = true)
    private String btn3Img;
    @Conf("左3链接")
    private String btn3Url;
    @Conf("左3文案")
    private String btn3Text;
    @Conf(value = "左4 icon", img = true)
    private String btn4Img;
    @Conf("左4链接")
    private String btn4Url;
    @Conf("左4文案")
    private String btn4Text;

}
