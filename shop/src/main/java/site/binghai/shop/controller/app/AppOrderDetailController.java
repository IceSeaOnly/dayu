package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;

/**
 * @author icesea
 * @date 2020/3/12 下午8:15
 **/
@RestController
@RequestMapping("app")
public class AppOrderDetailController extends AppBaseController {
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("orderDetail")
    public Object orderDetail(@RequestParam String token, @RequestParam Long orderId) {
        return verifyDoing(token, appToken -> {
            ShopOrder order = shopOrderService.findById(orderId);
            if (order == null || !order.getSchoolId().equals(appToken.getSchoolId())) {
                return fail("订单不存在!");
            }
            if (!order.getBindRider().equals(appToken.getId())) {
                return fail("只能查看自己抢到的订单!");
            }
            return success(order, null);
        });
    }

    @GetMapping("queryByBatchId")
    public Object queryByBatchId(@RequestParam String token, @RequestParam Long batchId) {
        return verifyDoing(token, appToken -> {
            List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
            if (isEmptyList(orders)) {
                return fail("找不到订单");
            }
            return success(orders, null);
        });
    }
}
