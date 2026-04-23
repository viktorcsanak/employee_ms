package com.example.userservice.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.permissions.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtSessionFilterTest {

  @Mock private SessionService sessionService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtSessionFilter filter;

  @Test
  void missingToken_returnsUnauthorized() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/some/protected");

    when(request.getCookies()).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
    verifyNoInteractions(filterChain);
  }

  @Test
  void validToken_setsAuthenticationAndContinues() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/protected");

    Cookie cookie = new Cookie("token", "abc");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});

    when(sessionService.getSessionUserId("abc")).thenReturn(1);
    when(sessionService.getUserRoles("abc")).thenReturn(Set.of(Role.ADMIN));

    doNothing().when(sessionService).verifySession("abc");

    filter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void invalidSession_returnsForbidden() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/protected");

    Cookie cookie = new Cookie("token", "abc");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});

    doThrow(new RuntimeException("invalid")).when(sessionService).verifySession("abc");

    filter.doFilterInternal(request, response, filterChain);

    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
    verifyNoInteractions(filterChain);
  }
}
