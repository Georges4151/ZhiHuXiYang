package com.elderly.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter implements Filter {
    
    private static final int MAX_REQUESTS = 10;
    private static final int TIME_WINDOW_MS = 60 * 1000;
    
    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String uri = httpRequest.getRequestURI();
        
        if (isLoginUri(uri)) {
            String ip = getClientIp(httpRequest);
            
            if (!isAllowed(ip)) {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\"}");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
    
    private boolean isLoginUri(String uri) {
        return uri.startsWith("/api/auth/login") || uri.startsWith("/admin/auth/login");
    }
    
    private boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        
        RequestInfo info = requestCounts.computeIfAbsent(ip, k -> new RequestInfo());
        
        synchronized (info) {
            if (now - info.startTime > TIME_WINDOW_MS) {
                info.startTime = now;
                info.count.set(0);
            }
            
            int currentCount = info.count.incrementAndGet();
            return currentCount <= MAX_REQUESTS;
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    private static class RequestInfo {
        long startTime = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
    }
}
