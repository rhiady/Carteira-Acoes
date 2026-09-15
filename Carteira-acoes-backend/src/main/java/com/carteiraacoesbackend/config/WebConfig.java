package com.carteiraacoesbackend.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final List<String> allowedOrigins;
  private final PaginationRequestInterceptor paginationRequestInterceptor;

  public WebConfig(
      @Value("${app.cors.allowed-origins}") List<String> allowedOrigins,
      PaginationRequestInterceptor paginationRequestInterceptor) {
    this.allowedOrigins = allowedOrigins;
    this.paginationRequestInterceptor = paginationRequestInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(paginationRequestInterceptor);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(allowedOrigins.toArray(String[]::new))
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization")
        .allowCredentials(true)
        .maxAge(3600);
  }
}
