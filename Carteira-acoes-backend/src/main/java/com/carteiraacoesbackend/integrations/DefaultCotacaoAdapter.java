package com.carteiraacoesbackend.integrations;

import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.exceptions.ApiException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DefaultCotacaoAdapter implements CotacaoAdapter {

  private final BrapiCotacaoAdapter brapi;
  private final TwelveDataCotacaoAdapter twelveData;

  public DefaultCotacaoAdapter(BrapiCotacaoAdapter brapi, TwelveDataCotacaoAdapter twelveData) {
    this.brapi = brapi;
    this.twelveData = twelveData;
  }

  @Override
  public Cotacao consultar(String ticker, Mercado mercado) {
    try {
      if (mercado == Mercado.BRASIL) {
        return brapi.consultar(ticker);
      }
      return twelveData.consultar(ticker);
    } catch (ApiException exception) {
      throw exception;
    } catch (Exception exception) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível consultar a cotação externa.");
    }
  }
}
