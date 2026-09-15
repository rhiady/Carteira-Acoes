package com.carteiraacoesbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CarteiraAcoesBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(CarteiraAcoesBackendApplication.class, args);
  }
}
