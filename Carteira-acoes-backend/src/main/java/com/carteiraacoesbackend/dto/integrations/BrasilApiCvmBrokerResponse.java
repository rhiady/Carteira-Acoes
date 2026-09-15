package com.carteiraacoesbackend.dto.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BrasilApiCvmBrokerResponse(
    String cnpj, @JsonProperty("codigo_cvm") String codigoCvm, String status) {}
