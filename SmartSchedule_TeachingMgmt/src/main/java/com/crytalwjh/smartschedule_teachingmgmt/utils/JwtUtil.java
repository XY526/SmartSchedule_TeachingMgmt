package com.crytalwjh.smartschedule_teachingmgmt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "itheima";
    
    // 默认token过期时间（24小时）
    private static final long DEFAULT_EXPIRATION = 1000 * 60 * 60 * 24;
    // 记住我token过期时间（30天）
    private static final long REMEMBER_ME_EXPIRATION = 1000 * 60 * 60 * 24 * 30;
	
    //接收业务数据,生成token并返回
    public static String genToken(Map<String, Object> claims) {
        return genToken(claims, false);
    }

    //接收业务数据,生成token并返回，支持记住我功能
    public static String genToken(Map<String, Object> claims, boolean rememberMe) {
        long expiration = rememberMe ? REMEMBER_ME_EXPIRATION : DEFAULT_EXPIRATION;
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(KEY));
    }

	//接收token,验证token,并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

}
