package edu.scau.forestproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类，用于生成和解析JWT令牌
 */
public class JwtUtils {

    /**
     * 密钥（Base64编码）
     */
    private static final String SECRET_KEY = "Zm9yZXN0IGVjb25vbXkgcHJvamVjdCBjcmVhdGVkIGJ5IGxpYW5nIGNoZW4gYW5kIGxpIHFpbnlhbmcgYW5kIGEgbG90IG9mIGdlbml1cw==";

    /**
     * 令牌过期时间（一个月，单位：毫秒）
     */
    private static final long EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000; // 一个月

    /**
     * 生成JWT令牌
     *
     * @param claims 载荷数据
     * @return JWT令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        // 将Base64字符串密钥转换为SecretKey对象
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        // 设置过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        // 构建并返回JWT
        return Jwts.builder()
                .claims(claims)  // 设置自定义载荷
                .expiration(expirationDate)  // 设置过期时间
                .signWith(key, Jwts.SIG.HS256)  // 设置签名算法和密钥
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌字符串
     * @return 解析后的载荷数据
     */
    public static Claims parseToken(String token) {
        // 将Base64字符串密钥转换为SecretKey对象
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        // 解析并返回载荷
        return Jwts.parser()
                .verifyWith(key)  // 设置验证密钥
                .build()  // 构建解析器
                .parseSignedClaims(token)  // 解析令牌
                .getPayload();  // 获取载荷
    }
}