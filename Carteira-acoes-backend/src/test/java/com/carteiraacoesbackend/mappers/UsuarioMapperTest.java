package com.carteiraacoesbackend.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.dto.UsuarioCreateRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

  @Test
  void responseNeverContainsPassword() {
    UsuarioMapper mapper = new UsuarioMapper();
    UsuarioResponse response =
        mapper.toResponse(
            mapper.toEntity(
                new UsuarioCreateRequest("ana@teste.com", "Segredo123", Papel.USER), "hash"));

    assertThat(response.login()).isEqualTo("ana@teste.com");
    assertThat(response.papel()).isEqualTo(Papel.USER);
    assertThat(response.ativo()).isTrue();
    assertThat(response.getClass().getRecordComponents())
        .extracting(component -> component.getName())
        .doesNotContain("senha");
  }
}
