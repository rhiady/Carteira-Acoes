package com.carteiraacoesbackend.config;

import com.carteiraacoesbackend.dto.ErroResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      SecurityContextRepository securityContextRepository,
      JsonMapper objectMapper)
      throws Exception {
    CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
    http.csrf(
            csrf ->
                csrf.csrfTokenRepository(csrfRepository)
                    .csrfTokenRequestHandler(csrfHandler)
                    .ignoringRequestMatchers(
                        request ->
                            "POST".equals(request.getMethod())
                                && ("/auth/login".equals(request.getRequestURI())
                                    || "/usuarios".equals(request.getRequestURI()))))
        .cors(cors -> {})
        .securityContext(security -> security.securityContextRepository(securityContextRepository))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/auth/login", "/usuarios")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/acoes", "/acoes/atualizar-cotacoes")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/acoes/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/corretoras")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/corretoras/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/acoes/**", "/corretoras/**")
                    .permitAll()
                    .requestMatchers(
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) ->
                            writeError(
                                objectMapper,
                                response,
                                401,
                                "UNAUTHORIZED",
                                "Usuário não autenticado.",
                                request.getRequestURI()))
                    .accessDeniedHandler(
                        (request, response, exception) ->
                            writeError(
                                objectMapper,
                                response,
                                403,
                                "FORBIDDEN",
                                "Acesso negado.",
                                request.getRequestURI())))
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
    return http.build();
  }

  private void writeError(
      JsonMapper objectMapper,
      HttpServletResponse response,
      int status,
      String error,
      String message,
      String path)
      throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
        response.getWriter(),
        new ErroResponse(OffsetDateTime.now(ZoneOffset.UTC), status, error, message, path));
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
