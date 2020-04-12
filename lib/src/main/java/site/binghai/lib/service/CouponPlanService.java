package site.binghai.lib.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.binghai.lib.entity.CouponPlan;
import site.binghai.lib.utils.TimeTools;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @date 2020/2/4 下午12:59
 **/
@Service
public class CouponPlanService extends BaseService<CouponPlan> {

    public List<CouponPlan> findAvailable() {
        List<CouponPlan> plans = getDao().findAll(Sort.by(Sort.Direction.DESC, "id"));
        return plans.stream().filter(p -> p.getTotalSize() > 0 && now() < p.getInvalidTs())
            .peek(p -> p.setValidTime(String.format("%s - %s", TimeTools.format2yyyy_MM_dd(p.getValidTs()),
                TimeTools.format2yyyy_MM_dd(p.getInvalidTs())))).collect(
                Collectors.toList());
    }

    public List<CouponPlan> findAll() {
        return query(new CouponPlan());
    }
}
