package com.vitasoft.goodsgrapher.core.security;

import com.vitasoft.goodsgrapher.domain.model.enums.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.DecodingException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    public String generateJwtToken(String memberId, MemberRole memberRole) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("role", "ROLE_" + memberRole.toString())
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null)
            return null;
        else if (token.contains("Bearer"))
            token = token.replace("Bearer ", "");
        else
            throw new DecodingException("");
        return getClaimsFromToken(token);
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    protected Authentication getAuthentication(Claims claims) {
        return new UsernamePasswordAuthenticationToken(claims.get("memberId"), "", getAuthorities(claims));
    }

    private Collection<GrantedAuthority> getAuthorities(Claims claims) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String role = claims.get("role", String.class);
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);
        grantedAuthorities.add(simpleGrantedAuthority);
        return grantedAuthorities;
    }
}
