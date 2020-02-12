package site.binghai.biz.service.third;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.third.ThirdOpenOrder;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import javax.transaction.Transactional;

/**
 *
 * @date 2018/12/16 上午11:01
 **/
@Service
public class ThirdOpenOrderSerivice extends BaseService<ThirdOpenOrder> implements UnifiedOrderMethods<ThirdOpenOrder> {
    @Autowired
    private WxTplMessageService wxTplMessageService;
    @Autowired
    private IceConfig iceConfig;

    @Override
    public JSONObject moreInfo(UnifiedOrder order) {
        ThirdOpenOrder it = loadByUnifiedOrder(order);
        JSONObject ret = newJSONObject();
        ret.put("服务名称", it.getServiceName());
        ret.put("备注信息", it.getRemark());
        return ret;
    }

    @Override
    public ThirdOpenOrder loadByUnifiedOrder(UnifiedOrder order) {
        ThirdOpenOrder exp = new ThirdOpenOrder();
        exp.setUnifiedId(order.getId());
        return queryOne(exp);
    }

    @Override
    @Transactional
    public ThirdOpenOrder cancel(UnifiedOrder order) {
        ThirdOpenOrder it = loadByUnifiedOrder(order);
        it.setStatus(OrderStatusEnum.CANCELED.getCode());
        return update(it);
    }

    @Override
    @Transactional
    public void onPaid(UnifiedOrder order) {
        ThirdOpenOrder it = loadByUnifiedOrder(order);
        it.setStatus(OrderStatusEnum.PAIED.getCode());
        update(it);

        JSONObject msg = new TplGenerator(
            iceConfig.getAppointmentOrderTplId(),
            iceConfig.getAppRoot() + "/user/view/page/ThirdServiceOrder?orderId=" + it.getId(),
            it.getOwnerOpenId()
        ).put("first", it.getServiceName() + "订单提醒：")
            .put("Content1", "有新的" + it.getServiceName() + "订单，请尽快处理！")
            .put("Good", order.getTitle())
            .put("expDate", TimeTools.now())
            .put("name", it.getUserName())
            .put("menu", order.getShouldPay() / 100.0 + "元")
            .put("remark", it.getRemark())
            .build();
        wxTplMessageService.send(msg);
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.THIRD_OPEN_SERVICE;
    }
}
