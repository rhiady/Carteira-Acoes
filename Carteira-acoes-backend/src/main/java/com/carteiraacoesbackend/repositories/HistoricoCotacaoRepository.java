package com.carteiraacoesbackend.repositories;

import com.carteiraacoesbackend.domains.HistoricoCotacao;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoCotacaoRepository extends JpaRepository<HistoricoCotacao, Long> {

  boolean existsByAcaoId(Long acaoId);

  Page<HistoricoCotacao> findByAcaoIdAndDataHoraGreaterThanEqualAndDataHoraLessThanEqual(
      Long acaoId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable);

  Page<HistoricoCotacao> findByAcaoIdAndDataHoraGreaterThanEqual(
      Long acaoId, OffsetDateTime inicio, Pageable pageable);

  Page<HistoricoCotacao> findByAcaoIdAndDataHoraLessThanEqual(
      Long acaoId, OffsetDateTime fim, Pageable pageable);

  Page<HistoricoCotacao> findByAcaoId(Long acaoId, Pageable pageable);
}
