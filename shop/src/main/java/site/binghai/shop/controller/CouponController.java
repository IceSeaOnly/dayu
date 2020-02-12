package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Coupon;
import site.binghai.lib.entity.CouponPlan;
import site.binghai.lib.enums.CouponStatus;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.CouponPlanService;
import site.binghai.lib.service.CouponService;
import site.binghai.lib.utils.HttpUtils;
import site.binghai.shop.entity.CartItem;
import site.binghai.shop.service.CartItemService;
import site.binghai.shop.service.ProductService;

import java.util.List;

/**
 *
 * @date 2020/2/3 下午7:57
 **/
@Controller
@RequestMapping("shop")
public class CouponController extends BaseController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponPlanService couponPlanService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemService cartItemService;

    @GetMapping("coupon")
    public String coupon(ModelMap map, String type) {
        if (type == null) {
            type = "INIT";
        }
        List<Coupon> coupons = couponService.findByUserIdAndStatus(getUser().getId(), CouponStatus.valueOf(type));
        map.put("coupons", coupons);
        return "coupon";
    }

    @GetMapping("couponRecommend")
    public String couponRecommend(ModelMap map) {
        map.put("coupons", couponPlanService.findAvailable());
        return "couponRecommend";
    }

    @GetMapping("getCoupon")
    @ResponseBody
    public Object getCoupon(@RequestParam Long cpId) {
        CouponPlan plan = couponPlanService.findById(cpId);
        if (plan == null) {
            return fail("非法访问");
        }
        if (plan.getInvalidTs() < now()) {
            return fail("该优惠券已经停止发放啦!");
        }
        if (couponService.exist(getUser().getId(), cpId)) {
            if (!plan.getRepeatTake()) {
                return fail("你已经领取过啦!不能重复领取哦~");
            } else {
                if (!isEmptyList(couponService.findByPlanIdAndStatus(getUser().getId(), cpId, CouponStatus.INIT))) {
                    return fail("你已经领取过啦!先去使用吧~");
                }
            }
        }
        couponService.create(getUser(), plan);
        return success();
    }

    @GetMapping("selectCouponForPayment")
    public String selectCouponForPayment(@RequestParam String cartIds, @RequestParam Long addressId, ModelMap map) {
        List<CartItem> cartItems = emptyList();
        int total = 0;
        if (!hasEmptyString(cartIds)) {
            String[] ids = cartIds.split(",");
            for (String id : ids) {
                Long cid = Long.parseLong(id);
                CartItem item = cartItemService.findById(cid);
                if (item == null || !item.getBuyerId().equals(getUser().getId())) {
                    return e500("非法访问");
                }
                item.setProduct(productService.findById(item.getProductId()));
                cartItems.add(item);
                total += item.getProduct().getPrice() * item.getSize();
            }
        } else {
            return e500("整挺好，啥都没选");
        }

        map.put("cartIds", cartIds);
        map.put("addressId", addressId);
        map.put("coupons", couponService.listAvailableCoupon(getUser().getId(), PayBizEnum.SCHOOL_SHOP, total));
        return "selectCoupon";
    }
}
