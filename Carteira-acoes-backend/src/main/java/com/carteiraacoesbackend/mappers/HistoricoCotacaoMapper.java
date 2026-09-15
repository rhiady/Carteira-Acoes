package com.carteiraacoesbackend.mappers;

import com.carteiraacoesbackend.domains.HistoricoCotacao;
import com.carteiraacoesbackend.dto.HistoricoCotacaoResponse;
import org.springframework.stereotype.Component;

@Component
public class HistoricoCotacaoMapper {

  public HistoricoCotacaoResponse toResponse(HistoricoCotacao historico) {
    return new HistoricoCotacaoResponse(
        historico.getId(),
        historico.getAcao().getId(),
        historico.getCotacao(),
        historico.getDataHora());
  }
}
