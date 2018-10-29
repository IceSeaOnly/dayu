package site.binghai.lib.inters;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import site.binghai.lib.interfaces.SessionPersistent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.ParameterizedType;

public abstract class BaseInterceptor<T extends SessionPersistent> extends HandlerInterceptorAdapter {
    private T holder;

    protected abstract boolean confirmed(T obj);

    protected abstract String getRedirectUrl(HttpSession session, HttpServletRequest request);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        HttpSession session = request.getSession();
        if (session != null) {
            Object obj = session.getAttribute(getInstanceTag());
            if (obj != null && confirmed((T)obj)) {
                return true;
            }
        }
        response.sendRedirect(getRedirectUrl(session, request));
        return false;
    }

    private String getInstanceTag()
        throws IllegalAccessException, InstantiationException {
        if (holder == null) {
            holder = getTypeArguement().newInstance();
        }
        return holder.sessionTag();
    }

    protected Class<T> getTypeArguement() {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

}