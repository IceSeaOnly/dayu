package site.binghai.biz.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.binghai.biz.filters.MockUserFilter;
import site.binghai.biz.filters.UserSubscribeFilter;
import site.binghai.biz.filters.WxLoginFilter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    @Bean
    public WxLoginFilter wxLoginFilter() {
        return new WxLoginFilter();
    }

    @Bean
    public MockUserFilter mockUserFilter() {
        return new MockUserFilter();
    }

    @Bean
    public UserSubscribeFilter userSubscribeFilter() {
        return new UserSubscribeFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(mockUserFilter()).addPathPatterns("/user/**", "/shop/**");
        registry.addInterceptor(wxLoginFilter()).addPathPatterns("/user/**", "/shop/**");
        //registry.addInterceptor(userSubscribeFilter()).addPathPatterns("/user/**");
    }
}
