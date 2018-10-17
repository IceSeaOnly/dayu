package site.binghai.biz.filters;

import site.binghai.lib.inters.BaseInterceptor;
import site.binghai.lib.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WxLoginFilter extends BaseInterceptor {
    private static final String tag = "__WXUSER__";

    @Override
    protected String getRedirectUrl(HttpSession session, HttpServletRequest request) {
        return "/wx/wxLogin?openId=LOGIN&validate=LOGIN&backUrl=" + UrlUtil.getFullUrl(request);
    }

    @Override
    protected String getFilterTag(HttpSession session) {
        return tag;
    }
}

