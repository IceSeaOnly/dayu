package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @author huaishuo
 * @date 2020/3/5 下午10:29
 **/
@Data
@Conf("「我的」页面配置")
public class MyPageConfig implements KvSupport {
    @Conf("钱包名称")
    private String walletName;
    @Conf("钱包充值文案")
    private String chargeText;
    @Conf("钱包充值链接")
    private String chargeUrl;
    @Conf("钱包图片")
    private String walletImg;

}
