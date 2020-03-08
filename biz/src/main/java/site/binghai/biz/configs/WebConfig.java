package site.binghai.biz.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.binghai.biz.filters.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    public WxLoginFilter wxLoginFilter() {
        return new WxLoginFilter();
    }

    public MockUserFilter mockUserFilter() {
        return new MockUserFilter();
    }

    public UserSubscribeFilter userSubscribeFilter() {
        return new UserSubscribeFilter();
    }

    public ManagerInterceptor managerInterceptor() {
        return new ManagerInterceptor();
    }

    public MockManageInterceptor mockManageInterceptor(){
        return new MockManageInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(mockUserFilter()).addPathPatterns("/user/**", "/shop/**");
        registry.addInterceptor(wxLoginFilter()).addPathPatterns("/user/**", "/shop/**");
        //registry.addInterceptor(mockManageInterceptor()).addPathPatterns("/manage/**");
        registry.addInterceptor(managerInterceptor()).addPathPatterns("/manage/**");
        //registry.addInterceptor(userSubscribeFilter()).addPathPatterns("/user/**");
    }
}
