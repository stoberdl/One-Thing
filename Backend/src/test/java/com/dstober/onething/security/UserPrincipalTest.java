package com.dstober.onething.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.model.User;
import org.junit.jupiter.api.Test;

class UserPrincipalTest {

    @Test
    void create_FromUser() {
        User user = TestDataFactory.createUser();

        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal).isNotNull();
        assertThat(principal.getId()).isEqualTo(user.getId());
        assertThat(principal.getEmail()).isEqualTo(user.getEmail());
        assertThat(principal.getPassword()).isEqualTo(user.getPassword());
        assertThat(principal.getName()).isEqualTo(user.getName());
    }

    @Test
    void getUsername_ReturnsEmail() {
        UserPrincipal principal = TestDataFactory.createUserPrincipal();

        String username = principal.getUsername();

        assertThat(username).isEqualTo(principal.getEmail());
        assertThat(username).isEqualTo(TestDataFactory.DEFAULT_EMAIL);
    }

    @Test
    void getAuthorities_Empty() {
        UserPrincipal principal = TestDataFactory.createUserPrincipal();

        assertThat(principal.getAuthorities()).isEmpty();
    }

    @Test
    void accountFlags_AllTrue() {
        UserPrincipal principal = TestDataFactory.createUserPrincipal();

        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.isEnabled()).isTrue();
    }

    @Test
    void constructor_SetsAllFields() {
        Long id = 42L;
        String email = "custom@example.com";
        String password = "hashedPassword";
        String name = "Custom Name";

        UserPrincipal principal = new UserPrincipal(id, email, password, name);

        assertThat(principal.getId()).isEqualTo(id);
        assertThat(principal.getEmail()).isEqualTo(email);
        assertThat(principal.getPassword()).isEqualTo(password);
        assertThat(principal.getName()).isEqualTo(name);
    }

    @Test
    void getId_ReturnsCorrectValue() {
        Long expectedId = 123L;
        UserPrincipal principal = new UserPrincipal(expectedId, "test@example.com", "pass", "Test");

        assertThat(principal.getId()).isEqualTo(expectedId);
    }

    @Test
    void getEmail_ReturnsCorrectValue() {
        String expectedEmail = "specific@example.com";
        UserPrincipal principal = new UserPrincipal(1L, expectedEmail, "pass", "Test");

        assertThat(principal.getEmail()).isEqualTo(expectedEmail);
    }

    @Test
    void getName_ReturnsCorrectValue() {
        String expectedName = "Specific Name";
        UserPrincipal principal = new UserPrincipal(1L, "test@example.com", "pass", expectedName);

        assertThat(principal.getName()).isEqualTo(expectedName);
    }
}
