package site.binghai.lib.def;

import site.binghai.lib.entity.UnifiedOrder;

/**
 *
 * @date 2018/12/9 下午6:28
 **/
public interface WxEventHandler {
    void onPaid(UnifiedOrder order);
    void onCanceled(UnifiedOrder order);
}
