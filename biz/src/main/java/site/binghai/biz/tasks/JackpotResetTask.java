package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.def.ManualInvoke;
import site.binghai.biz.entity.turntable.Jackpot;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.biz.service.turntable.JackpotService;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import java.util.List;

@Component
@EnableScheduling
public class JackpotResetTask extends BaseBean implements ManualInvoke {
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private WxTplMessageService wxTplMessageService;

    @Scheduled(cron = "59 59 23 * * ?")
    @Override
    public Object invoke() {

        List<Jackpot> all = jackpotService.findAll(9999);

        for (Jackpot jackpot : all) {
            jackpotService.delete(jackpot.getId());
        }

        String list = diamondService.get(DiamondKey.RESET_JACKPOT_LIST);
        List<Jackpot> arr = JSONArray.parseArray(list, Jackpot.class);
        jackpotService.batchSave(all);


        String conf = diamondService.get(DiamondKey.TURN_GAME_DAILY_REPORT_CONF);
        JSONObject cfg = JSONObject.parseObject(conf);
        JSONArray receivers = cfg.getJSONArray("receivers");
        for (int i = 0; i < receivers.size(); i++) {
            TplGenerator generator = new TplGenerator(cfg.getString("tpl"), cfg.getString("baseUrl") + now(),
                receivers.getString(i));
            generator.put("first", "奖池重置提醒")
                .put("keyword1", "今日大转盘奖池重置提醒")
                .put("keyword2", TimeTools.now())
                .put("keyword3", arr.size() + "条记录已重置");

            StringBuilder sb = new StringBuilder();
            for (Jackpot v : arr) {
                sb.append(String.format("%s 备奖 %d个，空包 %d; ", v.getName(), v.getRemains(), v.getFakeRemains()));
            }
            generator.put("remark", sb.toString(), "#FF0000");

            wxTplMessageService.send(generator.build());
        }


        logger.info("jackpot reset as {}", list);
        return arr;
    }
}
