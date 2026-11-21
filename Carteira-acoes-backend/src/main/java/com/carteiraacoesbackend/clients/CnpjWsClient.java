package com.carteiraacoesbackend.clients;

import com.carteiraacoesbackend.dto.integrations.CnpjWsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cnpjWs", url = "${CNPJ_WS_URL:https://publica.cnpj.ws}")
public interface CnpjWsClient {
  @GetMapping("/cnpj/{cnpj}")
  CnpjWsResponse consultarCnpj(@PathVariable String cnpj);
}
