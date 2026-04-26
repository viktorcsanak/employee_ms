package com.example.userservice.config.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.common.exception.SessionNotFoundException;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.permissions.Role;
import com.example.userservice.session.Session;
import com.example.userservice.session.SessionService;
import com.example.userservice.user.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtSessionFilterTest {

  @Mock private SessionService sessionService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @InjectMocks private JwtSessionFilter filter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void missingToken_returnsUnauthorized() throws Exception {

    when(request.getRequestURI()).thenReturn("/api/protected");
    when(request.getCookies()).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
    verifyNoInteractions(filterChain);
    verifyNoInteractions(sessionService);
  }

  @Test
  void validToken_setsAuthentication_andContinues() throws Exception {

    when(request.getRequestURI()).thenReturn("/api/protected");

    Cookie cookie = new Cookie("token", "abc");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});

    // mock domain objects
    Session session = mock(Session.class);
    User user = mock(User.class);

    when(sessionService.validateAndGetSession("abc")).thenReturn(session);
    when(session.getUser()).thenReturn(user);
    when(user.getId()).thenReturn(1);
    when(user.getRoles()).thenReturn(Set.of(Role.ADMIN));

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    assertNotNull(auth);
    assertEquals(1, auth.getPrincipal());

    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

    verify(sessionService).validateAndGetSession("abc");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void invalidSession_returnsForbidden_userUnauthorized() throws Exception {

    when(request.getRequestURI()).thenReturn("/api/protected");

    Cookie cookie = new Cookie("token", "abc");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});

    when(sessionService.validateAndGetSession("abc"))
        .thenThrow(new UserUnauthorizedException("invalid"));

    filter.doFilterInternal(request, response, filterChain);

    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
    verifyNoInteractions(filterChain);
  }

  @Test
  void invalidSession_returnsForbidden_sessionNotFound() throws Exception {

    when(request.getRequestURI()).thenReturn("/api/protected");

    Cookie cookie = new Cookie("token", "abc");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});

    when(sessionService.validateAndGetSession("abc"))
        .thenThrow(new SessionNotFoundException("missing"));

    filter.doFilterInternal(request, response, filterChain);

    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
    verifyNoInteractions(filterChain);
  }

  @Test
  void authEndpoints_areSkipped() throws Exception {

    when(request.getRequestURI()).thenReturn("/api/auth/login");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(sessionService);
  }
}
