package com.carteiraacoesbackend.repositories;

import com.carteiraacoesbackend.domains.CarteiraAcao;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteiraAcaoRepository extends JpaRepository<CarteiraAcao, Long> {

  boolean existsByCarteiraId(Long carteiraId);

  boolean existsByAcaoId(Long acaoId);

  Optional<CarteiraAcao> findByCarteiraIdAndAcaoId(Long carteiraId, Long acaoId);

  @EntityGraph(attributePaths = "acao")
  Page<CarteiraAcao> findByCarteiraIdAndQuantidadeGreaterThan(
      Long carteiraId, BigDecimal quantidade, Pageable pageable);

  @EntityGraph(attributePaths = "acao")
  List<CarteiraAcao> findByCarteiraIdAndQuantidadeGreaterThan(
      Long carteiraId, BigDecimal quantidade);
}
