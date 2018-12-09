package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.windWheel.DeliveryOrder;
import site.binghai.biz.entity.windWheel.ExpressOwner;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author huaishuo
 * @date 2018/12/3 下午11:03
 **/
@Service
public class DeliveryOrderService extends BaseService<DeliveryOrder> implements UnifiedOrderMethods<DeliveryOrder> {
    @Autowired
    private ExpressOwnerService expressOwnerService;
    @Autowired
    private WxTplMessageService wxTplMessageService;
    @Autowired
    private IceConfig iceConfig;

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

        owners.forEach(one -> {
            JSONObject msg = new TplGenerator(
                iceConfig.getAppointmentOrderTplId(),
                iceConfig.getAppRoot() + "/user/view/page/UnFinishedDeliveryList?eid=" + deliveryOrder.getExpressId(),
                one.getOpenId()
            ).put("first", one.getUserName() + "，你好!")
                .put("Content1", deliveryOrder.getExpressBrand() + "有新的代寄订单，请尽快处理！")
                .put("Good", order.getTitle())
                .put("expDate", TimeTools.now())
                .put("name", one.getUserName())
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

    public List<DeliveryOrder> findByIdBrandIdAndStatus(Long eid, Integer status) {
        DeliveryOrder exp = new DeliveryOrder();
        exp.setExpressId(eid);
        exp.setStatus(status);
        return query(exp);
    }
}
