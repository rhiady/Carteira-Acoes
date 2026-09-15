package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.dto.UsuarioRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import com.carteiraacoesbackend.dto.UsuarioUpdateRequest;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.mappers.UsuarioMapper;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {
  private static final Map<String, String> SORT_FIELDS =
      Map.of(
          "id", "id",
          "nome", "nome",
          "email", "email",
          "createdAt", "createdAt",
          "updatedAt", "updatedAt");
  private static final Sort DEFAULT_SORT = Sort.by("id").ascending();

  private final UsuarioRepository repository;
  private final UsuarioMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final CurrentUserService currentUser;

  @Autowired
  public UsuarioService(
      UsuarioRepository repository,
      UsuarioMapper mapper,
      PasswordEncoder passwordEncoder,
      CurrentUserService currentUser) {
    this.repository = repository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
    this.currentUser = currentUser;
  }

  public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
    this(repository, mapper, new BCryptPasswordEncoder(), null);
  }

  @Transactional
  public UsuarioResponse criar(UsuarioRequest request) {
    String email = request.email().trim().toLowerCase();
    if (repository.existsByEmail(email)) {
      throw ApiException.conflict("EMAIL_DUPLICADO", "Já existe um usuário com este e-mail.");
    }
    Usuario usuario =
        mapper.toEntity(
            new UsuarioRequest(
                request.nome().trim(), email, passwordEncoder.encode(request.senha())));
    return mapper.toResponse(repository.save(usuario));
  }

  public UsuarioResponse buscarPorId(Long id) {
    requireSelfIfWebRequest(id);
    return mapper.toResponse(obterEntidade(id));
  }

  @Transactional
  public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest request) {
    requireSelfIfWebRequest(id);
    Usuario usuario = obterEntidade(id);
    String email = request.email().trim().toLowerCase();
    if (repository.existsByEmailAndIdNot(email, id)) {
      throw ApiException.conflict("EMAIL_DUPLICADO", "Já existe um usuário com este e-mail.");
    }
    usuario.setNome(request.nome().trim());
    usuario.setEmail(email);
    if (request.senha() != null && !request.senha().isBlank()) {
      usuario.setSenha(passwordEncoder.encode(request.senha()));
    }
    return mapper.toResponse(repository.save(usuario));
  }

  public Page<UsuarioResponse> listar(Pageable pageable) {
    pageable = PageableSupport.translate(pageable, SORT_FIELDS, DEFAULT_SORT);
    if (currentUser != null && currentUser.isWebRequest()) {
      Usuario usuario = currentUser.required();
      if (pageable.getOffset() > 0) {
        return new PageImpl<>(java.util.List.of(), pageable, 1);
      }
      return new PageImpl<>(java.util.List.of(mapper.toResponse(usuario)), pageable, 1);
    }
    return repository.findAll(pageable).map(mapper::toResponse);
  }

  private void requireSelfIfWebRequest(Long id) {
    if (currentUser != null && currentUser.isWebRequest()) {
      currentUser.requireUserId(id);
    }
  }

  public Usuario obterEntidade(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> ApiException.notFound("USUARIO_NAO_ENCONTRADO", "Usuário não encontrado."));
  }
}
