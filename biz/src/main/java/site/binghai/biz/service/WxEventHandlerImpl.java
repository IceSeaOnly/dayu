package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.utils.TplGenerator;

/**
 * @author huaishuo
 * @date 2018/12/9 下午6:28
 **/
@Component
public class WxEventHandlerImpl implements WxEventHandler {
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
            .put("orderMoneySum", order.getShouldPay() / 100.0 + "元")
            .put("orderProductName", order.getTitle())
            .put("Remark", "订单详情可点击本消息查看")
            .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onCanceled(UnifiedOrder order) {

    }
}
