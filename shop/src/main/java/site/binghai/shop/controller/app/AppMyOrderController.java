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

/**
 * @author huaishuo
 * @date 2020/3/12 下午8:17
 **/
@RestController
@RequestMapping("app")
public class AppMyOrderController extends AppBaseController {
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("myOrder")
    public Object myOrder(@RequestParam Integer statusCode, @RequestParam Integer page, @RequestParam String token) {
        return verifyDoing(token, appToken -> {
            List<ShopOrder> ret = shopOrderService.findByStatusAndRider(OrderStatusEnum.valueOf(statusCode),
                appToken.getId(), page);
            return success(ret, null);
        });
    }
}
