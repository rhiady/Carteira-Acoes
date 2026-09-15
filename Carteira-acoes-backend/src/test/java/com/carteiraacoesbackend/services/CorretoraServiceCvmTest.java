package com.carteiraacoesbackend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.CorretoraRequest;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.facades.CnpjFacade;
import com.carteiraacoesbackend.facades.CvmFacade;
import com.carteiraacoesbackend.mappers.CorretoraMapper;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CorretoraServiceCvmTest {

  @Autowired private CorretoraRepository repository;

  @BeforeEach
  void limparCorretorasDeTeste() {
    repository.deleteAll();
  }

  @Test
  void doesNotPersistBrokerWithoutValidCvmRegistration() {
    BrasilApiClient client = new FakeBrasilApiClient(null);
    CorretoraService service =
        new CorretoraService(
            repository, new CnpjFacade(client), new CvmFacade(client), new CorretoraMapper());

    ApiException exception =
        assertThrows(
            ApiException.class, () -> service.criar(new CorretoraRequest("12.345.678/0001-90")));

    assertEquals("CORRETORA_NAO_REGISTRADA_CVM", exception.getCode());
    assertEquals(0, repository.count());
  }

  @Test
  void importsBrokerAddressFromTheCnpjResponse() {
    BrasilApiClient client =
        new FakeBrasilApiClient(new BrasilApiCvmBrokerResponse("12345678000190", "1234", "ATIVA"));
    CorretoraService service =
        new CorretoraService(
            repository, new CnpjFacade(client), new CvmFacade(client), new CorretoraMapper());

    var response = service.criar(new CorretoraRequest("12.345.678/0001-90"));

    assertEquals("01001000", response.cep());
    assertEquals("Rua", response.logradouro());
    assertEquals("100", response.numero());
    assertEquals("São Paulo", response.cidade());
  }

  @Test
  void persistsBrokerWhenOnlyRequiredDataIsAvailable() {
    BrasilApiCnpjResponse empresaComApenasRazaoSocial =
        new BrasilApiCnpjResponse(
            "Empresa", null, null, null, null, null, null, null, null, null, null, null);
    BrasilApiClient client =
        new FakeBrasilApiClient(
            new BrasilApiCvmBrokerResponse("12345678000190", null, "ATIVA"),
            empresaComApenasRazaoSocial);
    CorretoraService service =
        new CorretoraService(
            repository, new CnpjFacade(client), new CvmFacade(client), new CorretoraMapper());

    var response = service.criar(new CorretoraRequest("12.345.678/0001-90"));

    assertNull(response.email());
    assertNull(response.nomeFantasia());
    assertNull(response.telefone());
    assertNull(response.situacaoCadastral());
    assertNull(response.cep());
    assertNull(response.logradouro());
    assertNull(response.numero());
    assertNull(response.bairro());
    assertNull(response.cidade());
    assertNull(response.uf());
    assertNull(response.registroCvm());
  }

  @Test
  void doesNotPersistBrokerWithoutRazaoSocial() {
    BrasilApiCnpjResponse empresaSemRazaoSocial =
        new BrasilApiCnpjResponse(
            " ", null, null, null, null, null, null, null, null, null, null, null);
    BrasilApiClient client =
        new FakeBrasilApiClient(
            new BrasilApiCvmBrokerResponse("12345678000190", "1234", "ATIVA"),
            empresaSemRazaoSocial);
    CorretoraService service =
        new CorretoraService(
            repository, new CnpjFacade(client), new CvmFacade(client), new CorretoraMapper());

    ApiException exception =
        assertThrows(
            ApiException.class, () -> service.criar(new CorretoraRequest("12.345.678/0001-90")));

    assertEquals("DADOS_EXTERNOS_INCOMPLETOS", exception.getCode());
    assertEquals(0, repository.count());
  }

  private static final class FakeBrasilApiClient implements BrasilApiClient {
    private final BrasilApiCvmBrokerResponse registroCvm;
    private final BrasilApiCnpjResponse empresa;

    private FakeBrasilApiClient(BrasilApiCvmBrokerResponse registroCvm) {
      this(
          registroCvm,
          new BrasilApiCnpjResponse(
              "Empresa",
              "Empresa",
              "email@test.com",
              "11999999999",
              "ATIVA",
              "01001000",
              "Rua",
              "100",
              null,
              "Centro",
              "São Paulo",
              "SP"));
    }

    private FakeBrasilApiClient(
        BrasilApiCvmBrokerResponse registroCvm, BrasilApiCnpjResponse empresa) {
      this.registroCvm = registroCvm;
      this.empresa = empresa;
    }

    @Override
    public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
      return empresa;
    }

    @Override
    public BrasilApiCepResponse consultarCep(String cep) {
      return new BrasilApiCepResponse("Rua", "Centro", "São Paulo", "SP");
    }

    @Override
    public List<BrasilApiCvmBrokerResponse> listarCorretorasCvm() {
      throw new UnsupportedOperationException();
    }

    @Override
    public BrasilApiCvmBrokerResponse consultarCorretoraCvm(String cnpj) {
      if (registroCvm == null)
        throw ApiException.notFound(
            "EXTERNAL_RESOURCE_NOT_FOUND", "Recurso não encontrado no provedor externo.");
      return registroCvm;
    }
  }
}
