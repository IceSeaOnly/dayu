package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.entity.DelayNotice;
import site.binghai.biz.entity.turntable.Jackpot;
import site.binghai.biz.entity.turntable.Ticket;
import site.binghai.biz.enums.NoticeType;
import site.binghai.biz.service.DelayNoticeService;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.biz.service.turntable.JackpotService;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import java.util.List;

//@RestController
//@RequestMapping("/user/turngame/")
public class TurnTableGameController extends BaseController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private DelayNoticeService delayNoticeService;

    @GetMapping("myTickets")
    public Object myTickets() {
        return success(ticketService.myTickets(getUser(), true), null);
    }

    @GetMapping("listWinners")
    public Object listWinners(Long time, String raw) {
        List<Ticket> tickets = emptyList();

        if (time != null) {
            tickets.addAll(ticketService.listTodayWinners(time));
        } else {
            tickets.addAll(ticketService.listWinners());
        }

        tickets.sort((a, b) -> b.getGameTime() > a.getGameTime() ? 0 : 1);

        if (raw != null) {
            return success(tickets, null);
        }

        StringBuilder html = new StringBuilder(String.format("<h1>中奖人数:%d</h1>", tickets.size()));
        int counter = 1;
        for (Ticket v : tickets) {
            html.append(String.format("<h1>%d / 名称：%s</h1>", counter++, v.getUserName()));
            //html.append(String.format("<h1>手机号:%s</h1> <br/>", v.getUserPhone()));
            html.append(String.format("<h1>中奖时间:%s </h1><br/>", v.getGameTimeString()));
            html.append(String.format("<h1>奖品:%s </h1><br/>", v.getPrize()));
            html.append(String.format("<h1>关联订单号:%s </h1><br/><br/><br/>", v.getRelationNo()));
        }

        return html.toString();
    }

    @GetMapping("play")
    public Object play() {
        if (true) {
            return fail("抽奖活动暂停，您已经中得的奖品将照常配送，感谢您的支持!");
        }

        Ticket ticket = ticketService.play(getUser());
        if (ticket == null) {
            return fail(diamondService.get(DiamondKey.TURN_GAME_NO_TICKET_NOTICE));
        }

        ticket.setPlayed(true);
        ticket.setGameTime(now());
        ticket.setGameTimeString(TimeTools.now());
        ticket.setUserAvatar(getUser().getAvatar());
        ticket.setUserName(getUser().getUserName());
        //每人每天仅能中奖1次
        Jackpot ret = userHasWinToday() ? null : jackpotService.play();
        if (ret != null) {
            congratulations(getUser(), ret);
            ticket.setWin(true);
            ticket.setPrize(ret.getName());
        } else {
            ticket.setWin(false);
            ticket.setPrize(null);
        }

        ticketService.update(ticket);
        return success(ret, diamondService.get(DiamondKey.TURN_GAME_MISS_PRIZE));
    }

    private void congratulations(WxUser user, Jackpot ret) {
        String conf = diamondService.get(DiamondKey.TURN_GAME_CONGRATULATION_TPL);
        JSONObject cfg = JSONObject.parseObject(conf);
        TplGenerator generator = new TplGenerator(cfg.getString("tpl"), null, user.getOpenId());
        generator.put("first", ret.getMsg())
            .put("keyword1", ret.getName())
            .put("keyword2", TimeTools.now())
            .put("remark", cfg.getString("remark"));
        delayNoticeService.save(NoticeType.WX_TPL_MESSAGE, now() + 5000, generator.build().toJSONString());
    }

    private boolean userHasWinToday() {
        List<Ticket> today = ticketService.listTodayWinners(now());
        for (Ticket t : today) {
            if (t.getOpenId().equals(getUser().getOpenId())) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("updateUserNameAndPhone")
    public Object updateUserNameAndPhone(@RequestParam Long ticketId,
                                         @RequestParam String userName,
                                         @RequestParam String userPhone) {
        Ticket ticket = ticketService.findById(ticketId);
        if (ticket == null || !ticket.getOpenId().equals(getUser().getOpenId())) {
            return fail("非法参数");
        }
        ticket.setUserName(userName);
        ticket.setUserPhone(userPhone);
        ticketService.update(ticket);
        return success();
    }
}
