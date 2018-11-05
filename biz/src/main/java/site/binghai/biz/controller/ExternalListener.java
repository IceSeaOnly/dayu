package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.MD5;

/**
 * 外部事件监听器
 */
@RequestMapping("/external/")
@RestController
public class ExternalListener extends BaseController {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private TicketService ticketService;

    /**
     * 大转盘游戏监听支付成功消息
     */
    @PostMapping("turnGameListener")
    public Object turnGameListener(@RequestParam String openId,
                                   @RequestParam String txId,
                                   @RequestParam String sign) {
        if (hasEmptyString(openId, txId, sign)) {
            return fail("all parameters is required.");
        }

        if (!MD5.encryption(openId + txId + iceConfig.getWxValidateMD5Key()).equals(sign)) {
            return fail("Illegal signature");
        }

        ticketService.newTicket(openId,txId);
        return success();
    }

}
