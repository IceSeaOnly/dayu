package site.binghai.lib.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.MergePayOrder;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;

import javax.transaction.Transactional;

/**
 *
 * @date 2020/2/4 下午10:39
 **/
@Service
public class MergePayService extends BaseService<MergePayOrder> implements UnifiedOrderMethods<MergePayOrder> {
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @Override
    public JSONObject moreInfo(UnifiedOrder order) {
        JSONObject obj = new JSONObject();
        obj.put("合并详情", order.getTitle());
        return obj;
    }

    @Override
    public MergePayOrder loadByUnifiedOrder(UnifiedOrder order) {
        MergePayOrder mergePayOrder = new MergePayOrder();
        mergePayOrder.setUnifiedId(order.getId());
        return queryOne(mergePayOrder);
    }

    @Override
    public MergePayOrder cancel(UnifiedOrder order) {
        return null;
    }

    @Override
    @Transactional
    public void onPaid(UnifiedOrder order) {
        MergePayOrder mergePayOrder = loadByUnifiedOrder(order);
        for (String sid : mergePayOrder.getUnifiedIds().split(",")) {
            UnifiedOrder sub = unifiedOrderService.findById(Long.parseLong(sid));
            sub.setStatus(OrderStatusEnum.PAIED.getCode());
            unifiedOrderService.update(sub);
            payBizServiceFactory.mutePayEvent(sub);
        }
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.MERGE_PAY;
    }

    @Override
    public String buildPayCallbackUrl(UnifiedOrder unifiedOrder) {
        return "/shop/orders";
    }
}
