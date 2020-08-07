package fun.littlecc.web.config;

import fun.littlecc.common.filter.SignFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author luckyjiet
 * @date 2020-07-29 18:15
 */
@Configuration
public class FilterConfig {

    /**
     * 注入签名过滤器
     *
     * @return 过滤器注册
     */
    @Bean
    public FilterRegistrationBean<SignFilter> signFilter() {
        FilterRegistrationBean<SignFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter((SignFilter) getSignFilter());
        registration.addUrlPatterns("/*");
        registration.setName("SignFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public Filter getSignFilter() {
        return new SignFilter();
    }
}
