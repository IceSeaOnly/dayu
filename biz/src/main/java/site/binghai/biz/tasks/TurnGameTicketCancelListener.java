package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.biz.def.WxTplMessageHandler;
import site.binghai.biz.entity.jdy.WxTplMsg;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.biz.utils.NumberUtil;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.utils.BaseBean;

@Component
public class TurnGameTicketCancelListener extends BaseBean implements WxTplMessageHandler {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TurnGameReportDaily turnGameReportDaily;

    @Override
    public String focusOnTplId() {
        return iceConfig.getOrderCancelTplId();
    }

    @Override
    public void accept(WxTplMsg message) {
        JSONObject obj = JSONObject.parseObject(message.getText());
        String desc = obj.getJSONObject("first").getString("value");
        ticketService.cancel(message.getOpenId(), NumberUtil.getNumber(desc));
        turnGameReportDaily.report("因用户订单取消，中奖名单产生变动，请以此次通知为准。");
    }
}
