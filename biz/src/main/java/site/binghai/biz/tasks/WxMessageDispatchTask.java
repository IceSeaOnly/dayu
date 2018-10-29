package site.binghai.biz.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.lib.utils.BaseBean;

@Component
public class WxMessageDispatchTask extends BaseBean {

    @Scheduled(cron = "0/5 * * * * ?")
    public void doJob(){

    }
}
