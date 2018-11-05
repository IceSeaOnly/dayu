package site.binghai.biz.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.biz.def.WxTplMessageHandler;
import site.binghai.biz.entity.jdy.WxTplMsg;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.utils.BaseBean;

@Component
public class TurnGameTicketAddListener extends BaseBean implements WxTplMessageHandler {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private TicketService ticketService;

    @Override
    public String focusOnTplId() {
        return iceConfig.getPaySuccessTplId();
    }

    @Override
    public void accept(WxTplMsg message) {
        //ticketService.newTicket(message.getOpenId(), null);
    }
}
