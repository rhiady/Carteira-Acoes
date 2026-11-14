package com.carteiraacoesbackend.security;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.json.JsonMapper;

class RestSecurityHandlersTest {

  private final JsonMapper objectMapper = JsonMapper.builder().build();

  @Test
  void authenticationFailureReturnsSafeUnauthorizedError() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuarios");
    MockHttpServletResponse response = new MockHttpServletResponse();

    new RestAuthenticationEntryPoint(objectMapper)
        .commence(request, response, new BadCredentialsException("internal token detail"));

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    assertThat(response.getContentType()).isEqualTo("application/json");
    assertThat(response.getContentAsString()).contains("\"error\":\"UNAUTHORIZED\"");
    assertThat(response.getContentAsString()).contains("\"path\":\"/api/usuarios\"");
    assertThat(response.getContentAsString()).doesNotContain("internal token detail");
  }

  @Test
  void accessDeniedReturnsSafeForbiddenError() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuarios");
    MockHttpServletResponse response = new MockHttpServletResponse();

    new RestAccessDeniedHandler(objectMapper)
        .handle(request, response, new AccessDeniedException("internal permission detail"));

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
    assertThat(response.getContentType()).isEqualTo("application/json");
    assertThat(response.getContentAsString()).contains("\"error\":\"FORBIDDEN\"");
    assertThat(response.getContentAsString()).contains("\"path\":\"/api/usuarios\"");
    assertThat(response.getContentAsString()).doesNotContain("internal permission detail");
  }
}
