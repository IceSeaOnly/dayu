package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.ShipFeeRule;

import java.util.List;

/**
 * @author huaishuo
 * @date 2020/3/15 下午11:00
 **/
@Service
public class ShipFeeRuleService extends BaseService<ShipFeeRule> {

    public int calFee(int total) {
        int ret = Integer.MAX_VALUE;
        for (ShipFeeRule r : findAll()) {
            if (total >= r.getMuch()) {
                ret = Math.min(ret, r.getMuch());
            }
        }
        return ret == Integer.MAX_VALUE ? 0 : ret;
    }

    public List<ShipFeeRule> findAll() {
        ShipFeeRule rule = new ShipFeeRule();
        return empty(query(rule));
    }
}
