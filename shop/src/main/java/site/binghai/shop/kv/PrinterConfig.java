package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

/**
 * @Author: IceSea
 * @Date: 2020/5/9 10:24 下午
 */
@Data
@Conf("打印机配置")
public class PrinterConfig implements KvSupport {
    @Conf("clientId")
    private String clientId;
    @Conf("clientSecret")
    private String clientSecret;
    @Conf("machineCode")
    private String machineCode;
    @Conf("machineSecret")
    private String machineSecret;
}
