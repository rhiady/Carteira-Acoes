package com.carteiraacoesbackend.integrations;

import com.carteiraacoesbackend.domains.enums.Mercado;

public interface CotacaoAdapter {

  Cotacao consultar(String ticker, Mercado mercado);
}
