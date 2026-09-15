package com.carteiraacoesbackend.dto.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BrasilApiCnpjResponse(
    @JsonProperty("razao_social") String razaoSocial,
    @JsonProperty("nome_fantasia") String nomeFantasia,
    String email,
    @JsonProperty("ddd_telefone_1") String telefone,
    @JsonProperty("descricao_situacao_cadastral") String situacaoCadastral,
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    @JsonProperty("municipio") String cidade,
    String uf) {}
