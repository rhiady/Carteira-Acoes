package com.carteiraacoesbackend.dto;

import java.util.List;

public record AuthMeResponse(String login, List<String> authorities) {}
