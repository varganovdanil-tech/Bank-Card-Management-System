package com.bank.cardmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**");
    }
    
    public static class LoggingInterceptor extends HandlerInterceptorAdapter {
        
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            long startTime = System.currentTimeMillis();
            request.setAttribute("startTime", startTime);
            logger.info("Incoming Request: {} {} from {}", 
                       request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
            return true;
        }
        
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            long startTime = (Long) request.getAttribute("startTime");
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            
            logger.info("Completed Request: {} {} - Status: {} - Time: {}ms", 
                       request.getMethod(), request.getRequestURI(), response.getStatus(), executeTime);
        }
    }
}