package com.carteiraacoesbackend.config;

import com.carteiraacoesbackend.security.RestAccessDeniedHandler;
import com.carteiraacoesbackend.security.RestAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  SecretKey jwtSecretKey(JwtProperties properties) {
    return new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }

  @Bean
  JwtEncoder jwtEncoder(SecretKey secretKey) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
  }

  @Bean
  JwtDecoder jwtDecoder(SecretKey secretKey, JwtProperties properties) {
    NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuer()));
    return decoder;
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          List<String> authorities = jwt.getClaimAsStringList("authorities");
          return (authorities == null ? List.<String>of() : authorities).stream()
                .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                .toList();
        });
    return converter;
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationConverter jwtAuthenticationConverter,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler)
      throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> {})
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/api/auth/login")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/usuarios")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/papel")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/acoes", "/api/acoes/atualizar-cotacoes")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/acoes/*/cotacao")
                        .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/acoes/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/corretoras")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/corretoras/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/acoes/**", "/api/corretoras/**")
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
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
