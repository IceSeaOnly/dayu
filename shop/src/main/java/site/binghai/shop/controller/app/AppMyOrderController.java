package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;
import java.util.Map;

/**
 * @author huaishuo
 * @date 2020/3/12 下午8:17
 **/
@RestController
@RequestMapping("app")
public class AppMyOrderController extends AppBaseController {
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("countMyOrder")
    public Object countMyOrder(Integer statusCode, @RequestParam String token) {
        return verifyDoing(token, appToken -> {
            Long cnt = shopOrderService.countByRiderAndStatus(OrderStatusEnum.valueOf(statusCode),
                appToken.getId());
            return success(cnt, null);
        });
    }

    @GetMapping("myOrder")
    public Object myOrder(Integer statusCode, @RequestParam Integer page, @RequestParam String token) {
        return verifyDoing(token, appToken -> {
            Map<Long, List<ShopOrder>> ret = shopOrderService.findByStatusAndRider(OrderStatusEnum.valueOf(statusCode),
                appToken.getId(), page);
            return success(ret, null);
        });
    }

    @GetMapping("releaseOrder")
    public Object release(@RequestParam String token, @RequestParam Long batchId) {
        return verifyDoing(token, appToken -> {
            List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
            for (ShopOrder order : orders) {
                if (order == null || !order.getSchoolId().equals(appToken.getSchoolId())) {
                    return fail("订单不存在!");
                }
                if (!order.getBindRider().equals(appToken.getId())) {
                    return fail("只能释放自己抢到的订单!");
                }
                if (!order.getStatus().equals(OrderStatusEnum.DELIVERY.getCode())) {
                    return fail("订单状态错误!" + OrderStatusEnum.valueOf(order.getStatus()).getName());
                }
                order.setBindRider(null);
                order.setStatus(OrderStatusEnum.PROCESSING.getCode());
                shopOrderService.update(order);
                System.out.println(appToken.getUserName() + " released order " + order.getId());
            }
            return success();
        });
    }

    @GetMapping("handOver")
    public Object handOver(@RequestParam String token, @RequestParam Long batchId, @RequestParam Long rider) {
        return verifyDoing(token, appToken -> {
            List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
            for (ShopOrder order : orders) {
                if (order == null || !order.getSchoolId().equals(appToken.getSchoolId())) {
                    return fail("订单不存在!");
                }
                if (!order.getBindRider().equals(appToken.getId())) {
                    return fail("只能转交自己抢到的订单!");
                }
                if (!order.getStatus().equals(OrderStatusEnum.DELIVERY.getCode())) {
                    return fail("订单状态错误!" + OrderStatusEnum.valueOf(order.getStatus()).getName());
                }
                order.setBindRider(rider);
                shopOrderService.update(order);
                System.out.println(appToken.getUserName() + " hand over order " + order.getId() + " to " + rider);
            }
            return success();
        });
    }
}
