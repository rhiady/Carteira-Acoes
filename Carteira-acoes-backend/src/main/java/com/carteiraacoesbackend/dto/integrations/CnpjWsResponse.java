package com.carteiraacoesbackend.dto.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CnpjWsResponse(
    @JsonProperty("razao_social") String razaoSocial, Estabelecimento estabelecimento) {
  public record Estabelecimento(
      @JsonProperty("nome_fantasia") String nomeFantasia,
      String email,
      @JsonProperty("telefone1") String telefone,
      @JsonProperty("situacao_cadastral") String situacaoCadastral,
      String cep,
      String logradouro,
      String numero,
      String complemento,
      String bairro,
      Cidade cidade,
      Estado estado) {}

  public record Cidade(String nome) {}

  public record Estado(String sigla) {}
}
