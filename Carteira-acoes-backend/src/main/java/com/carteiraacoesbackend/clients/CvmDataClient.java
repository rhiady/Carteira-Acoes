package com.carteiraacoesbackend.clients;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "cvmData",
    url = "${CVM_DATA_URL:https://dados.cvm.gov.br/dados/INTERMED/CAD/DADOS}")
public interface CvmDataClient {
  @GetMapping("/cad_intermed.zip")
  Response baixarCadastroIntermediarios();
}
