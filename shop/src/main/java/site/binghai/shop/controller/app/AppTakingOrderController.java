package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.WxUserService;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;
import java.util.Map;

/**
 * @author icesea
 * @date 2020/3/10 下午11:02
 **/
@RestController
@RequestMapping("app")
public class AppTakingOrderController extends AppBaseController {
    @Autowired
    private WxEventHandler wxEventHandler;
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("takingOrder")
    public Object takingOrder(@RequestParam String token) {
        return verifyDoing(token, appToken -> {
            Map<Long, List<ShopOrder>> orders = shopOrderService.findByStatusAndTime(0L, now(),
                    OrderStatusEnum.PROCESSING);
            return success(orders, null);
        });
    }

    @GetMapping("takeOrder")
    public Object takeOrder(@RequestParam String token, @RequestParam Long batchId) {
        return verifyDoing(token, appToken -> {
            List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
            StringBuilder sb = new StringBuilder();
            Long userId = null;
            for (ShopOrder order : orders) {
                if (order == null || !order.getSchoolId().equals(appToken.getSchoolId())) {
                    return fail("订单不存在!");
                }
                if (!order.getStatus().equals(OrderStatusEnum.PROCESSING.getCode())) {
                    return fail("手慢了没抢到!");
                }
                order.setBindRider(appToken.getId());
                order.setStatus(OrderStatusEnum.DELIVERY.getCode());
                shopOrderService.update(order);
                sb.append("," + order.getTitle());
                userId = order.getUserId();
            }
            wxEventHandler.onOrderDelivering(wxUserService.findById(userId).getOpenId(), sb.toString().substring(1), appToken.getRemark(), appToken.getUserName());
            return success("抢单成功", null);
        });
    }

}
