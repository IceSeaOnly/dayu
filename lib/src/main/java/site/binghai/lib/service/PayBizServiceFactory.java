package site.binghai.lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.utils.BaseBean;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayBizServiceFactory extends BaseBean {
    private static Map<PayBizEnum, UnifiedOrderMethods> serviceMap;

    @Autowired
    private WxEventHandler wxEventHandler;
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    public UnifiedOrderMethods get(PayBizEnum payBizEnum) {
        return serviceMap.get(payBizEnum);
    }

    public UnifiedOrderMethods get(int code) {
        return serviceMap.get(PayBizEnum.valueOf(code));
    }

    public String buildPayUrl(UnifiedOrder unifiedOrder) {
        return "/user/unified/multiPay?unifiedId=" + unifiedOrder.getId();
    }

    public String buildWxPayUrl(UnifiedOrder unifiedOrder) {
        String url = iceConfig.getWxPayUrl()
            + "?title=" + unifiedOrder.getTitle()
            + "&totalFee=" + unifiedOrder.getShouldPay()
            + "&orderId=" + unifiedOrder.getOrderId();

        return url + buildCallbackUrl(unifiedOrder);
    }

    public String buildCallbackUrl(UnifiedOrder unifiedOrder) {
        String callback = serviceMap.get(PayBizEnum.valueOf(unifiedOrder.getAppCode())).buildPayCallbackUrl(
            unifiedOrder);
        return "&callBack=" + iceConfig.getAppRoot() + (callback == null ? "/user/view/page/OrderDetailPage?uid="
            + unifiedOrder.getId() : callback);
    }

    @Autowired
    public void setAll(List<UnifiedOrderMethods> bizs) {
        serviceMap = new HashMap<>();
        if (isEmptyList(bizs)) {
            return;
        }

        for (UnifiedOrderMethods biz : bizs) {
            serviceMap.put(biz.getBizType(), biz);
            logger.info("PayBizServiceFactory loaded service {} for {},",
                biz.getClass().getSimpleName(), biz.getBizType().getName());
        }
    }

    @Transactional
    public void onPayNotify(String orderId) throws Exception {
        UnifiedOrder unifiedOrder = unifiedOrderService.findByOrderId(orderId);

        if (unifiedOrder == null || unifiedOrder.getStatus() >= OrderStatusEnum.PAIED.getCode()) {
            logger.error("status of order {} is no longer paid!", orderId);
            return;
        }

        unifiedOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        unifiedOrderService.update(unifiedOrder);

        payEvent(unifiedOrder);
    }

    public void payEvent(UnifiedOrder unifiedOrder) {
        get(unifiedOrder.getAppCode()).onPaid(unifiedOrder);
        wxEventHandler.onPaid(unifiedOrder);
    }

    public void cancel(UnifiedOrder unifiedOrder) {
        get(unifiedOrder.getAppCode()).onPaid(unifiedOrder);
        wxEventHandler.onCanceled(unifiedOrder);
    }
}
