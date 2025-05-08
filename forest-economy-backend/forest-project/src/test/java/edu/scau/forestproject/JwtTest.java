package edu.scau.forestproject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
//    public void testGenerateJwt() {
//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("id", 1);
//        dataMap.put("name", "test");
//        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, "aWxvdmV5b3VpbXNvcnJ5aXRmZWVsc2xpa2VpbWZhbGxpbmdpbmxvdmU=")
//                .addClaims(dataMap)
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
//                .compact();
//        System.out.println(jwt);
//    }
    public void testGenerateJwt() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", 1);
        dataMap.put("name", "test");

        // 将Base64字符串密钥转换为SecretKey对象
        byte[] keyBytes = Base64.getDecoder().decode("Zm9yZXN0IGVjb25vbXkgcHJvamVjdCBjcmVhdGVkIGJ5IGxpYW5nIGNoZW4gYW5kIGxpIHFpbnlhbmcgYW5kIGEgbG90IG9mIGdlbml1cw==");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String jwt = Jwts.builder()
                .claims(dataMap)  // 替代addClaims方法
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))  // 替代setExpiration
                .signWith(key, Jwts.SIG.HS256)  // 新的签名方式
                .compact();

        System.out.println(jwt);
    }

    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdCIsImlkIjoxLCJleHAiOjE3NDY2MDUwOTN9.G1KfFJwaN4o9BjG5qNJbL9s52Kz0O7FbQtK08wjXJm0";

        // 将Base64字符串密钥转换为SecretKey对象
        byte[] keyBytes = Base64.getDecoder().decode("Zm9yZXN0IGVjb25vbXkgcHJvamVjdCBjcmVhdGVkIGJ5IGxpYW5nIGNoZW4gYW5kIGxpIHFpbnlhbmcgYW5kIGEgbG90IG9mIGdlbml1cw==");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        // 使用新版API解析JWT
        Claims claims = Jwts.parser()
                .verifyWith(key)           // 替代setSigningKey方法
                .build()                    // 构建解析器
                .parseSignedClaims(token)   // 替代parseClaimsJws方法
                .getPayload();              // 替代getBody方法

        System.out.println(claims);
    }
}
