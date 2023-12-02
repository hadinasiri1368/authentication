package org.authentication.config;

import org.authentication.filter.CheckPermission;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<CheckPermission> checkPermissionFilter() {
        FilterRegistrationBean<CheckPermission> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new CheckPermission());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
