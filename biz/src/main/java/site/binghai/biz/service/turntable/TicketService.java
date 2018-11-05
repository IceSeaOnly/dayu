package site.binghai.biz.service.turntable;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.turntable.Ticket;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TicketService extends BaseService<Ticket> {
    public List<Ticket> listWinners() {
        Ticket exp = new Ticket();
        exp.setWin(true);
        List<Ticket> ret = query(exp);
        if (isEmptyList(ret)) {
            return ret;
        }

        ret.forEach(v -> {
            v.setRelationNo(null);
            v.setUserPhone(null);
            v.setOpenId(null);
            v.setRelationNo(null);
        });

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
}
