package site.binghai.lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Coupon;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.dao.UnifiedOrderDao;
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.lib.utils.TimeTools;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/4/5. GitHub: https://github.com/IceSeaOnly
 */
@Service
public class UnifiedOrderService extends BaseService<UnifiedOrder> {
    @Autowired
    private UnifiedOrderDao dao;
    @Autowired
    private CouponService couponService;
    @Autowired
    private WxUserService wxUserService;

    @Override
    protected JpaRepository<UnifiedOrder, Long> getDao() {
        return dao;
    }

    public Long countByAppCodeAndStatus(PayBizEnum pb, Integer status) {
        return dao.countBySchoolIdAndAppCodeAndStatus(SchoolIdThreadLocal.getSchoolId(), pb.getCode(), status);
    }

    public Long countByDate(PayBizEnum pb, Long[] date, OrderStatusEnum... status) {
        List<Integer> ss = Arrays.stream(status).map(s -> s.getCode()).collect(Collectors.toList());
        return dao.countBySchoolIdAndAppCodeAndStatusInAndCreatedBetween(SchoolIdThreadLocal.getSchoolId(), pb.getCode(), ss, date[0], date[1]);
    }

    public Long countToday(PayBizEnum pb) {
        return dao.countBySchoolIdAndAppCodeAndCreatedAfter(SchoolIdThreadLocal.getSchoolId(), pb.getCode(), TimeTools.today()[0]);
    }

    public List<UnifiedOrder> list(PayBizEnum payBiz, OrderStatusEnum status, Integer page, Integer pageSize) {
        if (page == null || page < 0) {
            page = 0;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = 100;
        }

        return dao.findAllBySchoolIdAndAppCodeAndStatusOrderByCreatedDesc(SchoolIdThreadLocal.getSchoolId(), payBiz.getCode(), status.getCode(),
                new PageRequest(page, pageSize));
    }

    @Transactional
    public UnifiedOrder newOrderWithCoupon(PayBizEnum biz, WxUser user, String title, int payMuch, Long couponId) {
        if (couponService.consume(user.getId(), couponId)) {
            Coupon coupon = couponService.findById(couponId);
            payMuch -= coupon.getCouponValue();
        }
        UnifiedOrder order = new UnifiedOrder();
        order.setAppCode(biz.getCode());
        order.setCouponId(couponId);
        order.setOpenId(user.getOpenId());
        order.setUserId(user.getId());
        order.setUserName(user.getUserName());
        order.setUserPhone(user.getPhone());
        order.setTitle(title);
        order.setShouldPay(payMuch);
        order.setOriginalPrice(payMuch);
        return save(order);
    }

    @Transactional
    public UnifiedOrder newOrder(PayBizEnum biz, WxUser user, String title, int payMuch) {
        return newOrderWithCoupon(biz, user, title, payMuch, null);
    }

    @Transactional
    public void bindCoupon(UnifiedOrder order, Coupon coupon) {
        int shouldPay = order.getShouldPay();
        if (couponService.consume(order.getUserId(), coupon.getId())) {
            shouldPay -= coupon.getCouponValue();
        }

        if (order.getCouponId() != null) {
            shouldPay += couponService.unuse(order.getCouponId()).getCouponValue();
        }

        order.setShouldPay(shouldPay);
        order.setCouponId(coupon.getId());
        update(order);
    }

    public UnifiedOrder findByOrderId(String orderKey) {
        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setStatus(null);
        unifiedOrder.setOrderId(orderKey);
        return queryOne(unifiedOrder);
    }

    /**
     * @Params search : 用户姓名或手机号中的某几位
     */
    public List<UnifiedOrder> findBySearchWords(String search) {
        List<UnifiedOrder> res = emptyList();

        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setOrderId(null);
        unifiedOrder.setStatus(null);
        unifiedOrder.setUserName(search);

        res.addAll(query(unifiedOrder));
        unifiedOrder.setUserName(null);
        unifiedOrder.setUserPhone(search);

        res.addAll(query(unifiedOrder));
        return res;
    }

    public List<UnifiedOrder> findByUserIdOrderByIdDesc(Long userId, Integer page, Integer pageSize) {
        if (page == null || page < 0) {
            page = 0;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = 100;
        }
        return dao.findAllByUserIdOrderByIdDesc(userId, new PageRequest(page, pageSize));
    }

    @Transactional
    public boolean cancel(Long id) {
        UnifiedOrder order = findById(id);
        order.setStatus(OrderStatusEnum.REFUNDING.getCode());
        update(order);
        if (order.getCouponId() != null) {
            couponService.unuse(order.getCouponId());
        }
        if (order.getPoints() != null && order.getPoints() > 0) {
            wxUserService.recoveryShoppingPoints(order.getUserId(), order.getPoints());
        }
        return true;
    }


    public List<UnifiedOrder> scanTimeOut() {
        return dao.findAllByStatusAndCreatedBefore(OrderStatusEnum.CREATED.getCode(),
                now() - 15 * 6000);
    }

    public Integer sumShouldPay(PayBizEnum pb, Long[] date, OrderStatusEnum... status) {
        Integer ret = dao.sum(SchoolIdThreadLocal.getSchoolId(), pb.getCode(), date[0], date[1], Arrays.stream(status)
                .map(s -> s.getCode())
                .collect(Collectors.toList()));
        return ret == null ? 0 : ret;
    }

    public List<UnifiedOrder> loadRefunding() {
        return dao.findAllByStatusAndCreatedBefore(OrderStatusEnum.REFUNDING.getCode(),
                now());
    }
}