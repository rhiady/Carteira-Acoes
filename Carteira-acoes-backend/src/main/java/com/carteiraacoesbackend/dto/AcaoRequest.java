package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Mercado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AcaoRequest(
    @NotBlank
        @Pattern(
            regexp = TICKER_PATTERN,
            message = "Ticker deve conter de 1 a 15 letras, números, pontos ou hífens.")
        String ticker,
    @NotNull Mercado mercado) {
  public static final String TICKER_PATTERN = "[A-Za-z0-9][A-Za-z0-9.-]{0,14}";
}
