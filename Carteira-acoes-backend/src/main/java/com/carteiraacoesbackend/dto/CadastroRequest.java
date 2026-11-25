package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.validation.PasswordPolicy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(min = 8, max = 72) @Pattern(regexp = PasswordPolicy.PATTERN) String senha) {}
