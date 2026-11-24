package com.elderly.interceptor;

import com.elderly.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        if (isPublicUri(uri)) {
            return true;
        }
        
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或 Token 无效\"}");
            return false;
        }
        
        String token = authorization.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期，请重新登录\"}");
            return false;
        }
        
        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("userType", jwtUtil.getUserTypeFromToken(token));
        
        return true;
    }
    
    private boolean isPublicUri(String uri) {
        return uri.startsWith("/api/auth/") ||
               uri.startsWith("/admin/auth/") ||
               uri.startsWith("/api/news") ||
               uri.startsWith("/api/weather") ||
               uri.startsWith("/api/stats") ||
               uri.startsWith("/admin/stats") ||
               uri.startsWith("/api/upload") ||
               uri.startsWith("/upload/") ||
               uri.startsWith("/api/service") ||
               uri.startsWith("/api/health/analysis") ||
               uri.startsWith("/api/social") ||
               uri.startsWith("/api/activity") ||
               uri.startsWith("/admin/emergency") ||
               uri.equals("/") ||
               uri.startsWith("/index.html") ||
               uri.startsWith("/assets/") ||
               uri.endsWith(".js") ||
               uri.endsWith(".css") ||
               uri.endsWith(".png") ||
               uri.endsWith(".jpg") ||
               uri.endsWith(".jpeg") ||
               uri.endsWith(".gif") ||
               uri.endsWith(".ico") ||
               uri.endsWith(".svg") ||
               uri.endsWith(".woff") ||
               uri.endsWith(".woff2") ||
               uri.endsWith(".ttf") ||
               uri.endsWith(".eot");
    }
}
