package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Papel;

public record UsuarioResponse(Long id, String login, Papel papel, Boolean ativo) {}
