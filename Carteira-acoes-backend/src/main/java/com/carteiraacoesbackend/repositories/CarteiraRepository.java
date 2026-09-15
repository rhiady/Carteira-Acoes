package com.carteiraacoesbackend.repositories;

import com.carteiraacoesbackend.domains.Carteira;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

  boolean existsByCorretoraId(Long corretoraId);

  @Override
  @EntityGraph(attributePaths = {"usuario", "corretora"})
  Page<Carteira> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"usuario", "corretora"})
  Page<Carteira> findByUsuarioId(Long usuarioId, Pageable pageable);
}
