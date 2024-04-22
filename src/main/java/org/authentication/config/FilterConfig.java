package org.authentication.config;

import org.authentication.filter.ErrorHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ErrorHandler> errorHandlerFilter() {
        FilterRegistrationBean<ErrorHandler> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ErrorHandler());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
