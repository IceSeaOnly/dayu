package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.def.KvSupport;

import java.util.List;

/**
 * 拼团首页推荐，今日秒拼
 *
 * @author huaishuo
 * @date 2020/2/23 下午6:41
 **/
@Data
public class PinTodayRecommend implements KvSupport {
    private List<Long> itemIds;
}
