package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.carteiraacoesbackend.domains.*;
import com.carteiraacoesbackend.dto.AtualizarCarteiraRequest;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.mappers.*;
import com.carteiraacoesbackend.repositories.*;
import org.junit.jupiter.api.Test;

class ResourceMaintenanceServiceTest {

  @Test
  void atualizaSomenteNomeDaCarteira() {
    CarteiraRepository repo = mock(CarteiraRepository.class);
    Carteira carteira = new Carteira();
    carteira.setNome("Antiga");
    when(repo.findById(1L)).thenReturn(java.util.Optional.of(carteira));
    CarteiraMapper mapper = mock(CarteiraMapper.class);
    CarteiraService service =
        carteiraService(
            repo, mock(CarteiraAcaoRepository.class), mock(OperacaoRepository.class), mapper);

    service.atualizar(1L, new AtualizarCarteiraRequest("  Nova  "));

    org.assertj.core.api.Assertions.assertThat(carteira.getNome()).isEqualTo("Nova");
    verify(repo).save(carteira);
  }

  @Test
  void bloqueiaExclusaoDeCarteiraVinculada() {
    CarteiraRepository repo = mock(CarteiraRepository.class);
    when(repo.findById(1L)).thenReturn(java.util.Optional.of(new Carteira()));
    OperacaoRepository operacoes = mock(OperacaoRepository.class);
    when(operacoes.existsByCarteiraId(1L)).thenReturn(true);
    CarteiraService service =
        carteiraService(
            repo, mock(CarteiraAcaoRepository.class), operacoes, mock(CarteiraMapper.class));

    assertThatThrownBy(() -> service.excluir(1L))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("operações ou posições");
    verify(repo, never()).delete(any());
  }

  @Test
  void bloqueiaExclusaoDeAcaoComHistorico() {
    AcaoRepository repo = mock(AcaoRepository.class);
    when(repo.findById(1L)).thenReturn(java.util.Optional.of(new Acao()));
    when(repo.existsOperacoesById(1L)).thenReturn(false);
    HistoricoCotacaoRepository historicos = mock(HistoricoCotacaoRepository.class);
    when(historicos.existsByAcaoId(1L)).thenReturn(true);
    AcaoService service =
        new AcaoService(
            repo,
            historicos,
            mock(CarteiraAcaoRepository.class),
            mock(CotacaoAdapter.class),
            mock(CotacaoAtualizacaoService.class),
            mock(AcaoMapper.class),
            mock(CurrencyConversionService.class),
            mock(HistoricoCotacaoMapper.class));

    assertThatThrownBy(() -> service.excluir(1L))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("histórico de cotações");
    verify(repo, never()).delete(any());
  }

  private CarteiraService carteiraService(
      CarteiraRepository repo,
      CarteiraAcaoRepository posicoes,
      OperacaoRepository operacoes,
      CarteiraMapper mapper) {
    return new CarteiraService(
        repo,
        posicoes,
        mock(CorretoraRepository.class),
        operacoes,
        mock(UsuarioService.class),
        mapper,
        mock(PosicaoFinanceiraCalculator.class),
        mock(CurrencyConversionService.class));
  }
}
