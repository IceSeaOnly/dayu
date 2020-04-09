package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;

@Conf("自动接单配置")
@Data
public class AutoAcceptOrderConfig implements KvSupport {
    @Conf("是否启用自动接单打印,Y为启用，N为禁用")
    public String enable;
    @Conf("是否启用自动设为已发货,Y为启用，N为禁用")
    public String markProcessing;
    @Conf("打印联数，>= 0")
    public Integer printPieces;
}
