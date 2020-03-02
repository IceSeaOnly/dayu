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
 * @author huaishuo
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
        List<ShopOrder> orderList = shopOrderService.findByStatusAndTime(ts, ts + 86400000L, ss);
        map.put("date", date);
        map.put("orders", orderList);
        map.put("statusMap", getStatusMap());
        map.put("currentStatus", status == null ? "" : status);
        return "manage/orderManage";
    }

    @ResponseBody
    @GetMapping("markOrder")
    public Object markOrder(@RequestParam Long oid, @RequestParam Integer status) {
        ShopOrder shopOrder = shopOrderService.findById(oid);
        shopOrder.setStatus(OrderStatusEnum.valueOf(status).getCode());
        shopOrderService.update(shopOrder);
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(shopOrder.getUnifiedId());
        unifiedOrder.setStatus(OrderStatusEnum.valueOf(status).getCode());
        unifiedOrderService.update(unifiedOrder);
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
