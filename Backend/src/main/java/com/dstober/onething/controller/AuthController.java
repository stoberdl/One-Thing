package com.dstober.onething.controller;

import com.dstober.onething.dto.AuthResponse;
import com.dstober.onething.dto.LoginRequest;
import com.dstober.onething.dto.RegisterRequest;
import com.dstober.onething.exception.ErrorResponse;
import com.dstober.onething.service.AuthService;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    try {
      AuthResponse response = authService.register(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (RuntimeException e) {
      ErrorResponse error =
          new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), Instant.now());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    try {
      AuthResponse response = authService.login(request);
      return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
      ErrorResponse error =
          new ErrorResponse(
              HttpStatus.UNAUTHORIZED.value(), "Invalid email or password", Instant.now());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    } catch (RuntimeException e) {
      ErrorResponse error =
          new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), Instant.now());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
  }
}
