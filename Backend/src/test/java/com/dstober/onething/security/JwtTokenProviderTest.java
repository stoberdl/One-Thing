package com.dstober.onething.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET = "testSecretKeyForJwtTokenGenerationAtLeast32Bytes";
    private static final long EXPIRATION = 86400000L; // 24 hours

    private JwtTokenProvider tokenProvider;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(SECRET, EXPIRATION);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void generateToken_Success() {
        Long userId = 1L;
        String email = "test@example.com";

        String token = tokenProvider.generateToken(userId, email);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    void generateToken_ContainsClaims() {
        Long userId = 42L;
        String email = "user@example.com";

        String token = tokenProvider.generateToken(userId, email);

        Long extractedUserId = tokenProvider.getUserIdFromToken(token);
        String extractedEmail = tokenProvider.getEmailFromToken(token);

        assertThat(extractedUserId).isEqualTo(userId);
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void getUserIdFromToken_Valid() {
        Long userId = 123L;
        String email = "test@example.com";
        String token = tokenProvider.generateToken(userId, email);

        Long result = tokenProvider.getUserIdFromToken(token);

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void getUserIdFromToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void getUserIdFromToken_MissingClaim() {
        // Create a token without userId claim
        String tokenWithoutUserId = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();

        assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(tokenWithoutUserId))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Token missing required userId claim");
    }

    @Test
    void getEmailFromToken_Valid() {
        Long userId = 1L;
        String email = "test@example.com";
        String token = tokenProvider.generateToken(userId, email);

        String result = tokenProvider.getEmailFromToken(token);

        assertThat(result).isEqualTo(email);
    }

    @Test
    void validateToken_Valid() {
        String token = tokenProvider.generateToken(1L, "test@example.com");

        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    void validateToken_Expired() {
        // Create a provider with 0ms expiration
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(SECRET, 0L);
        String token = shortLivedProvider.generateToken(1L, "test@example.com");

        // Token should be expired immediately
        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isFalse();
    }

    @Test
    void validateToken_WrongSignature() {
        // Create token with different secret
        String differentSecret = "differentSecretKeyForJwtTokenGenerationAtLeast32Bytes";
        JwtTokenProvider otherProvider = new JwtTokenProvider(differentSecret, EXPIRATION);
        String tokenWithDifferentKey = otherProvider.generateToken(1L, "test@example.com");

        boolean result = tokenProvider.validateToken(tokenWithDifferentKey);

        assertThat(result).isFalse();
    }

    @Test
    void validateToken_Malformed() {
        String malformedToken = "not.a.valid.jwt.token";

        boolean result = tokenProvider.validateToken(malformedToken);

        assertThat(result).isFalse();
    }

    @Test
    void validateToken_EmptyString() {
        boolean result = tokenProvider.validateToken("");

        assertThat(result).isFalse();
    }

    @Test
    void validateToken_Null_ReturnsFalse() {
        // The validateToken method catches IllegalArgumentException and returns false
        boolean result = tokenProvider.validateToken(null);

        assertThat(result).isFalse();
    }
}
