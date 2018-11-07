package site.binghai.biz.service.turntable;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.turntable.Jackpot;
import site.binghai.lib.service.BaseService;

import java.util.List;
import java.util.Random;

@Service
public class JackpotService extends BaseService<Jackpot> {

    public Jackpot play() {
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

        logger.info("play pos :{}", pos);

        Long retId = null;
        for (Jackpot jackpot : jackpots) {
            int rng = jackpot.getFakeRemains() + jackpot.getRemains();
            pos -= rng;
            if (pos > 0) {
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

        if (total > 0) {
            return null;
        }

        logger.info("play ret : {}", retId);

        Jackpot jackpot = findById(Math.abs(retId));

        if (retId > 0) {
            jackpot.setRemains(jackpot.getRemains() - 1);
        } else {
            jackpot.setFakeRemains(jackpot.getFakeRemains() - 1);
        }
        return update(jackpot);
    }
}
