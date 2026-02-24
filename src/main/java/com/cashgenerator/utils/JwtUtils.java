package com.cashgenerator.utils;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private String jwtExpirationMs;

    public String generateJwtToken(String subject, String claimkey, Object claim) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .claim(claimkey, claim)
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                //jwtSecret 服务端唯一知道的密钥， 用来对 token 进行HS512 签名
                // 生成 JWT 签名验证 JWT 是否被篡改
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is Unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims  string is empty: {}", e.getMessage());
        }
        return false;
    }
}
