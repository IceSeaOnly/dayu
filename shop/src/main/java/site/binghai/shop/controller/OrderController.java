package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.utils.HttpUtils;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;

/**
 *
 * @date 2020/2/3 下午5:27
 **/
@Controller
@RequestMapping("shop")
public class OrderController extends BaseController {
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("orders")
    public String orders(ModelMap map, String type) {
        WxUser user = getUser();
        List<ShopOrder> shopOrders = null;
        if (type == null) {
            shopOrders = shopOrderService.findByUserId(user.getId());
        } else {
            shopOrders = shopOrderService.findByUserIdAndState(user.getId(), OrderStatusEnum.valueOf(type));
        }
        map.put("orders", shopOrders);
        map.put("type", type);
        map.put("typeEnum", OrderStatusEnum.class);
        map.put("allSize", shopOrderService.countByUserId(user.getId()));
        map.put("payingSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.CREATED));
        map.put("shippingSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.PAIED));
        map.put("shippedSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.PROCESSING));
        map.put("feedingSize", shopOrderService.countByUserIdAndState(user.getId(), OrderStatusEnum.COMPLETE));
        return "orders";
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
        return "redirect:leaveComment?orderId=" + orderId;
    }
}
