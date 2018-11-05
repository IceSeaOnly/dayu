package site.binghai.biz.filters;

import site.binghai.lib.entity.WxUser;
import site.binghai.lib.inters.BaseInterceptor;
import site.binghai.lib.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WxLoginFilter extends BaseInterceptor<WxUser> {
    @Override
    protected boolean confirmed(WxUser user) {
        return true;
    }

    @Override
    protected String getRedirectUrl(HttpSession session, HttpServletRequest request) {
        return "/wx/wxLogin?openId=LOGIN&validate=LOGIN&backUrl=" + UrlUtil.getFullUrl(request);
    }
}

