package site.binghai.biz.controller.third;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.third.ThirdOpen;
import site.binghai.biz.entity.third.ThirdOpenOrder;
import site.binghai.biz.service.third.ThirdOpenOrderSerivice;
import site.binghai.biz.service.third.ThirdOpenSerivice;
import site.binghai.lib.controller.AbstractPayBizController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.UnifiedOrderService;

import java.util.Map;

/**
 * @author huaishuo
 * @date 2018/12/16 上午10:58
 **/
@RestController
@RequestMapping("/user/third/service/")
public class ThirdOpenController extends AbstractPayBizController<ThirdOpenOrder> {
    @Autowired
    private ThirdOpenSerivice thirdOpenSerivice;
    @Autowired
    private ThirdOpenOrderSerivice thirdOpenOrderSerivice;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("listService")
    public Object listService() {
        return success(thirdOpenSerivice.findAll(999), null);
    }

    @PostMapping("booking")
    public Object booking(@RequestBody Map map) {
        WxUser user = getUser();
        ThirdOpenOrder order = thirdOpenOrderSerivice.newInstance(map);
        if (hasEmptyString(order.getServiceId())) {
            return fail("选择的服务有误");
        }
        ThirdOpen thirdOpen = thirdOpenSerivice.findById(order.getServiceId());

        if (!thirdOpen.getOnline()) {
            return fail("服务暂时下线");
        }

        if (thirdOpen.getName().equals("WXTPL")) {
            return fail("仅供API调用");
        }

        order.setServiceName(thirdOpen.getName());
        order.setServiceUrl(thirdOpen.getUrl());
        order.setUserName(user.getUserName());
        order.setUserOpenId(user.getOpenId());
        order.setOwnerOpenId(thirdOpen.getOwnerOpenId());

        try {
            return create(order, thirdOpen.getPrice());
        } catch (Exception e) {
            logger.error("booking ThirdOpenSerivice failed! request:{}", map, e);
        }
        return fail("创单失败!系统异常!");
    }

    @GetMapping("query")
    public Object query(@RequestParam Long orderId) {
        ThirdOpenOrder openOrder = thirdOpenOrderSerivice.findById(orderId);

        WxUser user = getUser();
        if (!user.getOpenId().equals(openOrder.getOwnerOpenId()) && !user.getOpenId().equals(
            openOrder.getUserOpenId())) {
            return fail("非法访问");
        }

        return success(openOrder, null);
    }

    /**
     * 标记订单完成
     */
    @GetMapping("done")
    public Object done(@RequestParam Long orderId) {
        ThirdOpenOrder openOrder = thirdOpenOrderSerivice.findById(orderId);

        WxUser user = getUser();
        if (!user.getOpenId().equals(openOrder.getOwnerOpenId())) {
            return fail("拒绝操作");
        }

        UnifiedOrder unifiedOrder = unifiedOrderService.findById(openOrder.getUnifiedId());
        unifiedOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        unifiedOrderService.update(unifiedOrder);

        openOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        thirdOpenOrderSerivice.update(openOrder);
        return success(openOrder, null);
    }
}
