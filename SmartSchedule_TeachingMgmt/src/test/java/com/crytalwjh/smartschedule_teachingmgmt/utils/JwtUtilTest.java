package com.crytalwjh.smartschedule_teachingmgmt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    public void testRememberMeToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("username", "admin");

        // 测试普通token（24小时）
        String normalToken = JwtUtil.genToken(claims, false);
        DecodedJWT decodedNormal = JWT.decode(normalToken);
        long normalExpiration = decodedNormal.getExpiresAt().getTime() - System.currentTimeMillis();
        // 验证过期时间是否接近24小时（允许1分钟误差）
        assertTrue(Math.abs(normalExpiration - 24 * 60 * 60 * 1000) < 60 * 1000);

        // 测试记住我token（30天）
        String rememberMeToken = JwtUtil.genToken(claims, true);
        DecodedJWT decodedRememberMe = JWT.decode(rememberMeToken);
        long rememberMeExpiration = decodedRememberMe.getExpiresAt().getTime() - System.currentTimeMillis();
        // 验证过期时间是否接近30天（允许1分钟误差）
        assertTrue(Math.abs(rememberMeExpiration - 30 * 24 * 60 * 60 * 1000) < 60 * 1000);

        // 验证token内容
        Map<String, Object> normalClaims = decodedNormal.getClaim("claims").asMap();
        Map<String, Object> rememberMeClaims = decodedRememberMe.getClaim("claims").asMap();
        
        // 验证claims内容
        assertEquals(claims.get("id"), normalClaims.get("id"));
        assertEquals(claims.get("username"), normalClaims.get("username"));
        assertEquals(claims.get("id"), rememberMeClaims.get("id"));
        assertEquals(claims.get("username"), rememberMeClaims.get("username"));
    }
} 