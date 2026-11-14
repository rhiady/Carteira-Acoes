package com.carteiraacoesbackend.security;

import com.carteiraacoesbackend.dto.ErroResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper objectMapper;

    public RestAccessDeniedHandler(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErroResponse error =
                new ErroResponse(
                        OffsetDateTime.now(ZoneOffset.UTC),
                        HttpServletResponse.SC_FORBIDDEN,
                        "FORBIDDEN",
                        "Acesso negado.",
                        request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
