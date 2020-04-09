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
import site.binghai.shop.enums.SalaryScene;
import site.binghai.shop.kv.AppConfig;
import site.binghai.shop.service.KvService;
import site.binghai.shop.service.SalaryLogService;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;
import java.util.Map;

/**
 * @author icesea
 * @date 2020/3/10 下午11:02
 **/
@RestController
@RequestMapping("app")
public class AppDeliveryOrderController extends AppBaseController {
    @Autowired
    private WxEventHandler wxEventHandler;
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private SalaryLogService salaryLogService;
    @Autowired
    private KvService kvService;
    @Autowired
    private WxUserService wxUserService;


    @GetMapping("myDeliveryList")
    public Object myDeliveryList(@RequestParam String token, @RequestParam Integer page) {
        return verifyDoing(token, appToken -> {
            Map<Long, List<ShopOrder>> orderMap = shopOrderService.findByStatusAndRider(OrderStatusEnum.DELIVERY,
                appToken.getId(), page);
            return success(orderMap, null);
        });
    }

    @GetMapping("deliveryOrder")
    public Object deliveryOrder(@RequestParam String token, @RequestParam Long batchId) {
        return verifyDoing(token, appToken -> {
            List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
            StringBuilder sb = new StringBuilder();
            Long userId = null;
            for (ShopOrder order : orders) {
                if (order == null || !order.getSchoolId().equals(appToken.getSchoolId())) {
                    return fail("订单不存在!");
                }
                if (!order.getBindRider().equals(appToken.getId())) {
                    order.setBindRider(appToken.getId());
//                    return fail("只能处理自己抢到的订单!");
                }
                if (!order.getStatus().equals(OrderStatusEnum.DELIVERY.getCode())) {
                    return fail("订单状态错误!" + OrderStatusEnum.valueOf(order.getStatus()).getName());
                }
                order.setBindRider(appToken.getId());
                order.setStatus(OrderStatusEnum.COMPLETE.getCode());
                shopOrderService.update(order);
                System.out.println(appToken.getUserName() + " mark delivery complete : order " + order.getId());
                sb.append("," + order.getTitle());
                userId = order.getUserId();
            }
            wxEventHandler.onOrderDeliveried(wxUserService.findById(userId).getOpenId(), sb.toString().substring(1), appToken.getRemark(), appToken.getUserName());
            AppConfig appConfig = kvService.get(AppConfig.class);
            salaryLogService.book(appToken.getId(), batchId, SalaryScene.DELIVERY, appConfig.getDeliverySalary());
            return success();
        });
    }
}
