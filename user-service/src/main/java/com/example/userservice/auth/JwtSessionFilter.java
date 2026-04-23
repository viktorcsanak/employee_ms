package com.example.userservice.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtSessionFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(JwtSessionFilter.class);

  private final SessionService sessionService;

  public JwtSessionFilter(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String path = request.getRequestURI();

    // allow auth endpoints
    if (path.startsWith("/api/auth/login")
        || path.startsWith("/api/auth/logout")
        || path.startsWith("/api/auth/verify-token")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = null;

    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        if ("token".equals(c.getName())) {
          token = c.getValue();
        }
      }
    }

    if (token == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
      return;
    }

    try {
      sessionService.verifySession(token);

      final Integer userId = sessionService.getSessionUserId(token);

      final var roles = sessionService.getUserRoles(token);
      final var authorities =
          roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();

      final UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);

    } catch (Exception e) {
      log.error("token processing failed: {}", e.getMessage());
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
    }
  }
}
