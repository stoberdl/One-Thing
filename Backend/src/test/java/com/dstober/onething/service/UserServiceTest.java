package com.dstober.onething.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.model.User;
import com.dstober.onething.repository.UserRepository;
import com.dstober.onething.security.UserPrincipal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  @Test
  void loadUserByUsername_Found() {
    String email = "test@example.com";
    User user = TestDataFactory.createUser();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    UserDetails result = userService.loadUserByUsername(email);

    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(UserPrincipal.class);
    assertThat(result.getUsername()).isEqualTo(email);

    UserPrincipal principal = (UserPrincipal) result;
    assertThat(principal.getId()).isEqualTo(TestDataFactory.DEFAULT_USER_ID);
    assertThat(principal.getEmail()).isEqualTo(email);
    assertThat(principal.getName()).isEqualTo(TestDataFactory.DEFAULT_NAME);
  }

  @Test
  void loadUserByUsername_NotFound() {
    String email = "nonexistent@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.loadUserByUsername(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("User not found with email: " + email);
  }

  @Test
  void getUserById_Found() {
    Long userId = 1L;
    User user = TestDataFactory.createUser();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    User result = userService.getUserById(userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
    assertThat(result.getEmail()).isEqualTo(TestDataFactory.DEFAULT_EMAIL);
  }

  @Test
  void getUserById_NotFound() {
    Long userId = 999L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserById(userId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id: " + userId);
  }
}
