package com.carteiraacoesbackend.dto;

import java.time.OffsetDateTime;

public record ErroResponse(
    OffsetDateTime timestamp, int status, String error, String message, String path) {}
