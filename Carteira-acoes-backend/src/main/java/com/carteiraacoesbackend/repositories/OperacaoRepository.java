package com.carteiraacoesbackend.repositories;

import com.carteiraacoesbackend.domains.Operacao;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {

  boolean existsByCarteiraId(Long carteiraId);

  boolean existsByAcaoId(Long acaoId);

  @EntityGraph(attributePaths = "acao")
  Page<Operacao> findByCarteiraId(Long carteiraId, Pageable pageable);

  Page<Operacao> findByCarteiraIdOrderByDataHoraDesc(Long carteiraId, Pageable pageable);

  List<Operacao> findByCarteiraIdAndAcaoIdInOrderByDataHoraAscIdAsc(
      Long carteiraId, Collection<Long> acoesIds);
}
