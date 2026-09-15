package com.carteiraacoesbackend.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.dto.UsuarioRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

  @Test
  void responseNeverContainsPassword() {
    UsuarioMapper mapper = new UsuarioMapper();
    UsuarioResponse response =
        mapper.toResponse(mapper.toEntity(new UsuarioRequest("Ana", "ana@teste.com", "segredo")));

    assertThat(response.nome()).isEqualTo("Ana");
    assertThat(response.email()).isEqualTo("ana@teste.com");
    assertThat(response.getClass().getRecordComponents())
        .extracting(component -> component.getName())
        .doesNotContain("senha");
  }
}
