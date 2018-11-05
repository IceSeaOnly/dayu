package site.binghai.biz.filters;

import site.binghai.lib.config.IceConfig;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.inters.BaseInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserSubscribeFilter extends BaseInterceptor<WxUser> {
    @Override
    protected boolean confirmed(WxUser obj) {
        return obj.getSubscribed();
    }

    @Override
    protected String getRedirectUrl(HttpSession session, HttpServletRequest request) {
        return IceConfig.iceConfigHolder.getSubscribePage();
    }
}
