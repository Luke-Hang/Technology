package com.cashgenerator.utils;

import com.cashgenerator.model.LoginBo;
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

    /**
     *
     * @param subject   用户登录名
     * @param claimKey    Claim名称  customer_auth_token or app_auth_token
     * @param claim       Claim值: LoginBo的JSON字符串
     *
     *
     *  **生成的Token结构**:
     * ```json
     * {
     *   "sub": "user123",
     *   "customer_auth_token": "{\"loginId\":\"user123\",\"storeId\":\"1\",...}",
     *   "iat": 1704067200000,
     *   "exp": 1704153600000
     * }
     */
    // Subject: 用户登录名，Claim名称，Claim值: LoginBo的JSON字符串
    public String generateJwtToken(String subject, String claimKey, Object claim) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .claim(claimKey, claim)
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
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public <T> T getClaim(String jwtToken, String claimKey, Class<T> clazz) {
        Claims body = Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(jwtToken).getBody();
        T object = (T) body.get(claimKey, clazz);
        return object;
    }
}
