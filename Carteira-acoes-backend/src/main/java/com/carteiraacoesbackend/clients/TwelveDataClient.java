package com.carteiraacoesbackend.clients;

import com.carteiraacoesbackend.dto.integrations.TwelveDataQuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "twelveData", url = "${TWELVE_DATA_URL:https://api.twelvedata.com}")
public interface TwelveDataClient {

  @GetMapping("/quote")
  TwelveDataQuoteResponse consultar(
      @RequestParam("symbol") String ticker, @RequestHeader("Authorization") String authorization);
}
