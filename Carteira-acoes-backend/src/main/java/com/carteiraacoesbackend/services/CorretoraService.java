package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.dto.CorretoraRequest;
import com.carteiraacoesbackend.dto.CorretoraResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.facades.CnpjFacade;
import com.carteiraacoesbackend.facades.CvmFacade;
import com.carteiraacoesbackend.mappers.CorretoraMapper;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CorretoraService {
  private static final Map<String, String> SORT_FIELDS =
      Map.ofEntries(
          Map.entry("id", "id"),
          Map.entry("cnpj", "cnpj"),
          Map.entry("razaoSocial", "razaoSocial"),
          Map.entry("nomeFantasia", "nomeFantasia"),
          Map.entry("cidade", "cidade"),
          Map.entry("uf", "uf"),
          Map.entry("createdAt", "createdAt"),
          Map.entry("updatedAt", "updatedAt"));
  private static final Sort DEFAULT_SORT = Sort.by("id").ascending();
  private final CorretoraRepository repository;
  private final CarteiraRepository carteiras;
  private final CnpjFacade cnpjFacade;
  private final CvmFacade cvmFacade;
  private final CorretoraMapper mapper;

  @Autowired
  public CorretoraService(
      CorretoraRepository repository,
      CarteiraRepository carteiras,
      CnpjFacade cnpjFacade,
      CvmFacade cvmFacade,
      CorretoraMapper mapper) {
    this.repository = repository;
    this.carteiras = carteiras;
    this.cnpjFacade = cnpjFacade;
    this.cvmFacade = cvmFacade;
    this.mapper = mapper;
  }

  public CorretoraService(
      CorretoraRepository repository,
      CnpjFacade cnpjFacade,
      CvmFacade cvmFacade,
      CorretoraMapper mapper) {
    this(repository, null, cnpjFacade, cvmFacade, mapper);
  }

  @Transactional
  public CorretoraResponse criar(CorretoraRequest request) {

    String cnpj = request.cnpj().replaceAll("\\D", "");

    if (repository.existsByCnpj(cnpj))
      throw ApiException.conflict("CNPJ_DUPLICADO", "Já existe uma corretora com este CNPJ.");
    BrasilApiCnpjResponse empresa = cnpjFacade.consultar(cnpj);
    BrasilApiCvmBrokerResponse registroCvm = cvmFacade.consultarCorretora(cnpj);
    Corretora c = new Corretora();
    c.setCnpj(cnpj);
    c.setRazaoSocial(obrigatorio(empresa.razaoSocial()));
    c.setNomeFantasia(opcional(empresa.nomeFantasia()));
    c.setEmail(opcional(empresa.email()));
    c.setTelefone(opcional(empresa.telefone()));
    c.setCep(somenteDigitos(opcional(empresa.cep())));
    c.setLogradouro(opcional(empresa.logradouro()));
    c.setNumero(opcional(empresa.numero()));
    c.setComplemento(opcional(empresa.complemento()));
    c.setBairro(opcional(empresa.bairro()));
    c.setCidade(opcional(empresa.cidade()));
    c.setUf(opcional(empresa.uf()));
    c.setSituacaoCadastral(opcional(empresa.situacaoCadastral()));
    c.setRegistroCvm(opcional(registroCvm.codigoCvm()));
    c.setDataValidacaoCvm(OffsetDateTime.now(ZoneOffset.UTC));
    return mapper.toResponse(repository.save(c));
  }

  public CorretoraResponse buscarPorId(Long id) {
    return mapper.toResponse(obter(id));
  }

  @Transactional
  public void excluir(Long id) {
    Corretora corretora = obter(id);
    if (carteiras != null && carteiras.existsByCorretoraId(id)) {
      throw ApiException.conflict(
          "CORRETORA_VINCULADA",
          "Não é possível excluir a corretora porque existem carteiras vinculadas.");
    }
    repository.delete(corretora);
  }

  public CorretoraResponse buscarPorCnpj(String cnpj) {
    String normalizado = cnpj == null ? "" : cnpj.replaceAll("\\D", "");
    if (normalizado.length() != 14) {
      throw ApiException.badRequest("CNPJ_INVALIDO", "CNPJ deve conter 14 dígitos.");
    }
    return mapper.toResponse(
        repository
            .findByCnpj(normalizado)
            .orElseThrow(
                () ->
                    ApiException.notFound(
                        "CORRETORA_NAO_ENCONTRADA", "Corretora não encontrada.")));
  }

  public Page<CorretoraResponse> listar(Pageable pageable) {
    return repository
        .findAll(PageableSupport.translate(pageable, SORT_FIELDS, DEFAULT_SORT))
        .map(mapper::toResponse);
  }

  private Corretora obter(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> ApiException.notFound("CORRETORA_NAO_ENCONTRADA", "Corretora não encontrada."));
  }

  private String obrigatorio(String value) {
    if (value == null || value.isBlank())
      throw ApiException.unprocessable(
          "DADOS_EXTERNOS_INCOMPLETOS", "A validação externa retornou dados incompletos.");
    return value;
  }

  private String opcional(String value) {
    return value == null || value.isBlank() ? null : value;
  }

  private String somenteDigitos(String value) {
    return value == null ? null : value.replaceAll("\\D", "");
  }
}
