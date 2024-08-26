package com.nhat.fileservice.Jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${security.jwt.secret-key}")
    private String SECRETKEY;

    private static final long validityInMilliseconds = 3600000;

//    @Autowired
//    CustomUserDetailsService customUserDetailsService;

//    public String createToken(String username, String role) {
//        Claims claims = Jwts.claims().setSubject(username);
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);
//        claims.put("role", role);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, SECRETKEY)
//                .compact();
//    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(SECRETKEY).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserID(String token) {
        return Jwts.parser().setSigningKey(SECRETKEY).parseClaimsJws(token).getBody().getId();
    }

//    public String getRoles(String token) {
//        return Jwts.parser().setSigningKey(SECRETKEY).parseClaimsJws(token).getBody().get("role").toString();
//    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRETKEY).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

}
