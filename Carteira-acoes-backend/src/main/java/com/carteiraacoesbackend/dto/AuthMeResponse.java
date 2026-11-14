package com.carteiraacoesbackend.dto;

import java.util.List;

public record AuthMeResponse(String username, List<String> authorities) {}
