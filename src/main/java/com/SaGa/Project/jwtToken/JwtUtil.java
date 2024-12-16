package com.SaGa.Project.jwtToken;

import com.SaGa.Project.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRETKEY;

    public String generateToken(User userForToken, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("userId", userForToken.getUserId());
        return createToken(claims, userForToken.getEmail());
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*60))
                .signWith(SignatureAlgorithm.HS256, SECRETKEY)
                .compact();
    }
    public Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRETKEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }
    public String extractUserId(String token){
        Claims claims = extractClaims(token);
        return claims.get("userId", String.class);
    }

    public List<String> extractRoles(String token){
        Claims claims = extractClaims(token);
        return (List<String>) claims.get("roles");
    }

    public boolean isExpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validToken(String token, String email){
        return (email.equals(extractEmail(token)) && ! isExpired(token));
    }


}
