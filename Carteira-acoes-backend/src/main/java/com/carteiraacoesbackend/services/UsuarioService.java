package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.dto.AlterarSenhaRequest;
import com.carteiraacoesbackend.dto.AlterarPapelRequest;
import com.carteiraacoesbackend.dto.CadastroRequest;
import com.carteiraacoesbackend.dto.UsuarioCreateRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import com.carteiraacoesbackend.dto.UsuarioUpdateRequest;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.mappers.UsuarioMapper;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

  @Autowired
  public UsuarioService(
      UsuarioRepository repository,
      UsuarioMapper mapper,
      PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
    this(repository, mapper, new BCryptPasswordEncoder());
  }

  @Transactional
  public UsuarioResponse criar(UsuarioCreateRequest request) {
    String login = request.login().trim().toLowerCase();
    if (repository.existsByEmail(login)) {
      throw ApiException.conflict("EMAIL_DUPLICADO", "Já existe um usuário com este e-mail.");
    }
    Usuario usuario = mapper.toEntity(request, passwordEncoder.encode(request.password()));
    return mapper.toResponse(repository.save(usuario));
  }

  @Transactional
  public UsuarioResponse cadastrar(CadastroRequest request) {
    String email = request.email().trim().toLowerCase();
    if (repository.existsByEmail(email)) {
      throw ApiException.conflict("EMAIL_DUPLICADO", "Já existe um usuário com este e-mail.");
    }
    Usuario usuario = new Usuario();
    usuario.setNome(request.nome().trim());
    usuario.setEmail(email);
    usuario.setSenha(passwordEncoder.encode(request.senha()));
    usuario.setPapel(Papel.USER);
    usuario.setAtivo(true);
    return mapper.toResponse(repository.save(usuario));
  }

  public UsuarioResponse buscarPorId(Long id) {
    return mapper.toResponse(obterEntidade(id));
  }

  @Transactional
  public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest request) {
    Usuario usuario = obterEntidade(id);
    String login = request.login().trim().toLowerCase();
    if (repository.existsByEmailAndIdNot(login, id)) {
      throw ApiException.conflict("EMAIL_DUPLICADO", "Já existe um usuário com este e-mail.");
    }
    usuario.setNome(login);
    usuario.setEmail(login);
    usuario.setAtivo(request.ativo());
    return mapper.toResponse(repository.save(usuario));
  }

  @Transactional
  public UsuarioResponse alterarSenha(Long id, AlterarSenhaRequest request) {
    Usuario usuario = obterEntidade(id);
    usuario.setSenha(passwordEncoder.encode(request.password()));
    return mapper.toResponse(repository.save(usuario));
  }

  @Transactional
  public UsuarioResponse alterarPapel(Long id, AlterarPapelRequest request) {
    if (!isAdmin()) {
      throw ApiException.forbidden("PAPEL_NAO_PERMITIDO", "Somente administradores podem alterar o papel.");
    }
    Usuario usuario = obterEntidade(id);
    usuario.setPapel(request.papel());
    return mapper.toResponse(repository.save(usuario));
  }

  public Page<UsuarioResponse> listar(Pageable pageable) {
    pageable = PageableSupport.translate(pageable, SORT_FIELDS, DEFAULT_SORT);
    return repository.findAll(pageable).map(mapper::toResponse);
  }

  private boolean isAdmin() {
    var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.getAuthorities().stream()
        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
  }

  public Usuario obterEntidade(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> ApiException.notFound("USUARIO_NAO_ENCONTRADO", "Usuário não encontrado."));
  }
}
