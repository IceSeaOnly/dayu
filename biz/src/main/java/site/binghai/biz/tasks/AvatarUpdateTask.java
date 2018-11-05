package site.binghai.biz.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.lib.def.UserTask;
import site.binghai.lib.entity.WxInfo;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.WxCommonService;
import site.binghai.lib.utils.BaseBean;

@Component
public class AvatarUpdateTask extends BaseBean implements UserTask {
    @Autowired
    private WxCommonService wxCommonService;

    @Override
    public WxUser handle(WxUser user) {
        WxInfo info = wxCommonService.getUserInfo(user.getOpenId());
        if (!info.isSubscribed()) {
            logger.warn("{} canceled subscribe on us.", user);
            return user;
        }

        if (hasEmptyString(info.getHeadimgurl()) && !info.getHeadimgurl().equals(user.getAvatar())) {
            user.setAvatar(info.getHeadimgurl());
        }
        return user;
    }
}
