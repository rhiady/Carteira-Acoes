package com.carteiraacoesbackend.dto;

import java.time.OffsetDateTime;

public record CorretoraResponse(
    Long id,
    String cnpj,
    String razaoSocial,
    String nomeFantasia,
    String email,
    String telefone,
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String uf,
    String situacaoCadastral,
    String registroCvm,
    OffsetDateTime dataValidacaoCvm,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
