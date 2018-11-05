package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.def.WxTplMessageHandler;
import site.binghai.biz.entity.jdy.WxTplMsg;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.biz.utils.NumberUtil;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.TplGenerator;

@Component
public class TurnGameTicketAddListener extends BaseBean implements WxTplMessageHandler {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private WxTplMessageService wxTplMessageService;
    @Autowired
    private DiamondService diamondService;

    @Override
    public String focusOnTplId() {
        return iceConfig.getPaySuccessTplId();
    }

    @Override
    public void accept(WxTplMsg message) {
        String url = message.getUrl();
        ticketService.newTicket(message.getOpenId(), NumberUtil.getNumber(url.split("\\?")[1]));
        // 活动邀约
        JSONObject cfg = JSONObject.parseObject(diamondService.get(DiamondKey.TURN_GAME_INVATION_TPL));
        TplGenerator generator = new TplGenerator(iceConfig.getActivityInvitationTplId(), cfg.getString("url"), message.getOpenId());
        generator.put("first",cfg.getString("title"));
        generator.put("keyword1",cfg.getString("activityName"));
        generator.put("keyword2",cfg.getString("auditStatus"));
        generator.put("remark",cfg.getString("remark"),"#FF0000");
        wxTplMessageService.send(generator.build());
    }
}
