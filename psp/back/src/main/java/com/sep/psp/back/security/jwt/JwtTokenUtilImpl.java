package com.sep.psp.back.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtilImpl implements JwtTokenUtil {

    public static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000;

    private SecretKey secret;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        this.secret = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String username, String role, String merchantId) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("merchantId", merchantId)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String getUsernameFromJWT(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public String getRoleFromJWT(String token) {
        return getClaims(token).get("role", String.class);
    }

    @Override
    public String getMerchantIdFromJWT(String token) {
        return getClaims(token).get("merchantId", String.class);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);

            Date expirationDate = claims.getExpiration();

            return expirationDate == null || !expirationDate.before(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return (UserDetails) principal;
            }
        }

        return null;
    }

    @Override
    public String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private Claims getClaims(String token) {
        try {
            token = token.startsWith("Bearer ") ? token.substring(7) : token;

            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT token.");
        }
    }

}