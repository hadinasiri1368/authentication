package org.authentication.common;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.authentication.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {
    private static String secret;
    private static int expirationMinutes;

    @Value("${jwt.secretKey}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.expirationMinutes}")
    public void setExpirationMinutes(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public static Map getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return (Map) claims.get("tokenDate");
    }

    public static User getUserFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue((Map) claims.get("tokenDate"), User.class);
    }

    private static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token.replaceAll("Bearer ", "")).getBody();
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static String generateToken(User o) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        String oldToken = TokenManager.getInstance().getToken(o.getId());
        if (!CommonUtils.isNull(oldToken)) {
            if (!isTokenExpired(oldToken))
                return oldToken;
            TokenManager.getInstance().removeTokenByUserId(o.getId());
        }
        claims.put("tokenDate", o);
        String token = doGenerateToken(claims);
        TokenManager.getInstance().setToken(o.getId(), token);
        return doGenerateToken(claims);
    }

    private static String doGenerateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setSubject("token").setIssuedAt(new Date())
                .setExpiration(DateUtils.addMinutes(new Date(), expirationMinutes))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    public static Boolean validateToken(String token) {
        try {
            return (!isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }

    }
}
