package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ShopOrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author icesea
 * @date 2020/2/29 上午1:13
 **/
@RequestMapping("manage")
@Controller
public class OrderManageController extends BaseController {
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("orderManage")
    public String orderManage(String date, String status, ModelMap map) {
        OrderStatusEnum[] ss = hasEmptyString(status) ? OrderStatusEnum.values() : read(status);
        Long ts = hasEmptyString(date) ? TimeTools.today()[0] : TimeTools.dataTime2Timestamp(date, "MM/dd/yyyy");
        Map<Long, List<ShopOrder>> orderList = shopOrderService.findByStatusAndTimeGroupingByBatchId(ts, ts + 86400000L,
            ss);
        Map<Long, Integer> totalPrices = new HashMap<>();
        orderList.forEach((k, v) -> {
            Integer t = v.stream().map(p -> p.getTotalPrice()).reduce(Integer::sum).get();
            totalPrices.put(k, t);
        });
        map.put("date", date);
        map.put("totalPrices", totalPrices);
        map.put("orders", orderList);
        map.put("statusMap", getStatusMap());
        map.put("currentStatus", status == null ? "" : status);
        return "manage/orderManage";
    }

    @GetMapping("orderDetail")
    public String orderDetail(@RequestParam Long batchId, ModelMap map) {
        List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
        Integer total = orders.stream().map(o -> o.getTotalPrice()).reduce(Integer::sum).get();
        map.put("orders", orders);
        map.put("total", total);
        map.put("batchId", batchId);
        return "manage/orderDetail";
    }

    @ResponseBody
    @GetMapping("markOrder")
    public Object markOrder(@RequestParam Long batchId, @RequestParam Integer status) {
        List<ShopOrder> shopOrders = shopOrderService.findByBatchId(batchId);
        for (ShopOrder shopOrder : shopOrders) {
            shopOrder.setStatus(OrderStatusEnum.valueOf(status).getCode());
            shopOrderService.update(shopOrder);
            UnifiedOrder unifiedOrder = unifiedOrderService.findById(shopOrder.getUnifiedId());
            unifiedOrder.setStatus(OrderStatusEnum.valueOf(status).getCode());
            unifiedOrderService.update(unifiedOrder);
        }
        return success();
    }

    private Map<Integer, OrderStatusEnum> getStatusMap() {
        Map<Integer, OrderStatusEnum> map = new HashMap<>();
        for (OrderStatusEnum value : OrderStatusEnum.values()) {
            map.put(value.getCode(), value);
        }
        return map;
    }

    private OrderStatusEnum[] read(String status) {
        String[] names = status.split(",");
        OrderStatusEnum[] ret = new OrderStatusEnum[names.length];
        for (int i = 0; i < names.length; i++) {
            ret[i] = OrderStatusEnum.valueOf(names[i]);
        }
        return ret;
    }
}
