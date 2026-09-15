package com.carteiraacoesbackend.mappers;

import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.dto.CarteiraResponse;
import org.springframework.stereotype.Component;

@Component
public class CarteiraMapper {

  public CarteiraResponse toResponse(Carteira carteira) {
    return new CarteiraResponse(
        carteira.getId(),
        carteira.getNome(),
        carteira.getUsuario().getId(),
        carteira.getCorretora().getId(),
        carteira.getCreatedAt(),
        carteira.getUpdatedAt());
  }
}
