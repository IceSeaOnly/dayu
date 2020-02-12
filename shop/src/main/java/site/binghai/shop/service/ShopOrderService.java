package site.binghai.shop.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.ShopOrder;

import javax.transaction.Transactional;
import java.util.List;

/**
 *
 * @date 2020/2/2 下午12:47
 **/
@Service
public class ShopOrderService extends BaseService<ShopOrder> implements UnifiedOrderMethods<ShopOrder> {
    @Override
    public JSONObject moreInfo(UnifiedOrder order) {
        ShopOrder shopOrder = loadByUnifiedOrder(order);
        JSONObject ret = newJSONObject();
        ret.put("商品款式", shopOrder.getStandardInfo());
        ret.put("收货人", shopOrder.getReceiver());
        ret.put("收货地址", shopOrder.getReceiverAddress());
        ret.put("收货人手机", shopOrder.getReceiverPhone());
        ret.put("备注信息", shopOrder.getRemark());
        return ret;
    }

    private ShopOrder findByUnifiedId(Long unifiedId) {
        ShopOrder exp = new ShopOrder();
        exp.setUnifiedId(unifiedId);
        return queryOne(exp);
    }

    @Override
    public ShopOrder loadByUnifiedOrder(UnifiedOrder order) {
        return findByUnifiedId(order.getId());
    }

    @Override
    @Transactional
    public ShopOrder cancel(UnifiedOrder order) {
        ShopOrder shopOrder = loadByUnifiedOrder(order);
        shopOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        update(shopOrder);
        return shopOrder;
    }

    @Override
    @Transactional
    public void onPaid(UnifiedOrder order) {
        ShopOrder shopOrder = loadByUnifiedOrder(order);
        shopOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        update(shopOrder);
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.SCHOOL_SHOP;
    }

    public long countByUserIdAndState(Long userId, OrderStatusEnum status) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        exp.setStatus(status.getCode());
        return count(exp);
    }

    public long countByUserId(Long userId) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        return count(exp);
    }

    public List<ShopOrder> findByUserId(Long userId) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        return sortQuery(exp, "id", true);
    }

    public List<ShopOrder> findByUserIdAndState(Long userId, OrderStatusEnum status) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        exp.setStatus(status.getCode());
        return query(exp);
    }

    @Override
    public String buildPayCallbackUrl(UnifiedOrder unifiedOrder) {
        return "/shop/orders";
    }
}
