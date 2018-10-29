package site.binghai.lib.def;

import site.binghai.lib.entity.WxUser;

public interface UserTask {
    WxUser handle(WxUser user);
}
