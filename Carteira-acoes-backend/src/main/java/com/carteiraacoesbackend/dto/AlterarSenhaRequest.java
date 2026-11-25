package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.validation.PasswordPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(
    @NotBlank @Size(min = 8, max = 72) @Pattern(regexp = PasswordPolicy.PATTERN) String password) {}
