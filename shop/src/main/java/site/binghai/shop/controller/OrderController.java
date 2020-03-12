package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.service.WxUserService;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2020/2/3 下午5:27
 **/
@Controller
@RequestMapping("shop")
public class OrderController extends BaseController {
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("orders")
    public String orders(ModelMap map, String type) {
        WxUser user = getUser();
        List<ShopOrder> shopOrders = new ArrayList<>();
        if (type == null) {
            shopOrders = shopOrderService.findByUserId(user.getId());
        } else {
            OrderStatusEnum status = OrderStatusEnum.valueOf(type);
            List<ShopOrder> tmp = shopOrderService.findByUserIdAndState(user.getId(), status);
            if (!isEmptyList(tmp)) {
                shopOrders.addAll(tmp);
            }
            if (status == OrderStatusEnum.PROCESSING) {
                tmp = shopOrderService.findByUserIdAndState(user.getId(), OrderStatusEnum.DELIVERY);
                if (!isEmptyList(tmp)) {
                    shopOrders.addAll(tmp);
                }
            }
        }
        if (!isEmptyList(shopOrders)) {
            shopOrders.forEach(this::enrichUnifiedOrder);
        }
        map.put("orders", shopOrders);
        map.put("type", type);
        map.put("typeEnum", OrderStatusEnum.class);
        map.put("allSize", shopOrderService.countByUserId(user.getId()));
        map.put("payingSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.CREATED));
        map.put("processingSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.PAIED));
        map.put("shippingSize",
            shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.PROCESSING) + shopOrderService
                .countByUserIdAndState(user.getId(), OrderStatusEnum.DELIVERY));
        map.put("feedingSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.COMPLETE));
        return "orders";
    }

    private void enrichUnifiedOrder(ShopOrder shopOrder) {
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(shopOrder.getUnifiedId());
        shopOrder.setUnifiedOrder(unifiedOrder);
    }

    @GetMapping("confirmReceivedGood")
    public Object confirmReceivedGood(@RequestParam Long orderId) {
        ShopOrder order = shopOrderService.findById(orderId);
        if (order == null || !order.getUserId().equals(getUser().getId())) {
            return e500("非法访问!");
        }
        if (order.getStatus() >= OrderStatusEnum.PAIED.getCode() && order.getStatus() < OrderStatusEnum.COMPLETE
            .getCode()) {
            order.setStatus(OrderStatusEnum.COMPLETE.getCode());
            shopOrderService.update(order);
        }
        addUserPoint(order.getTotalPrice());
        return "redirect:leaveComment?orderId=" + orderId;
    }

    private void addUserPoint(Integer totalPrice) {
        int p = Math.max(1, totalPrice / 100);
        WxUser user = wxUserService.findById(getUser().getId());
        user.setHistoryShoppingPoints(user.getHistoryShoppingPoints() + p);
        user.setShoppingPoints(user.getShoppingPoints() + p);
        wxUserService.update(user);
        persistent(user);
    }
}
