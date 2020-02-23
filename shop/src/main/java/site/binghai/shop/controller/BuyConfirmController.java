package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Coupon;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.CouponStatus;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.CouponService;
import site.binghai.lib.service.WxUserService;
import site.binghai.shop.entity.Address;
import site.binghai.shop.entity.CartItem;
import site.binghai.shop.service.AddressService;
import site.binghai.shop.service.CartItemService;
import site.binghai.shop.service.ProductService;

import java.util.List;

/**
 * @date 2020/2/5 上午10:00
 **/
@RequestMapping("shop")
@Controller
public class BuyConfirmController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("buyConfirm")
    public Object buyConfirm(String cartIds, Long selectedCoupon, Long addressId, ModelMap map) {
        WxUser user = wxUserService.findById(getUser().getId());
        List<CartItem> cartItems = emptyList();
        int total = 0;
        int size = 0;
        if (!hasEmptyString(cartIds)) {
            String[] ids = cartIds.split(",");
            for (String id : ids) {
                Long cid = Long.parseLong(id);
                CartItem item = cartItemService.findById(cid);
                if (item == null || !item.getBuyerId().equals(user.getId())) {
                    return e500("订单可能已提交");
                }
                item.setProduct(productService.findById(item.getProductId()));
                cartItems.add(item);
                total += item.getProduct().getPrice() * item.getSize();
                size += item.getSize();
            }
        } else {
            return e500("整挺好，啥都没选");
        }

        String shipFee = "免邮费 ￥0";
        Coupon coupon = null;
        int sourceTotal = total;
        boolean moreCoupon = true;
        if (selectedCoupon != null && selectedCoupon > 0) {
            coupon = couponService.findById(selectedCoupon);
            if (coupon == null || coupon.getCouponStatus() != CouponStatus.INIT) {
                return e500("优惠券选的不对吧老弟?");
            }
            if (!coupon.getUserId().equals(user.getId())) {
                return e500("别对别的人的优惠券动手动脚的老弟");
            }
            total -= coupon.getCouponValue();
        } else {
            moreCoupon = !isEmptyList(couponService.listAvailableCoupon(user.getId(), PayBizEnum.SCHOOL_SHOP, total));
        }
        if (addressId != null && addressId > 0) {
            Address address = addressService.findById(addressId);
            if (address == null || !address.getUserId().equals(user.getId())) {
                return e500("你的地址有点问题小老弟");
            }
            map.put("address", address);
        } else {
            List<Address> list = addressService.findByUserId(user.getId());
            if (!isEmptyList(list)) {
                map.put("address", list.get(0));
                addressId = list.get(0).getId();
            }
        }
        map.put("addressId", addressId == null ? -1 : addressId);
        map.put("cartIds", cartIds);
        map.put("moreCoupon", moreCoupon);
        map.put("selectedCoupon", coupon);
        map.put("cartItems", cartItems);
        map.put("shipFee", shipFee);
        map.put("user", user);
        map.put("maxPoints", maxPoints(user.getShoppingPoints(), total));
        map.put("totalSize", size);
        map.put("totalPrice", Math.max(total, 0));
        map.put("sourceTotal", Math.max(sourceTotal, 0));
        return "confirm";
    }

    private int maxPoints(int shoppingPoints, int total) {
        if (shoppingPoints < 100) {
            return shoppingPoints;
        }
        shoppingPoints = shoppingPoints - shoppingPoints % 100;
        while (shoppingPoints * 5 > total) {
            shoppingPoints -= 100;
        }
        return shoppingPoints;
    }

}
