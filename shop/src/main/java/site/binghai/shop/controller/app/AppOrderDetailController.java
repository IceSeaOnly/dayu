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
 * @date 2020/3/12 下午8:15
 **/
@RestController
@RequestMapping("app")
public class AppOrderDetailController extends AppBaseController {
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("takeOrder")
    public Object takeOrder(@RequestParam String token, @RequestParam Long orderId) {
        return verifyDoing(token, appToken -> {
            ShopOrder order = shopOrderService.findById(orderId);
            if (order == null || order.getSchoolId().equals(appToken.getSchoolId())) {
                return fail("订单不存在!");
            }
            if (!order.getStatus().equals(OrderStatusEnum.PAIED.getCode())) {
                return fail("抢单失败!");
            }
            order.setBindRider(appToken.getId());
            order.setStatus(OrderStatusEnum.PROCESSING.getCode());
            shopOrderService.update(order);
            return success();
        });
    }

}
