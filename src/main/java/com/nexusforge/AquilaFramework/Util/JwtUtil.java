package com.nexusforge.AquilaFramework.Util;

import com.nexusforge.AquilaFramework.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "D3IUnEntfaD4u520TZNNOiyPR62C4LDH";

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();

    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        try{
            var claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject().equals(userDetails.getUsername()) &&
                    !claims.getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired at: " + e.getClaims().getExpiration());
        }catch(Exception e){
            System.out.println("Invalid JWT: " + e.getMessage());
        }
        return false;
    }

}
