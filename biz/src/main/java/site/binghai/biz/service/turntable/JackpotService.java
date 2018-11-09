package site.binghai.biz.service.turntable;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.turntable.Jackpot;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

@Service
public class JackpotService extends BaseService<Jackpot> {
    @Transactional
    public Jackpot play() {
        try {
            return play_();
        } catch (Exception e) {
            logger.error("play error !", e);
        }
        return null;
    }

    @Transactional
    public Jackpot play_() {
        List<Jackpot> jackpots = findAll(999);
        if (isEmptyList(jackpots)) {
            return null;
        }

        Integer total = jackpots.stream()
            .mapToInt(v -> v.getRemains() + v.getFakeRemains())
            .sum();

        if (total == 0) {
            return null;
        }

        Random random = new Random();
        int pos = random.nextInt(total) - 1;

        Long retId = null;
        for (Jackpot jackpot : jackpots) {
            int rng = jackpot.getFakeRemains() + jackpot.getRemains();
            pos -= rng;
            if (pos > 0 || rng <= 0) {
                continue;
            }
            Random r = new Random();
            int it = r.nextInt(rng);
            if (it > jackpot.getRemains()) {
                retId = jackpot.getId() * -1L;
            } else {
                retId = jackpot.getId();
            }
            break;
        }

        if (pos >= 0 || retId == null) {
            return null;
        }

        logger.info("play ret : {}", retId);

        Jackpot jackpot = findById(Math.abs(retId));

        if (jackpot.getRemains() <= 0 && retId > 0) {
            retId *= -1;
        }

        if (retId > 0) {
            jackpot.setRemains(jackpot.getRemains() - 1);
        } else {
            jackpot.setFakeRemains(jackpot.getFakeRemains() - 1);
        }
        update(jackpot);

        return retId > 0 ? jackpot : null;
    }
}
