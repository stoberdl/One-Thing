package com.dstober.onething.service;

import com.dstober.onething.dto.AuthResponse;
import com.dstober.onething.dto.LoginRequest;
import com.dstober.onething.dto.RegisterRequest;
import com.dstober.onething.model.User;
import com.dstober.onething.repository.UserRepository;
import com.dstober.onething.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtTokenProvider tokenProvider;

  @Autowired private AuthenticationManager authenticationManager;

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Email already in use");
    }

    User user =
        new User(
            request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName());

    User savedUser = userRepository.save(user);

    String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail());

    return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getName());
  }

  public AuthResponse login(LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    String token = tokenProvider.generateToken(user.getId(), user.getEmail());

    return new AuthResponse(token, user.getId(), user.getEmail(), user.getName());
  }
}
