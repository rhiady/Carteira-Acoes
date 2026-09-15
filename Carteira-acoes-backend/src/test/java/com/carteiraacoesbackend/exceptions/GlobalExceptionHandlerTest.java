package com.carteiraacoesbackend.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.dto.ErroResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

  @Test
  void returnsStandardErrorBodyForBusinessErrors() {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/operacoes/vendas");
    var response =
        new GlobalExceptionHandler()
            .handleApi(
                ApiException.unprocessable("QUANTIDADE_INSUFICIENTE", "Quantidade indisponível."),
                request);

    ErroResponse body = response.getBody();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(body).isNotNull();
    assertThat(body.error()).isEqualTo("QUANTIDADE_INSUFICIENTE");
    assertThat(body.path()).isEqualTo("/operacoes/vendas");
    assertThat(body.timestamp()).isNotNull();
  }

  @Test
  void returnsSafeConflictForConcurrentModification() {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/operacoes/vendas");
    var response =
        new GlobalExceptionHandler()
            .handleOptimisticLock(
                new OptimisticLockingFailureException("internal persistence details"), request);

    ErroResponse body = response.getBody();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(body).isNotNull();
    assertThat(body.error()).isEqualTo("CONCURRENT_MODIFICATION");
    assertThat(body.message()).doesNotContain("persistence");
    assertThat(body.path()).isEqualTo("/operacoes/vendas");
  }
}
