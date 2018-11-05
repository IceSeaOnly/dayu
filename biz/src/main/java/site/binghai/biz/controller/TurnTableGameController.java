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

@RestController
@RequestMapping("/user/turngame/")
public class TurnTableGameController extends BaseController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private DiamondService diamondService;

    @GetMapping("listWinners")
    public Object listWinners() {
        return success(ticketService.listWinners(), null);
    }

    @GetMapping("getGameRule")
    public Object getGameRule() {
        return success(diamondService.get(DiamondKey.TURN_GAME_RULE), null);
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
        return ret == null ? fail(diamondService.get(DiamondKey.TURN_GAME_MISS_PRIZE)) : success(ret, null);
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
