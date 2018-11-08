package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.def.ManualInvoke;
import site.binghai.biz.entity.turntable.Ticket;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class WinnerListPublishTask extends BaseBean implements ManualInvoke {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private WxTplMessageService wxTplMessageService;

    @Scheduled(cron = "0 0 16 * * ?")
    @Override
    public Object invoke() {
        JSONObject cfg = diamondService.getConf(DiamondKey.TURN_GAME_WINLIST_PUBLISH_TPL);
        List<Ticket> today = ticketService.listTodayTickets();
        Set<String> openids = new HashSet<>();
        today.forEach(v -> openids.add(v.getOpenId()));
        openids.addAll(cfg.getJSONArray("extraOpenIds").toJavaList(String.class));

        String winnerNames = ticketService.listTodayWinners(now())
            .stream()
            .map(v -> v.getUserName())
            .distinct()
            .collect(Collectors.joining("、"));

        openids.forEach(v -> {
            TplGenerator generator = new TplGenerator(cfg.getString("tpl"), cfg.getString("url"), v);
            generator.put("first", String.format("%s 期大转盘抽奖结果公布", TimeTools.format2yyyyMMdd(now())))
                .put("keyword1", TimeTools.format2yyyyMMdd(now()))
                .put("keyword2", TimeTools.format2yyyyMMdd(now()))
                .put("keyword3", winnerNames)
                .put("remark", cfg.getString("remark"));

            wxTplMessageService.send(generator.build());
            logger.info("WinnerListPublishTask published to :{}", v);
        });

        return today;
    }
}
