package com.carteiraacoesbackend.clients;

import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "brasilApi", url = "${BRASIL_API_URL:https://brasilapi.com.br/api}")
public interface BrasilApiClient {
  @GetMapping("/cnpj/v1/{cnpj}")
  BrasilApiCnpjResponse consultarCnpj(@PathVariable String cnpj);

  @GetMapping("/cep/v2/{cep}")
  BrasilApiCepResponse consultarCep(@PathVariable String cep);

  @GetMapping("/cvm/corretoras/v1")
  List<BrasilApiCvmBrokerResponse> listarCorretorasCvm();

  @GetMapping("/cvm/corretoras/v1/{cnpj}")
  BrasilApiCvmBrokerResponse consultarCorretoraCvm(@PathVariable String cnpj);
}
