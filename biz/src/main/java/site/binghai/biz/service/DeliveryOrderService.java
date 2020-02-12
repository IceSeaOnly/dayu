package site.binghai.biz.service;

import com.alibaba.acm.shaded.com.google.common.collect.Lists;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.entity.windWheel.DeliveryOrder;
import site.binghai.biz.entity.windWheel.ExpressOwner;
import site.binghai.biz.utils.NumberUtil;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @date 2018/12/3 下午11:03
 **/
//@Service
public class DeliveryOrderService extends BaseService<DeliveryOrder> implements UnifiedOrderMethods<DeliveryOrder> {
    @Autowired
    private ExpressOwnerService expressOwnerService;
    @Autowired
    private WxTplMessageService wxTplMessageService;
    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private DiamondService diamondService;

    public DeliveryOrder getLastOrder(WxUser user) {
        DeliveryOrder exp = new DeliveryOrder();
        exp.setUserId(user.getId());
        exp.setPaid(Boolean.TRUE);
        List<DeliveryOrder> ret = sortQuery(exp, "id", true);
        return isEmptyList(ret) ? null : ret.get(0);
    }

    @Override
    public JSONObject moreInfo(UnifiedOrder order) {
        DeliveryOrder deliveryOrder = loadByUnifiedOrder(order);
        JSONObject ret = newJSONObject();
        ret.put("寄件姓名", deliveryOrder.getUserName());
        ret.put("手机号码", deliveryOrder.getUserPhone());
        ret.put("取件地址", deliveryOrder.getUserAddress());
        ret.put("快递名称", deliveryOrder.getExpressBrand());
        ret.put("快递电话", deliveryOrder.getExpressPhone());
        ret.put("客户备注", deliveryOrder.getRemark());
        ret.put("寄出日期", deliveryOrder.getExpressOutDate());
        return ret;
    }

    @Override
    public DeliveryOrder loadByUnifiedOrder(UnifiedOrder order) {
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setUnifiedId(order.getId());
        return queryOne(deliveryOrder);
    }

    @Transactional
    @Override
    public DeliveryOrder cancel(UnifiedOrder order) {
        DeliveryOrder deliveryOrder = loadByUnifiedOrder(order);
        deliveryOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        return update(deliveryOrder);
    }

    @Transactional
    @Override
    public void onPaid(UnifiedOrder order) {
        DeliveryOrder deliveryOrder = loadByUnifiedOrder(order);
        deliveryOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        deliveryOrder.setPaid(Boolean.TRUE);
        update(deliveryOrder);
        postPaidEvent(order);
    }

    private void postPaidEvent(UnifiedOrder order) {
        DeliveryOrder deliveryOrder = loadByUnifiedOrder(order);
        List<ExpressOwner> owners = expressOwnerService.findByBrandId(deliveryOrder.getExpressId());

        String notice = hasEmptyString(deliveryOrder.getExpressOutDate()) ? "今天寄出，请尽快处理！"
            : "预约寄出时间为" + deliveryOrder.getExpressOutDate();
        owners.forEach(one -> {
            JSONObject msg = new TplGenerator(
                iceConfig.getAppointmentOrderTplId(),
                iceConfig.getAppRoot() + "/user/view/page/AllDeliveryList?eid=" + deliveryOrder.getExpressId(),
                one.getOpenId()
            ).put("first", one.getUserName() + "，你好!")
                .put("Content1", deliveryOrder.getExpressBrand() + "有新的代寄订单," + notice)
                .put("Good", order.getTitle())
                .put("expDate", TimeTools.now())
                .put("name", one.getUserName())
                .put("menu", order.getShouldPay() / 100.0 + "元")
                .put("remark", "订单详情请点击本消息查看!")
                .build();
            wxTplMessageService.send(msg);
        });

        JSONObject muteList = diamondService.getConf(DiamondKey.DELIVERY_NOTICE_MUTE_LIST);
        List<WxUser> users = wxUserService.findAllDeliverySuperUser();
        users.stream()
            .filter(v -> !muteList.containsKey(v.getOpenId()))
            .forEach(u -> {
                JSONObject msg = new TplGenerator(
                    iceConfig.getAppointmentOrderTplId(),
                    iceConfig.getAppRoot() + "/user/view/page/AllDeliveryList?eid=" + deliveryOrder
                        .getExpressId(),
                    u.getOpenId()
                ).put("first", u.getUserName() + "，你好!")
                    .put("Content1", deliveryOrder.getExpressBrand() + "有新的代寄订单," + notice)
                    .put("Good", order.getTitle())
                    .put("expDate", TimeTools.now())
                    .put("name", u.getUserName())
                    .put("menu", order.getShouldPay() / 100.0 + "元")
                    .put("remark", "订单详情请点击本消息查看!")
                    .build();
                wxTplMessageService.send(msg);
            });

    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXPRESS_DELIVERY;
    }

    public List<DeliveryOrder> findByIdBrandIdAndStatusAndBookDate(Long eid, Integer status, String bookDate) {
        Map<Long, DeliveryOrder> orderMap = new HashMap<>();
        DeliveryOrder exp = new DeliveryOrder();
        exp.setExpressId(eid);
        exp.setStatus(status);
        exp.setExpressOutDate(bookDate);
        List<DeliveryOrder> orders = query(exp);
        if (!isEmptyList(orders)) {
            orders.forEach(v -> orderMap.put(v.getId(), v));
        }
        // 取未完成订单时追加历史未完成订单
        if (OrderStatusEnum.valueOf(status) == OrderStatusEnum.PAIED) {
            exp.setExpressOutDate(null);
            List<DeliveryOrder> uns = query(exp);
            if (isEmptyList(uns)) {
                return Lists.newArrayList(orderMap.values());
            }
            uns.stream()
                .filter(v -> isNotFinished(v))
                .forEach(v -> orderMap.put(v.getId(), v));
        }
        return Lists.newArrayList(orderMap.values());
    }

    private boolean isNotFinished(DeliveryOrder order) {
        if (hasEmptyString(order.getExpressOutDate())) {
            return true;
        }
        if (TimeTools.data2Timestamp(order.getExpressOutDate()) <= now()) {
            return true;
        }
        return false;
    }

    public List<DeliveryOrder> findByIdBrandIdAndStatus(Long eid, Integer status) {
        return findByIdBrandIdAndStatusAndBookDate(eid, status, null);
    }

    public List<DeliveryOrder> search(String content) {
        Map<Long, DeliveryOrder> ret = new HashMap<>();
        if (NumberUtils.isDigits(content)) {
            DeliveryOrder order = findById(Long.parseLong(content));
            if (order != null) {
                ret.put(order.getId(), order);
            }
            //1
            DeliveryOrder exp = new DeliveryOrder();
            exp.setUserPhone(content);
            query(exp).forEach(v -> ret.put(v.getId(), v));
            //2
            exp.setUserPhone(null);
            exp.setExpressPhone(content);
            query(exp).forEach(v -> ret.put(v.getId(), v));
        } else {
            //1
            DeliveryOrder exp = new DeliveryOrder();
            exp.setUserName(content);
            query(exp).forEach(v -> ret.put(v.getId(), v));
        }
        return new ArrayList<>(ret.values());
    }
}
