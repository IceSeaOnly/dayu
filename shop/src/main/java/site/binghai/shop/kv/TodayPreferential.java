package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.def.KvSupport;

import java.util.List;

/**
 * 今日特惠关联的商品id
 *
 * @date 2020/2/10 上午11:28
 **/
@Data
public class TodayPreferential implements KvSupport {
    private List<Long> itemIdList;
}
