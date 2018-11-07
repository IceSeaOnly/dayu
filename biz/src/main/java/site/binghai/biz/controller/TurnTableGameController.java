package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.entity.turntable.Jackpot;
import site.binghai.biz.entity.turntable.Ticket;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.service.turntable.JackpotService;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.TimeTools;

import java.util.List;

@RestController
@RequestMapping("/user/turngame/")
public class TurnTableGameController extends BaseController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private DiamondService diamondService;

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

        tickets.sort((a, b) -> b.getId() > a.getId() ? 0 : 1);

        if (raw != null) {
            return success(tickets, null);
        }

        StringBuilder html = new StringBuilder(String.format("<h1>中奖人数:%d</h1>", tickets.size()));
        int counter = 1;
        for (Ticket v : tickets) {
            html.append(String.format("<h1>%d / 姓名：%s</h1>", counter++, v.getUserName()));
            html.append(String.format("<h1>手机号:%s</h1> <br/>", v.getUserPhone()));
            html.append(String.format("<h1>中奖时间:%s </h1><br/>", v.getGameTimeString()));
            html.append(String.format("<h1>奖品:%s </h1><br/>", v.getPrize()));
            html.append(String.format("<h1>关联订单号:%s </h1><br/><br/><br/>", v.getRelationNo()));
        }

        return html.toString();
    }

    @GetMapping("play")
    public Object play() {
        Ticket ticket = ticketService.play(getUser());
        if (ticket == null) {
            return fail(diamondService.get(DiamondKey.TURN_GAME_NO_TICKET_NOTICE));
        }

        ticket.setPlayed(true);
        ticket.setGameTime(now());
        ticket.setGameTimeString(TimeTools.now());
        ticket.setUserAvatar(getUser().getAvatar());
        ticket.setUserName(getUser().getUserName());

        Jackpot ret = jackpotService.play();
        if (ret != null) {
            ticket.setWin(true);
            ticket.setPrize(ret.getName());
        } else {
            ticket.setWin(false);
            ticket.setPrize(null);
        }

        ticketService.update(ticket);
        return success(ret, diamondService.get(DiamondKey.TURN_GAME_MISS_PRIZE));
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
