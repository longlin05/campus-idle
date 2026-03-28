package org.lin.campusidle.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 生成JWT令牌
     * @param subject 主题（通常是用户ID）
     * @param claims 自定义声明
     * @return JWT令牌
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtConfig.getExpireTime());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return 令牌中的声明
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取主题（用户ID）
     * @param token JWT令牌
     * @return 主题（用户ID）
     */
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 验证令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }
}