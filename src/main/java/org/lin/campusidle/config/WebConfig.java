package org.lin.campusidle.config;

import org.lin.campusidle.common.jwt.JwtAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoggerInterceptor loggerInterceptor;
    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Autowired
    public WebConfig(LoggerInterceptor loggerInterceptor, JwtAuthInterceptor jwtAuthInterceptor) {
        this.loggerInterceptor = loggerInterceptor;
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册日志记录拦截器，顺序为1（最先执行）
        registry.addInterceptor(loggerInterceptor)
                .addPathPatterns("/**")
                .order(1);
        
        // 注册JWT验证拦截器，顺序为2
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**")
                .order(2);
    }
}