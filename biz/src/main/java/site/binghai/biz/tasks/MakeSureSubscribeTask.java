package site.binghai.biz.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.lib.def.UserTask;
import site.binghai.lib.entity.WxInfo;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.WxCommonService;

@Component
public class MakeSureSubscribeTask implements UserTask {
    @Autowired
    private WxCommonService wxCommonService;

    @Override
    public WxUser handle(WxUser user) {
        WxInfo wxInfo = wxCommonService.getUserInfo(user.getOpenId());
        user.setSubscribed(wxInfo.isSubscribed());
        return user;
    }
}
