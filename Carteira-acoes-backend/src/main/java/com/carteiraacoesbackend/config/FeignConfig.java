package com.carteiraacoesbackend.config;

import com.carteiraacoesbackend.clients.FeignErrorDecoder;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
  @Bean
  ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }

  @Bean
  Request.Options requestOptions() {
    return new Request.Options(
        2_000,
        java.util.concurrent.TimeUnit.MILLISECONDS,
        3_000,
        java.util.concurrent.TimeUnit.MILLISECONDS,
        true);
  }

  @Bean
  Retryer retryer() {
    return new Retryer.Default(100, 500, 2);
  }
}
