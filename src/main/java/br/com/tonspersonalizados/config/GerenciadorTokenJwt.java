package br.com.tonspersonalizados.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GerenciadorTokenJwt {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.resetSecret}")
    private String resetSecret;

    @Value("${jwt.resetValidity}")
    private Long resetTokenValidity;

    @Value("${jwt.validity}")
    private long jwtTokenValidity;

    // -------------------------
    // ‘Token’ de autenticação
    // -------------------------

    public String getUsernameFromToken(String token) {
        return getClaimForToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimForToken(token, Claims::getExpiration);
    }

    public String generateToken(final Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(parseSecret())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1_000))
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // -------------------------
    // Token de reset de senha
    // -------------------------

    public String generateResetToken(String userId, String email, String currentPasswordHash) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("pwdHash", currentPasswordHash)
                .signWith(parseResetSecret())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + resetTokenValidity * 1_000))
                .compact();
    }

    public boolean validateResetToken(String token, String currentPasswordHash) {
        try {
            Claims claims = getAllClaimsFromResetToken(token);
            String tokenPwdHash = claims.get("pwdHash", String.class);
            return !isResetTokenExpired(token) && tokenPwdHash.equals(currentPasswordHash);
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromResetToken(String token) {
        return getClaimForResetToken(token, Claims::getSubject);
    }

    public String getEmailFromResetToken(String token) {
        return getClaimForResetToken(token, claims -> claims.get("email", String.class));
    }

    public <T> T getClaimForToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    public <T> T getClaimForResetToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromResetToken(token));
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date(System.currentTimeMillis()));
    }

    private boolean isResetTokenExpired(String token) {
        return getClaimForResetToken(token, Claims::getExpiration)
                .before(new Date(System.currentTimeMillis()));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(parseSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims getAllClaimsFromResetToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(parseResetSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey parseSecret() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey parseResetSecret() {
        return Keys.hmacShaKeyFor(this.resetSecret.getBytes(StandardCharsets.UTF_8));
    }
}