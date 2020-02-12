package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.def.KvSupport;

/**
 * 首页拼团图墙
 *
 * @date 2020/2/10 上午11:48
 **/
@Data
public class PinTuanIndexImgWall implements KvSupport {
    private String redText;
    private String blackText;
    private String smallText;
    private String moreUrl;

    private String leftImg;
    private String right01;
    private String right02;
    private String right03;
    private String right04;

}
