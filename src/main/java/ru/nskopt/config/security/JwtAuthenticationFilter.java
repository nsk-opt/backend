package ru.nskopt.config.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nskopt.exceptions.AuthenticationFailedException;
import ru.nskopt.utils.JwtUtils;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final CustomUserDetailsService customUserDetailsService;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = extractToken(request);

      if (token == null) {
        filterChain.doFilter(request, response);
        return;
      }

      String username = jwtUtils.extractUsername(token);
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

      if (!jwtUtils.isTokenValid(token, userDetails)) {
        if (jwtUtils.isTokenExpired(token)) {
          throw new AuthenticationFailedException("Token expired");
        } else {
          throw new AuthenticationFailedException("Invalid token");
        }
      }

      SecurityContextHolder.getContext()
          .setAuthentication(
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities()));

    } catch (AuthenticationFailedException | JwtException | IllegalArgumentException e) {

      logger.error("JWT processing error: " + e.getMessage());
      authenticationEntryPoint.commence(
          request, response, new BadCredentialsException("JWT token is invalid or expired"));
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) return header.substring(7);

    return null;
  }
}
