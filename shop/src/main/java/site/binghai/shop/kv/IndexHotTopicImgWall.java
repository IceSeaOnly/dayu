package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.def.KvSupport;

/**
 *
 * @date 2020/2/10 下午12:15
 **/
@Data
public class IndexHotTopicImgWall implements KvSupport {
    private String redText;
    private String blackText;
    private String smallText;
    private String moreUrl;

    private String leftBig;
    private String right1;
    private String right2;
    private String right3;
    private String right4;

    private String down1;
    private String down2;
    private String down3;
    private String down4;
}
