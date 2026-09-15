package com.carteiraacoesbackend.clients;

import com.carteiraacoesbackend.dto.integrations.PtaxQuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ptax", url = "${ptax.url}")
public interface PtaxClient {

  @GetMapping("/CotacaoDolarPeriodo(dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)")
  PtaxQuoteResponse consultarCotacoes(
      @RequestParam("@dataInicial") String dataInicial,
      @RequestParam("@dataFinalCotacao") String dataFinal,
      @RequestParam("$top") int limite,
      @RequestParam("$format") String formato);
}
