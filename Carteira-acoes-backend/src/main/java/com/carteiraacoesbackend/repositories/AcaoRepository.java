package com.carteiraacoesbackend.repositories;

import com.carteiraacoesbackend.domains.Acao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AcaoRepository extends JpaRepository<Acao, Long> {

  Optional<Acao> findByTickerIgnoreCase(String ticker);

  boolean existsByTickerIgnoreCase(String ticker);

  @Query("select count(o) > 0 from Operacao o where o.acao.id = :acaoId")
  boolean existsOperacoesById(@Param("acaoId") Long acaoId);
}
