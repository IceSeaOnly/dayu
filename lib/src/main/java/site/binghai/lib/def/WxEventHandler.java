package site.binghai.lib.def;

import site.binghai.lib.entity.UnifiedOrder;

/**
 * @date 2018/12/9 下午6:28
 **/
public interface WxEventHandler {
    void onPaid(UnifiedOrder order);

    void onCanceled(UnifiedOrder order);

    void onTuanCreate(Long tuanId, String goodsName, String openId, Integer price, Integer ptSize);

    void onTuanFull(Long tuanId, String goodsName, Integer price, String openId);

    void onTuanJoin(Long tuanId, String goodsName, Integer shouldPay, String openId);

    void onTuanFail(Long tId, String title, Integer price,String openId);

    void onOrderProcessing(String openId, String userName, String text);

    void onOrderDelivering(String openId, String text, String riderName, String riderPhone);

    void onOrderDeliveried(String openId, String text, String riderName, String riderPhone);

    void onTimeoutCanceled(UnifiedOrder unifiedOrder);
}
