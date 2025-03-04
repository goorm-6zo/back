package goorm.back.zo6.auth.util;

import goorm.back.zo6.user.domain.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Log4j2
@Component
public class JwtUtil {
    private SecretKey secretKey;

    @Value("${jwt.valid-time}")
    private long TOKEN_VALID_TIME;

    public static final String BEARER_PREFIX = "bearer ";

    private JwtUtil(@Value("${jwt.secret}") String secret){
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email, String name, Role role){
        Date timeNow = new Date(System.currentTimeMillis());
        Date expirationTime = new Date(timeNow.getTime() + TOKEN_VALID_TIME);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("email",email)
                .claim("name",name)
                .claim("role",role.getRoleSecurity())
                .setIssuedAt(timeNow)
                .setExpiration(expirationTime)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public  Long getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("userId", Long.class);
    }
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("email", String.class);
    }

    public String getName(String token){
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("nickname", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public String resolveToken(String bearerToken){
        if(bearerToken != null && bearerToken.toLowerCase().startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    public boolean validateToken(String token){
        //log.info("토큰 유효성 검증 시작");
        return valid(secretKey, token);
    }

    private boolean valid(SecretKey secretKey, String token){
        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().before(new Date());
        }catch (SignatureException ex){
            throw new RuntimeException();
        }catch (MalformedJwtException ex){
            throw new RuntimeException();
        }catch (ExpiredJwtException ex){
            throw new RuntimeException();
        }catch (IllegalArgumentException ex){
            throw new RuntimeException();
        }
    }
}
