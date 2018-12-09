package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.windWheel.DeliveryOrder;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author huaishuo
 * @date 2018/12/3 下午11:03
 **/
@Service
public class DeliveryOrderService extends BaseService<DeliveryOrder> implements UnifiedOrderMethods<DeliveryOrder> {

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
        ret.put("寄件姓名",deliveryOrder.getUserName());
        ret.put("手机号码",deliveryOrder.getUserPhone());
        ret.put("取件地址",deliveryOrder.getUserAddress());
        ret.put("快递名称",deliveryOrder.getExpressBrand());
        ret.put("快递电话",deliveryOrder.getExpressPhone());
        ret.put("客户备注",deliveryOrder.getRemark());
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
        update(deliveryOrder);
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
