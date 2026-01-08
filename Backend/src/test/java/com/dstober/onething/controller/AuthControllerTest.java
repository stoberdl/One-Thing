package com.dstober.onething.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.dto.AuthResponse;
import com.dstober.onething.dto.LoginRequest;
import com.dstober.onething.dto.RegisterRequest;
import com.dstober.onething.exception.GlobalExceptionHandler;
import com.dstober.onething.security.JwtAuthenticationFilter;
import com.dstober.onething.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class))
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthService authService;

  @Test
  void register_Success() throws Exception {
    AuthResponse response = TestDataFactory.createAuthResponse("jwt.token");

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    String requestBody =
        """
        {
            "email": "test@example.com",
            "password": "password123",
            "name": "Test User"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("jwt.token"))
        .andExpect(jsonPath("$.userId").value(TestDataFactory.DEFAULT_USER_ID))
        .andExpect(jsonPath("$.email").value(TestDataFactory.DEFAULT_EMAIL))
        .andExpect(jsonPath("$.name").value(TestDataFactory.DEFAULT_NAME));
  }

  @Test
  void register_DuplicateEmail_Returns400WithJson() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(new RuntimeException("Email already in use"));

    String requestBody =
        """
        {
            "email": "existing@example.com",
            "password": "password123",
            "name": "Test User"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Email already in use"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void register_InvalidEmail_Returns400() throws Exception {
    String requestBody =
        """
        {
            "email": "invalid-email",
            "password": "password123",
            "name": "Test User"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void register_ShortPassword_Returns400() throws Exception {
    String requestBody =
        """
        {
            "email": "test@example.com",
            "password": "short",
            "name": "Test User"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void register_ShortName_Returns400() throws Exception {
    String requestBody =
        """
        {
            "email": "test@example.com",
            "password": "password123",
            "name": "T"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void register_BlankFields_Returns400() throws Exception {
    String requestBody =
        """
        {
            "email": "",
            "password": "",
            "name": ""
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void login_Success() throws Exception {
    AuthResponse response = TestDataFactory.createAuthResponse("jwt.login.token");

    when(authService.login(any(LoginRequest.class))).thenReturn(response);

    String requestBody =
        """
        {
            "email": "test@example.com",
            "password": "password123"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt.login.token"))
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.email").exists());
  }

  @Test
  void login_InvalidCredentials_Returns401WithJson() throws Exception {
    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    String requestBody =
        """
        {
            "email": "test@example.com",
            "password": "wrongpassword"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Invalid email or password"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void login_ValidationError_Returns400() throws Exception {
    String requestBody =
        """
        {
            "email": "",
            "password": ""
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void login_RuntimeException_Returns400WithJson() throws Exception {
    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new RuntimeException("User not found"));

    String requestBody =
        """
        {
            "email": "test@example.com",
            "password": "password123"
        }
        """;

    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("User not found"));
  }
}
