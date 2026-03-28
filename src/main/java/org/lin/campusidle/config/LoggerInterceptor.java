package org.lin.campusidle.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute("startTime", System.currentTimeMillis());
        
        // 记录请求基本信息
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            log.info("Request: {} {} - Method: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    handlerMethod.getMethod().getName());
        } else {
            log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        }
        
        // 记录请求参数
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            // 敏感参数脱敏
            if ("password".equals(name) || "token".equals(name)) {
                params.put(name, "***");
            } else {
                params.put(name, request.getParameter(name));
            }
        }
        if (!params.isEmpty()) {
            log.info("Request parameters: {}", params);
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 计算响应时间
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 记录响应信息
        log.info("Response: {} - {}ms - Status: {}", 
                request.getRequestURI(), 
                duration, 
                response.getStatus());
        
        // 记录异常信息
        if (ex != null) {
            log.error("Exception occurred: {}", ex.getMessage(), ex);
        }
    }
}