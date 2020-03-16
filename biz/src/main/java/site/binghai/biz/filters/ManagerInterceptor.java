package site.binghai.biz.filters;

import site.binghai.lib.entity.Manager;
import site.binghai.lib.inters.BaseInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author icesea
 * @date 2020/2/27 下午11:43
 **/
public class ManagerInterceptor extends BaseInterceptor<Manager> {
    @Override
    protected boolean confirmed(Manager obj) {
        return true;
    }

    @Override
    protected String getRedirectUrl(HttpSession session, HttpServletRequest request) {
        return "/p/login";
    }
}
