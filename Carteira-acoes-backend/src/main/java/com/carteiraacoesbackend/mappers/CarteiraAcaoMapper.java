package com.carteiraacoesbackend.mappers;

import com.carteiraacoesbackend.domains.CarteiraAcao;
import com.carteiraacoesbackend.dto.CarteiraAcaoResponse;
import org.springframework.stereotype.Component;

@Component
public class CarteiraAcaoMapper {

  public CarteiraAcaoResponse toResponse(CarteiraAcao posicao) {
    return new CarteiraAcaoResponse(
        posicao.getId(),
        posicao.getCarteira().getId(),
        posicao.getAcao().getId(),
        posicao.getAcao().getTicker(),
        posicao.getQuantidade(),
        posicao.getVersion());
  }
}
