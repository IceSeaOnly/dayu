package site.binghai.biz.service.turntable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.turntable.Ticket;
import site.binghai.biz.service.dao.TicketDao;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.TimeTools;

import javax.transaction.Transactional;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService extends BaseService<Ticket> {
    @Autowired
    private TicketDao ticketDao;

    @Override
    protected JpaRepository<Ticket, Long> getDao() {
        return ticketDao;
    }

    public List<Ticket> listWinners() {
        Ticket ticket = new Ticket();
        ticket.setWin(Boolean.TRUE);
        return query(ticket);
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

    public List<Ticket> listTodayWinners(Long now) {
        Long[] today = TimeTools.today();
        if (now == null) {
        } else {
            String date = TimeTools.format(now);
            date = date.split(" ")[0] + " 00:00:00";
            Long cur = TimeTools.data2Timestamp(date);
            today[0] = cur;
            today[1] = cur + 86400000;
        }

        List<Ticket> ret = ticketDao.findByPlayedAndGameTimeBetween(Boolean.TRUE, today[0], today[1]);
        if (isEmptyList(ret)) {
            return emptyList();
        }

        return ret.stream().filter(v -> v.getWin()).collect(Collectors.toList());
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

    public List<Ticket> listTodayTickets() {
        Long[] today = TimeTools.today();
        return ticketDao.findByCreatedBetween(today[0], today[1]);
    }
}
