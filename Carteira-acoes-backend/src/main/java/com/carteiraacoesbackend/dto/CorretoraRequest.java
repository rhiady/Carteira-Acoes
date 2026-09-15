package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CorretoraRequest(
    @NotBlank
        @Pattern(
            regexp = CNPJ_PATTERN,
            message = "CNPJ deve conter 14 dígitos, com ou sem máscara.")
        String cnpj) {
  public static final String CNPJ_PATTERN = "(?:\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})";
}
