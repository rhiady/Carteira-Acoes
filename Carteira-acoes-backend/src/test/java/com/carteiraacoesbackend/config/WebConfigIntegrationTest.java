package com.carteiraacoesbackend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebConfigIntegrationTest {

  private static final String ALLOWED_ORIGIN = "http://localhost:5173";

  @Autowired private MockMvc mockMvc;

  @Test
  void allowsConfiguredFrontendOrigin() throws Exception {
    mockMvc
        .perform(get("/usuarios").header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN))
        .andExpect(status().isUnauthorized())
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
  }

  @Test
  void answersPreflightForAllowedOperation() throws Exception {
    mockMvc
        .perform(
            options("/usuarios")
                .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
        .andExpect(
            header()
                .string(
                    HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                    org.hamcrest.Matchers.containsString("POST")));
  }

  @Test
  void rejectsUnconfiguredOrigin() throws Exception {
    mockMvc
        .perform(get("/usuarios").header(HttpHeaders.ORIGIN, "https://untrusted.example"))
        .andExpect(status().isForbidden())
        .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
  }
}
