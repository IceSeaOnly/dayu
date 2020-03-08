package site.binghai.lib.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

/**
 * @author huaishuo
 * @date 2020/3/8 下午12:06
 **/
public class SchoolIdThreadLocal {
    private static final String TAG = "_SCHOOL_ID_";

    public static Long getSchoolId() {
        if (getSession() == null) {
            return null;
        }
        Long id = (Long)getSession().getAttribute(TAG);
        setSchoolId(id);
        return id;
    }

    public static void setSchoolId(Long schoolId) {
        if (getSession() != null) {
            getSession().setAttribute(TAG, schoolId);
        }
    }

    private static HttpSession getSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes;
        if (requestAttributes instanceof ServletRequestAttributes) {
            servletRequestAttributes = (ServletRequestAttributes)requestAttributes;
            return servletRequestAttributes.getRequest().getSession();
        }
        return null;
    }
}
