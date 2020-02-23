package site.binghai.lib.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Coupon;
import site.binghai.lib.entity.CouponPlan;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.CouponStatus;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.utils.TimeTools;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @date 2020/2/4 下午12:43
 **/
@Service
public class CouponService extends BaseService<Coupon> {

    public List<Coupon> listAvailableCoupon(Long userId, PayBizEnum biz, int shouldPay) {
        Coupon coupon = new Coupon();
        coupon.setBizType(biz);
        coupon.setUserId(userId);
        coupon.setCouponStatus(CouponStatus.INIT);

        List<Coupon> list = empty(sortQuery(coupon, "couponValue", true));
        list.forEach(p -> {
            if (p.getMinLimit() > shouldPay) {
                p.setAvailable(false);
                p.setDisableReason("不满足满减条件");
            } else {
                p.setAvailable(true);
            }
        });
        return enrich(list);
    }

    public boolean consume(Long userId, Long couponId) {
        if (couponId == null) {
            return false;
        }
        Coupon coupon = findById(couponId);
        if (coupon.getCouponStatus() != CouponStatus.INIT) {
            return false;
        }
        if (!coupon.getUserId().equals(userId)) {
            return false;
        }
        if (coupon.getInvalidTs() < now()) {
            return false;
        }
        coupon.setCouponStatus(CouponStatus.USED);
        update(coupon);
        return true;
    }

    public boolean exist(Long userId, Long cpId) {
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        coupon.setCouponId(cpId);
        return count(coupon) > 0;
    }

    @Transactional
    public void create(WxUser user, CouponPlan plan) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(plan, coupon);
        coupon.setUserId(user.getId());
        coupon.setCouponId(plan.getId());
        coupon.setCouponStatus(CouponStatus.INIT);
        coupon.setId(null);
        save(coupon);
    }

    public List<Coupon> findByPlanIdAndStatus(Long userId, Long couponId, CouponStatus status) {
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        coupon.setCouponId(couponId);
        coupon.setCouponStatus(status);
        return enrich(query(coupon));
    }

    private List<Coupon> enrich(List<Coupon> query) {
        for (Coupon coupon : query) {
            if (now() > coupon.getValidTs()) {
                coupon.setValidTime(String.format("有效期至%s",
                    TimeTools.format2yyyy_MM_dd(coupon.getInvalidTs())));
            } else {
                coupon.setValidTime(String.format("%s后可用",
                    TimeTools.format2yyyy_MM_dd(coupon.getValidTs())));
            }
        }
        return query;
    }

    public List<Coupon> findByUserId(Long userId) {
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        return enrich(query(coupon));
    }

    public List<Coupon> findByUserIdAndStatus(Long userId, CouponStatus status) {
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        coupon.setCouponStatus(status);
        return enrich(query(coupon));
    }

    @Transactional
    public Coupon unuse(Long couponId) {
        Coupon coupon = findById(couponId);
        coupon.setCouponStatus(CouponStatus.INIT);
        update(coupon);
        return coupon;
    }
}
