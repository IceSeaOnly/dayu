package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Coupon;
import site.binghai.lib.entity.MergePayOrder;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.CouponService;
import site.binghai.lib.service.MergePayService;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.service.WxUserService;
import site.binghai.shop.entity.Address;
import site.binghai.shop.entity.CartItem;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.AddressService;
import site.binghai.shop.service.CartItemService;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.ShopOrderService;

import java.util.Map;

/**
 *
 * @date 2020/2/2 下午12:323
 **/
@RestController
@RequestMapping("shop")
public class SubmitController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private MergePayService mergePayService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private WxUserService wxUserService;

    @PostMapping("mergePay")
    public Object mergePay(@RequestBody Map map) {
        String cartIds = getString(map, "cartIds");
        String remark = getString(map, "remark");
        Long couponId = getLong(map, "couponId");
        Long addrId = getLong(map, "addrId");
        Boolean usePoints = getBoolean(map, "usePoints");

        WxUser user = getUser();
        String mergePayTitle = null;
        int sum = 0;
        int total = 0;
        String unifiedIds = "";

        if (addrId == -1) {
            return fail("哦我的亲,还没选择地址!");
        }
        Address address = addressService.findById(addrId);
        if (address == null || !address.getUserId().equals(user.getId())) {
            return fail("哦上帝啊，你的地址有问题呀");
        }
        Coupon coupon = null;
        if (couponId > 0) {
            coupon = couponService.findById(couponId);
            if (coupon == null || !coupon.getUserId().equals(user.getId())) {
                return fail("哦上帝啊，你的优惠券有问题呀");
            }
        }

        String[] ids = cartIds.split(",");
        if (ids.length < 1) {
            return e500("别搞了，你啥都没选啊");
        }
        for (String id : ids) {
            CartItem item = cartItemService.findById(Long.parseLong(id));
            if (item == null || !item.getBuyerId().equals(user.getId())) {
                return e500("非法访问");
            }
            Product product = productService.findById(item.getProductId());
            if (product == null || product.getOffline()) {
                return e500("商品" + product.getTitle() + "已下架或不存在");
            }
            if (product.getStock() < item.getSize()) {
                return e500("商品" + product.getTitle() + "库存不足");
            }
            sum += item.getSize();
            total += item.getSize() * product.getPrice();
            if (mergePayTitle == null) {
                mergePayTitle = product.getTitle();
            }
            cartItemService.delete(item.getId());

            //依次下单
            ShopOrder shopOrder = submitOrder(product, item.getSize(), item.getStandardInfo(), remark, address);
            if (ids.length == 1) {
                bindCouponAndPoints(shopOrder.getUnifiedId(), coupon, usePoints);
                return success(shopOrder, null);
            }
            unifiedIds += "," + shopOrder.getUnifiedId();
        }
        mergePayTitle += "等" + sum + "件商品";

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(PayBizEnum.MERGE_PAY, getUser(), mergePayTitle,
            total);
        bindCouponAndPoints(unifiedOrder.getId(), coupon, usePoints);
        MergePayOrder mergePayOrder = new MergePayOrder();
        mergePayOrder.setUnifiedId(unifiedOrder.getId());
        mergePayOrder.setUnifiedIds(unifiedIds.substring(1));
        mergePayOrder.setStatus(OrderStatusEnum.CREATED.getCode());
        mergePayOrder.setUserId(getUser().getId());
        mergePayOrder.setPaid(Boolean.FALSE);
        mergePayService.save(mergePayOrder);
        return success(mergePayOrder, null);
    }

    private void bindCouponAndPoints(Long unifiedId, Coupon coupon, Boolean usePoints) {
        if (coupon == null && !usePoints) {
            return;
        }
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(unifiedId);
        if (coupon != null) {
            unifiedOrderService.bindCoupon(unifiedOrder, coupon);
        }
        if (usePoints) {
            WxUser user = wxUserService.findById(getUser().getId());
            int points = user.getShoppingPoints();
            int shouldPay = unifiedOrder.getShouldPay();
            if (shouldPay >= points) {
                shouldPay -= points;
                points = 0;
            } else if (shouldPay < points) {
                points -= shouldPay;
                shouldPay = 0;
            }
            user.setShoppingPoints(points);
            unifiedOrder.setShouldPay(shouldPay);
            wxUserService.update(user);
            unifiedOrderService.update(unifiedOrder);
        }
    }

    private ShopOrder submitOrder(Product product, int size, String stdInfo, String remark, Address address) {
        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(PayBizEnum.SCHOOL_SHOP, getUser(), product.getTitle(),
            product.getPrice() * size);
        ShopOrder order = new ShopOrder();
        order.setProductImgUrl(product.getImgUrl());
        order.setTitle(product.getTitle());
        order.setRemark(remark);
        order.setUserId(getUser().getId());
        order.setBuyerName(getUser().getUserName());
        order.setUnifiedId(unifiedOrder.getId());
        order.setExpiredTime(now() + 15 * 6000);
        order.setTotalPrice(product.getPrice() * size);
        order.setStatus(OrderStatusEnum.CREATED.getCode());
        order.setSize(size);
        order.setPrice(product.getPrice());
        order.setProductId(product.getId());
        order.setStandardInfo(stdInfo);
        order.setPaid(Boolean.FALSE);
        order.setReceiver(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getReceiverAddr());
        product.setStock(product.getStock() - size);
        product.setSold(product.getSold() + size);
        productService.update(product);
        order = shopOrderService.save(order);
        return order;
    }

    private String extraStandardInfo(Map body) {
        StringBuilder sb = new StringBuilder();
        body.forEach((k, v) -> {
            sb.append(k + ":" + v + " ");
        });
        return sb.toString();
    }
}
