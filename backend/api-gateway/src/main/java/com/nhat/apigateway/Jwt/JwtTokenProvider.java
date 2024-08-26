package com.nhat.apigateway.Jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${security.jwt.secret-key}")
    private String SECRETKEY;

    private static final long validityInMilliseconds = 3600000;

//    public String createToken(UserDetails userDetails) {
//        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);
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
