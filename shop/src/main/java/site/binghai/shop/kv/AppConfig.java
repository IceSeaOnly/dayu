package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @author huaishuo
 * @date 2020/3/10 下午10:29
 **/
@Data
@Conf("app配置")
public class AppConfig implements KvSupport {
    @Conf("页首网页链接")
    private String indexWebViewUrl;

}