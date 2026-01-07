package com.dstober.onething.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.dto.AuthResponse;
import com.dstober.onething.dto.LoginRequest;
import com.dstober.onething.dto.RegisterRequest;
import com.dstober.onething.model.User;
import com.dstober.onething.repository.UserRepository;
import com.dstober.onething.security.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        RegisterRequest request = TestDataFactory.createRegisterRequest();
        User savedUser = TestDataFactory.createUser();
        String token = "jwt.token.here";

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(TestDataFactory.DEFAULT_ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail())).thenReturn(token);

        AuthResponse result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getUserId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(result.getName()).isEqualTo(savedUser.getName());
    }

    @Test
    void register_DuplicateEmail() {
        RegisterRequest request = TestDataFactory.createRegisterRequest();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already in use");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_PasswordEncoded() {
        RegisterRequest request = TestDataFactory.createRegisterRequest();
        User savedUser = TestDataFactory.createUser();
        String encodedPassword = "$2a$10$newEncodedPassword";

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(tokenProvider.generateToken(anyLong(), anyString())).thenReturn("token");

        authService.register(request);

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(argThat(user -> user.getPassword().equals(encodedPassword)));
    }

    @Test
    void register_TokenGenerated() {
        RegisterRequest request = TestDataFactory.createRegisterRequest();
        User savedUser = TestDataFactory.createUser();
        String expectedToken = "generated.jwt.token";

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(TestDataFactory.DEFAULT_ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail())).thenReturn(expectedToken);

        AuthResponse result = authService.register(request);

        assertThat(result.getToken()).isEqualTo(expectedToken);
        verify(tokenProvider).generateToken(savedUser.getId(), savedUser.getEmail());
    }

    @Test
    void login_Success() {
        LoginRequest request = TestDataFactory.createLoginRequest();
        User user = TestDataFactory.createUser();
        String token = "jwt.login.token";
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(user.getId(), user.getEmail())).thenReturn(token);

        AuthResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getName()).isEqualTo(user.getName());
    }

    @Test
    void login_InvalidCredentials() {
        LoginRequest request = TestDataFactory.createLoginRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_UserNotFoundAfterAuth() {
        LoginRequest request = TestDataFactory.createLoginRequest();
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}
