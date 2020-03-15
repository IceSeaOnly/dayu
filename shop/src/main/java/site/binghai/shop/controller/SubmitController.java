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
import site.binghai.shop.enums.TuanStatus;
import site.binghai.shop.service.*;
import site.binghai.shop.util.BatchIdGenerator;

import java.util.Map;

/**
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
    @Autowired
    private ShipFeeRuleService shipFeeRuleService;

    @GetMapping("getDeliveryFee")
    public Object getDeliveryFee(@RequestParam Integer total) {
        return success(shipFeeRuleService.calFee(total), null);
    }

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

        Long batchId = BatchIdGenerator.nextBatchId(getUser().getId());
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
            ShopOrder shopOrder = submitOrder(batchId, product, item, remark, address);

            if (ids.length == 1) {
                bindCouponAndPoints(shopOrder.getUnifiedId(), coupon, usePoints);
                return success(shopOrder, null);
            }
            unifiedIds += "," + shopOrder.getUnifiedId();
        }
        mergePayTitle += "等" + sum + "件商品";

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(PayBizEnum.MERGE_PAY, getUser(), mergePayTitle,
            total + shipFeeRuleService.calFee(total));
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
            int shouldPay = unifiedOrder.getShouldPay();
            int points = maxPoints(user.getShoppingPoints(), shouldPay);
            if (points > 0) {
                shouldPay -= points * 5;
                user.setShoppingPoints(user.getShoppingPoints() - points);
                unifiedOrder.setPoints(points);
            }
            unifiedOrder.setShouldPay(shouldPay);
            wxUserService.update(user);
            unifiedOrderService.update(unifiedOrder);
        }
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

    private ShopOrder submitOrder(Long batchId, Product product, CartItem cartItem, String remark, Address address) {
        int size = cartItem.getSize();
        int deliveryFee = shipFeeRuleService.calFee(product.getPrice() * size);
        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(PayBizEnum.SCHOOL_SHOP, getUser(), product.getTitle(),
            product.getPrice() * size + deliveryFee);
        ShopOrder order = new ShopOrder();
        order.setProductImgUrl(product.getImgUrl());
        order.setTitle(product.getTitle());
        order.setRemark(remark);
        order.setUserId(getUser().getId());
        order.setBuyerName(getUser().getUserName());
        order.setBuyerAvatar(getUser().getAvatar());
        order.setUnifiedId(unifiedOrder.getId());
        order.setExpiredTime(now() + 15 * 6000);
        order.setTotalPrice(product.getPrice() * size);
        order.setStatus(OrderStatusEnum.CREATED.getCode());
        order.setSize(size);
        order.setPrice(product.getPrice());
        order.setProductId(product.getId());
        order.setStandardInfo(cartItem.getStandardInfo());
        order.setPaid(Boolean.FALSE);
        order.setReceiver(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getReceiverAddr());
        order.setBatchId(batchId);
        product.setStock(product.getStock() - size);
        product.setSold(product.getSold() + size);
        if (cartItem.getPtOrder() != null && cartItem.getPtOrder()) {
            order.setPtOrder(Boolean.TRUE);
            order.setTuanStatus(TuanStatus.INIT);
            order.setTuanId(cartItem.getJoinPtId());
        } else {
            order.setPtOrder(Boolean.FALSE);
        }
        order.setDeliveryFee(deliveryFee);
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
