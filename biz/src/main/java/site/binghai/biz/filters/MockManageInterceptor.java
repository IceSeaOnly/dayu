package site.binghai.biz.filters;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import site.binghai.lib.entity.Manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author huaishuo
 * @date 2020/2/27 下午11:50
 **/
public class MockManageInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        HttpSession session = request.getSession();
        if (session != null) {
            Object obj = session.getAttribute("__MANAGER__");
            if (obj == null) {
                session.setAttribute("__MANAGER__", mockManage());
            }
        }
        return true;
    }

    private Manager mockManage() {
        Manager manager = new Manager();
        manager.setId(1L);
        manager.setUserName("Lee");
        manager.setPassWord("123456");
        manager.setForbidden(Boolean.FALSE);
        return manager;
    }
}
