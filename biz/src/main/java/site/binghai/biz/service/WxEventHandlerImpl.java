package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

/**
 *
 * @date 2018/12/9 下午6:28
 **/
@Component
public class WxEventHandlerImpl extends BaseBean implements WxEventHandler {
    @Autowired
    private WxTplMessageService wxTplMessageService;
    @Autowired
    private IceConfig iceConfig;

    @Override
    public void onPaid(UnifiedOrder order) {
        JSONObject msg = new TplGenerator(
            iceConfig.getPaySuccessTplId(),
            iceConfig.getAppRoot() + "/user/view/page/OrderDetailPage?uid=" + order.getId(),
            order.getOpenId()
        ).put("first", order.getUserName() + "，您好！我们已经收到您的" + order.getTitle() + ",服务人员将尽快联系您！感谢您的使用，祝您生活愉快！")
            .put("keyword1", order.getUserName())
            .put("keyword2", TimeTools.format2yyyyMMdd(now()) + order.getOrderId())
            .put("keyword3", order.getShouldPay() / 100.0 + "元")
            .put("keyword4", order.getTitle())
            .put("Remark", "订单详情可点击本消息查看")
            .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onCanceled(UnifiedOrder order) {
        JSONObject msg = new TplGenerator(
            iceConfig.getPaySuccessTplId(),
            iceConfig.getAppRoot() + "/user/view/page/OrderDetailPage?uid=" + order.getId(),
            order.getOpenId()
        ).put("first", order.getUserName() + "您好！您的" + order.getTitle() + "已取消，如有使用积分优惠券，将于稍后返还，感谢您的使用，祝您生活愉快！")
            .put("keyword1", TimeTools.format2yyyyMMdd(now()) + order.getOrderId())
            .put("keyword2", order.getTitle())
            .put("Remark", "订单详情可点击本消息查看")
            .build();

        wxTplMessageService.send(msg);
    }
}
