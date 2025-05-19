package swp.group2.swpbe;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import swp.group2.swpbe.exception.ApiRequestException;

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
        if (token == null || token.trim().isEmpty()) {
            throw new ApiRequestException("Token is empty or null", HttpStatus.UNAUTHORIZED);
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token.trim())
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new ApiRequestException("Token has expired", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            throw new ApiRequestException("Token format is not supported", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            throw new ApiRequestException("Token is malformed", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            throw new ApiRequestException("Token signature is invalid", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ApiRequestException("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}