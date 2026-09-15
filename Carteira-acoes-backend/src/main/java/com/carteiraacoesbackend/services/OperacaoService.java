package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.CarteiraAcao;
import com.carteiraacoesbackend.domains.Operacao;
import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import com.carteiraacoesbackend.dto.CompraRequest;
import com.carteiraacoesbackend.dto.OperacaoResponse;
import com.carteiraacoesbackend.dto.VendaRequest;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.mappers.OperacaoMapper;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OperacaoService {
  private static final Map<String, String> SORT_FIELDS =
      Map.ofEntries(
          Map.entry("id", "id"),
          Map.entry("ticker", "acao.ticker"),
          Map.entry("tipo", "tipo"),
          Map.entry("quantidade", "quantidade"),
          Map.entry("precoUnitario", "precoUnitario"),
          Map.entry("valorBruto", "valorBruto"),
          Map.entry("valorLiquido", "valorLiquido"),
          Map.entry("dataHora", "dataHora"),
          Map.entry("createdAt", "createdAt"));
  private static final Sort DEFAULT_SORT =
      Sort.by(Sort.Order.desc("dataHora"), Sort.Order.desc("id"));
  private final OperacaoRepository operacoes;
  private final CarteiraAcaoRepository posicoes;
  private final CarteiraService carteiraService;
  private final AcaoService acaoService;
  private final CotacaoAdapter cotacaoAdapter;
  private final OperacaoMapper mapper;
  private final CurrentUserService currentUser;

  @Autowired
  public OperacaoService(
      OperacaoRepository operacoes,
      CarteiraAcaoRepository posicoes,
      CarteiraService carteiraService,
      AcaoService acaoService,
      CotacaoAdapter cotacaoAdapter,
      OperacaoMapper mapper,
      CurrentUserService currentUser) {
    this.operacoes = operacoes;
    this.posicoes = posicoes;
    this.carteiraService = carteiraService;
    this.acaoService = acaoService;
    this.cotacaoAdapter = cotacaoAdapter;
    this.mapper = mapper;
    this.currentUser = currentUser;
  }

  public OperacaoService(
      OperacaoRepository operacoes,
      CarteiraAcaoRepository posicoes,
      CarteiraService carteiraService,
      AcaoService acaoService,
      CotacaoAdapter cotacaoAdapter,
      OperacaoMapper mapper) {
    this(operacoes, posicoes, carteiraService, acaoService, cotacaoAdapter, mapper, null);
  }

  @Transactional
  public OperacaoResponse comprar(CompraRequest request) {
    Carteira carteira = carteiraService.obterEntidade(request.carteiraId());
    validarOwnership(carteira);
    if (!request.isStockIdentifierValid()) {
      throw ApiException.unprocessable(
          "IDENTIFICACAO_ACAO_INVALIDA", "Informe acaoId ou ticker e mercado.");
    }
    Acao acao =
        request.acaoId() != null
            ? acaoService.obterEntidade(request.acaoId())
            : acaoService.resolverOuCriar(request.ticker(), request.mercado());
    BigDecimal preco =
        request.precoUnitario() != null ? request.precoUnitario() : consultarPrecoAtual(acao);
    CarteiraAcao posicao =
        posicoes
            .findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId())
            .orElseGet(() -> novaPosicao(carteira, acao));
    posicao.setQuantidade(posicao.getQuantidade().add(request.quantidade()));
    posicoes.save(posicao);
    return mapper.toResponse(
        operacoes.save(
            novaOperacao(
                carteira,
                acao,
                TipoOperacao.COMPRA,
                request.quantidade(),
                preco,
                request.corretagem(),
                request.impostos(),
                request.valorAdicional())));
  }

  @Transactional
  public OperacaoResponse vender(VendaRequest request) {
    Carteira carteira = carteiraService.obterEntidade(request.carteiraId());
    validarOwnership(carteira);
    Acao acao = acaoService.obterEntidade(request.acaoId());
    CarteiraAcao posicao =
        posicoes
            .findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId())
            .orElseThrow(
                () ->
                    ApiException.unprocessable(
                        "QUANTIDADE_INSUFICIENTE", "A carteira não possui esta ação."));
    if (posicao.getQuantidade().compareTo(request.quantidade()) < 0) {
      throw ApiException.unprocessable(
          "QUANTIDADE_INSUFICIENTE", "Quantidade de ações insuficiente para a venda.");
    }
    BigDecimal preco =
        request.precoUnitario() != null ? request.precoUnitario() : consultarPrecoAtual(acao);
    posicao.setQuantidade(posicao.getQuantidade().subtract(request.quantidade()));
    posicoes.save(posicao);
    return mapper.toResponse(
        operacoes.save(
            novaOperacao(
                carteira,
                acao,
                TipoOperacao.VENDA,
                request.quantidade(),
                preco,
                request.corretagem(),
                request.impostos(),
                request.valorAdicional())));
  }

  public OperacaoResponse buscarPorId(Long id) {
    Operacao operacao =
        operacoes
            .findById(id)
            .orElseThrow(
                () -> ApiException.notFound("OPERACAO_NAO_ENCONTRADA", "Operação não encontrada."));
    validarOwnership(operacao.getCarteira());
    return mapper.toResponse(operacao);
  }

  public Page<OperacaoResponse> historico(Long carteiraId, Pageable pageable) {
    Carteira carteira = carteiraService.obterEntidade(carteiraId);
    validarOwnership(carteira);
    return operacoes
        .findByCarteiraId(
            carteiraId, PageableSupport.translate(pageable, SORT_FIELDS, DEFAULT_SORT))
        .map(mapper::toResponse);
  }

  private void validarOwnership(Carteira carteira) {
    if (currentUser != null && currentUser.isWebRequest()) currentUser.requireOwnership(carteira);
  }

  private CarteiraAcao novaPosicao(Carteira carteira, Acao acao) {
    CarteiraAcao p = new CarteiraAcao();
    p.setCarteira(carteira);
    p.setAcao(acao);
    p.setQuantidade(BigDecimal.ZERO);
    return p;
  }

  private BigDecimal consultarPrecoAtual(Acao acao) {
    com.carteiraacoesbackend.integrations.Cotacao cotacao =
        cotacaoAdapter.consultar(acao.getTicker(), acao.getMercado());
    AcaoService.validarCotacao(cotacao);
    return cotacao.preco();
  }

  private Operacao novaOperacao(
      Carteira carteira,
      Acao acao,
      TipoOperacao tipo,
      BigDecimal quantidade,
      BigDecimal preco,
      BigDecimal corretagem,
      BigDecimal impostos,
      BigDecimal adicional) {
    Operacao op = new Operacao();
    op.setCarteira(carteira);
    op.setAcao(acao);
    op.setTipo(tipo);
    op.setQuantidade(quantidade);
    op.setPrecoUnitario(preco);
    op.setCorretagem(corretagem);
    op.setImpostos(impostos);
    op.setValorAdicional(adicional);
    return op;
  }
}
