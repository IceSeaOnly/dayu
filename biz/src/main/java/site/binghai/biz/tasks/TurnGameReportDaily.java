package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONArray;
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

import java.util.List;

@Component
@EnableScheduling
public class TurnGameReportDaily extends BaseBean implements ManualInvoke {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private WxTplMessageService wxTplMessageService;

    @Scheduled(cron = "0 0 16 * * ? ")
    public void report() {
        List<Ticket> winners = ticketService.listTodayWinners(now());
        String conf = diamondService.get(DiamondKey.TURN_GAME_DAILY_REPORT_CONF);
        JSONObject cfg = JSONObject.parseObject(conf);

        JSONArray receivers = cfg.getJSONArray("receivers");
        for (int i = 0; i < receivers.size(); i++) {
            String receiver = receivers.getString(i);
            TplGenerator generator = new TplGenerator(cfg.getString("tpl"), cfg.getString("baseUrl") + now(), receiver);
            generator.put("first", "统计服务 - 今日报表")
                .put("keyword1", "今日大转盘中奖列表")
                .put("keyword2", TimeTools.now())
                .put("keyword3", winners.size() + "条记录")
                .put("remark", String.format("今日大转盘中奖共%d人，详细信息点击查看", winners.size()));

            wxTplMessageService.send(generator.build());
        }
    }

    @Override
    public Object invoke() {
        report();
        return null;
    }
}
