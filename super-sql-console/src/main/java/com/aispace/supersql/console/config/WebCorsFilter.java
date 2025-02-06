package com.aispace.supersql.console.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 拦截器解决跨域问题
 *
 * @author chengjie.x.guo
 * @email chengjie.x.guo@gsk.com
 */
@Component
public class WebCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
