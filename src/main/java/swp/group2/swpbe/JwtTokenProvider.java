package swp.group2.swpbe;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;

@Service
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String JWT_SECRET;
    private static final long JWT_VERIFY_EXPIRATION = 300000L;
    private static final long JWT_ACCESS_EXPIRATION = 2592000000L;

    public String generateVerifyToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_VERIFY_EXPIRATION);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String generateAccessToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_ACCESS_EXPIRATION);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String verifyToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}