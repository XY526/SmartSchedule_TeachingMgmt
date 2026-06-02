package com.crytalwjh.smartschedule_teachingmgmt.interceptors;

import com.crytalwjh.smartschedule_teachingmgmt.utils.JwtUtil;
import com.crytalwjh.smartschedule_teachingmgmt.utils.ThreadLocalUtil;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求信息
        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
        
        // 允许OPTIONS请求通过
        if (request.getMethod().equals("OPTIONS")) {
            log.info("Allowing OPTIONS request to pass through");
            return true;
        }
        
        //令牌验证
        String token = request.getHeader("Authorization");
        log.info("Received Authorization header: {}", token);
        
        // 处理Bearer前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            log.info("Token after removing Bearer prefix: {}", token);
        } else if (token != null) {
            log.warn("Token does not have Bearer prefix: {}", token);
        }
        
        //验证token
        try {
            if (token == null || token.isEmpty()) {
                log.warn("Token is null or empty");
                handleError(response, "未登录");
                return false;
            }
            
            Map<String, Object> claims = JwtUtil.parseToken(token);
            log.info("Token claims: {}", claims);
            
            if (claims == null || claims.isEmpty()) {
                log.warn("Claims is null or empty");
                handleError(response, "无效的token");
                return false;
            }
            
            // 将用户信息存入ThreadLocal
            ThreadLocalUtil.set(claims);
            log.info("User info stored in ThreadLocal: {}", claims);
            
            //放行
            return true;
        } catch (Exception e) {
            log.error("Token validation failed", e);
            //http响应状态码为401
            response.setStatus(401);
            handleError(response, "登录已过期，请重新登录");
            return false;
        }
    }

    private void handleError(HttpServletResponse response, String message) throws Exception {
        log.warn("Authentication error: {}", message);
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        Result result = Result.fail(message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal中的数据
        ThreadLocalUtil.remove();
        log.info("ThreadLocal data cleared after request completion");
    }
}
