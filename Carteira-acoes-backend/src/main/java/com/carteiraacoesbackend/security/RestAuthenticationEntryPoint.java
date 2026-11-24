package com.carteiraacoesbackend.security;

import com.carteiraacoesbackend.dto.ErroResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final JsonMapper objectMapper;

  public RestAuthenticationEntryPoint(JsonMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ErroResponse error =
        new ErroResponse(
            OffsetDateTime.now(ZoneOffset.UTC),
            HttpServletResponse.SC_UNAUTHORIZED,
            "UNAUTHORIZED",
            "Usuário não autenticado.",
            request.getRequestURI());

    objectMapper.writeValue(response.getOutputStream(), error);
  }
}
