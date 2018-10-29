package site.binghai.lib.tasks;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.lib.def.UserTask;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.BaseBean;

import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
public class UserTaskCenter extends BaseBean {
    private List<UserTask> userTasks = new ArrayList<>();
    @Autowired
    private WxUserService wxUserService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void doJob() {
        int page = 0;
        int pageSize = 100;
        List<WxUser> users = wxUserService.findAll(page++, pageSize);

        logger.info("User Tasks Start.");
        while (!isEmptyList(users)) {
            for (WxUser user : users) {
                JSONObject old = toJsonObject(user);
                user = handleTask(user);
                JSONObject cur = toJsonObject(user);
                if (!old.equals(cur)) {
                    logger.info("user info changed by UserTasks from {} to {}", old, cur);
                    wxUserService.update(user);
                }
            }
        }
        logger.info("User Tasks End.");
    }

    private WxUser handleTask(WxUser user) {
        for (UserTask task : userTasks) {
            user = task.handle(user);
        }
        return user;
    }

    public void join(List<UserTask> tasks) {
        userTasks.addAll(tasks);
    }
}
