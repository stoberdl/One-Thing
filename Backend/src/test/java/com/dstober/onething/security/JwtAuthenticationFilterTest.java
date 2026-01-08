package com.dstober.onething.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.model.User;
import com.dstober.onething.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtTokenProvider tokenProvider;

  @Mock private UserRepository userRepository;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
    String token = "valid.jwt.token";
    Long userId = 1L;
    User user = TestDataFactory.createUser();

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isInstanceOf(UserPrincipal.class);

    UserPrincipal principal =
        (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    assertThat(principal.getId()).isEqualTo(userId);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_NoHeader_ContinuesWithoutAuth() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(anyString());
  }

  @Test
  void doFilterInternal_InvalidToken_ContinuesWithoutAuth() throws Exception {
    String token = "invalid.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
    verify(userRepository, never()).findById(anyLong());
  }

  @Test
  void doFilterInternal_UserNotFound_ContinuesWithoutAuth() throws Exception {
    String token = "valid.jwt.token";
    Long userId = 1L;

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_NoBearer_ContinuesWithoutAuth() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Basic credentials");

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(anyString());
  }

  @Test
  void doFilterInternal_EmptyToken_ContinuesWithoutAuth() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Bearer ");

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ExceptionThrown_LogsAndContinues() throws Exception {
    String token = "valid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.getUserIdFromToken(token)).thenThrow(new RuntimeException("Test error"));

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }
}
