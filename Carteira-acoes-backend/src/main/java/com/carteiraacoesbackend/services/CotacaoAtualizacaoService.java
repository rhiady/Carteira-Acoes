package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.HistoricoCotacao;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.HistoricoCotacaoRepository;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CotacaoAtualizacaoService {
  private final AcaoRepository acoes;
  private final HistoricoCotacaoRepository historicos;
  private final CotacaoAdapter cotacaoAdapter;

  public CotacaoAtualizacaoService(
      AcaoRepository acoes, HistoricoCotacaoRepository historicos, CotacaoAdapter cotacaoAdapter) {
    this.acoes = acoes;
    this.historicos = historicos;
    this.cotacaoAdapter = cotacaoAdapter;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Acao atualizar(Long acaoId) {
    Acao acao =
        acoes
            .findById(acaoId)
            .orElseThrow(
                () ->
                    com.carteiraacoesbackend.exceptions.ApiException.notFound(
                        "ACAO_NAO_ENCONTRADA", "Ação não encontrada."));
    Cotacao cotacao = cotacaoAdapter.consultar(acao.getTicker(), acao.getMercado());
    AcaoService.validarCotacao(cotacao);
    java.time.OffsetDateTime novaDataHora =
        cotacao.dataHora().withOffsetSameInstant(ZoneOffset.UTC);
    if (acao.getDataHoraCotacao() != null) {
      java.time.OffsetDateTime dataHoraAtual =
          acao.getDataHoraCotacao().withOffsetSameInstant(ZoneOffset.UTC);
      if (novaDataHora.isBefore(dataHoraAtual)) {
        throw com.carteiraacoesbackend.exceptions.ApiException.external(
            org.springframework.http.HttpStatus.BAD_GATEWAY,
            "EXTERNAL_API_STALE_RESPONSE",
            "O provedor retornou uma cotação anterior à já armazenada.");
      }
      if (novaDataHora.isEqual(dataHoraAtual)) {
        if (acao.getCotacaoAtual() != null
            && acao.getCotacaoAtual().compareTo(cotacao.preco()) == 0) {
          return acao;
        }
        throw com.carteiraacoesbackend.exceptions.ApiException.external(
            org.springframework.http.HttpStatus.BAD_GATEWAY,
            "EXTERNAL_API_INVALID_RESPONSE",
            "O provedor retornou valores divergentes para o mesmo instante.");
      }
    }
    acao.setCotacaoAtual(cotacao.preco());
    acao.setDataHoraCotacao(novaDataHora);
    Acao atualizada = acoes.save(acao);
    registrarHistorico(atualizada, cotacao);
    return atualizada;
  }

  @Transactional
  public void registrarHistorico(Acao acao, Cotacao cotacao) {
    HistoricoCotacao historico = new HistoricoCotacao();
    historico.setAcao(acao);
    historico.setCotacao(cotacao.preco());
    historico.setDataHora(cotacao.dataHora().withOffsetSameInstant(ZoneOffset.UTC));
    historicos.save(historico);
  }
}
