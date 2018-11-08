package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.biz.def.ManualInvoke;
import site.binghai.biz.entity.DelayNotice;
import site.binghai.biz.enums.NoticeType;
import site.binghai.biz.service.DelayNoticeService;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.lib.utils.BaseBean;
import java.util.List;

/**
 * 延时通知
 */
@Component
@EnableScheduling
public class DelayNoticeTask extends BaseBean implements ManualInvoke {
    @Autowired
    private DelayNoticeService delayNoticeService;
    @Autowired
    private WxTplMessageService wxTplMessageService;

    @Scheduled(cron = "0 0/30 * * ?")
    @Override
    public Object invoke() {
        List<DelayNotice> list = delayNoticeService.findAvailable();
        list.forEach(v -> {
            switch (NoticeType.valueOf(v.getNoticeType())) {
                case SMS:
                    break;
                case WX_TPL_MESSAGE:
                    wxTplMessageService.send(JSONObject.parseObject(v.getContext()));
                    break;
            }
            logger.info("delay notice sended: {}", v);
            v.setFired(true);
            delayNoticeService.update(v);
        });
        return list;
    }
}
