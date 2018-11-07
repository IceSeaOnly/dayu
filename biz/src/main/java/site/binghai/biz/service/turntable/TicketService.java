package site.binghai.biz.service.turntable;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.turntable.Ticket;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.TimeTools;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService extends BaseService<Ticket> {
    public List<Ticket> listWinners() {
        Ticket exp = new Ticket();
        exp.setWin(true);
        List<Ticket> ret = query(exp);
        if (isEmptyList(ret)) {
            return ret;
        }

        //ret.forEach(v -> {
        //    v.setRelationNo(null);
        //    v.setUserPhone(null);
        //    v.setOpenId(null);
        //    v.setRelationNo(null);
        //});

        return ret;
    }

    public Ticket play(WxUser user) {
        Ticket exp = new Ticket();
        exp.setOpenId(user.getOpenId());
        exp.setPlayed(false);
        List<Ticket> ret = query(exp);
        if (isEmptyList(ret)) {
            return null;
        }

        return ret.get(0);
    }

    @Transactional
    public void newTicket(String openId, String txId) {
        Ticket exp = new Ticket();
        exp.setRelationNo(txId);
        if (!isEmptyList(query(exp))) {
            return;
        }

        exp.setOpenId(openId);
        exp.setWin(false);
        exp.setPlayed(false);
        save(exp);
    }

    public List<Ticket> listTodayWinners(long now) {
        Long[] today = TimeTools.today();

        List<Ticket> winners = listWinners();
        winners = winners.stream().filter(v -> today[0] <= v.getGameTime() && v.getGameTime() <= now)
            .collect(Collectors.toList());
        return winners;
    }

    @Transactional
    public boolean cancel(String openId, String number) {
        Ticket exp = new Ticket();
        exp.setOpenId(openId);
        exp.setRelationNo(number);

        List<Ticket> list = query(exp);
        boolean changed = false;
        for (Ticket t : list) {
            changed = t.getWin() ? true : changed;
            t.setPlayed(Boolean.TRUE);
            t.setWin(Boolean.FALSE);
            t.setPrize("因订单取消资格&奖品作废");
            update(t);
        }
        return changed;
    }

    public List<Ticket> myTickets(WxUser user, boolean onlyValid) {
        Ticket exp = new Ticket();
        exp.setOpenId(user.getOpenId());
        if (onlyValid) {
            exp.setPlayed(Boolean.FALSE);
        }
        return query(exp);
    }
}
