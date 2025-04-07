package ru.nskopt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtils {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expiration-time}")
  private long expirationTime;

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("authorities", userDetails.getAuthorities());
    return generateToken(claims, userDetails);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    long timestamp = System.currentTimeMillis();

    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(timestamp))
        .expiration(new Date(timestamp + expirationTime))
        .signWith(getSignInKey())
        .compact();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 64)
      throw new IllegalArgumentException(
          "Key too short. For HS512, key must be at least 64 bytes (512 bits)");

    return Keys.hmacShaKeyFor(keyBytes);
  }
}
