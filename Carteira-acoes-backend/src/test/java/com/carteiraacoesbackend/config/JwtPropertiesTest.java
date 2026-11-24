package com.carteiraacoesbackend.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtPropertiesTest {

  @Test
  void rejectsShortSecret() {
    assertThatThrownBy(() -> new JwtProperties("short", Duration.ofMinutes(1), "issuer"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new JwtProperties(null, Duration.ofMinutes(1), "issuer"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsNonPositiveExpiration() {
    assertThatThrownBy(
            () ->
                new JwtProperties("secret-with-at-least-32-bytes-123456", Duration.ZERO, "issuer"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(
            () -> new JwtProperties("secret-with-at-least-32-bytes-123456", null, "issuer"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsBlankIssuer() {
    assertThatThrownBy(
            () ->
                new JwtProperties(
                    "secret-with-at-least-32-bytes-123456", Duration.ofMinutes(1), " "))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(
            () ->
                new JwtProperties(
                    "secret-with-at-least-32-bytes-123456", Duration.ofMinutes(1), null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
