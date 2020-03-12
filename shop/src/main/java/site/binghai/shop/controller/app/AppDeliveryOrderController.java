package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

/**
 * @author huaishuo
 * @date 2020/3/10 下午11:02
 **/
@RestController
@RequestMapping("app")
public class AppDeliveryOrderController extends AppBaseController {

    @Autowired
    private ShopOrderService shopOrderService;


    @GetMapping("deliveryOrder")
    public Object deliveryOrder(@RequestParam String token, @RequestParam Long orderId) {
        return verifyDoing(token, appToken -> {
            ShopOrder order = shopOrderService.findById(orderId);
            if (order == null || !order.getSchoolId().equals(appToken.getSchoolId())) {
                return fail("订单不存在!");
            }
            if (!order.getBindRider().equals(appToken.getId())) {
                return fail("只能处理自己抢到的订单!");
            }
            if (!order.getStatus().equals(OrderStatusEnum.PROCESSING.getCode())) {
                return fail("订单状态错误!" + OrderStatusEnum.valueOf(order.getStatus()).getName());
            }
            order.setBindRider(appToken.getId());
            order.setStatus(OrderStatusEnum.COMPLETE.getCode());
            shopOrderService.update(order);
            return success();
        });
    }
}
