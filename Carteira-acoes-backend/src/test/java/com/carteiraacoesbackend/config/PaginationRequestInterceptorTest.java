package com.carteiraacoesbackend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

class PaginationRequestInterceptorTest {

  private final PaginationRequestInterceptor interceptor = new PaginationRequestInterceptor();
  private final HttpServletResponse response = mock(HttpServletResponse.class);

  @Test
  void acceptsMissingAndValidPaginationAndSort() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameterValues("sort")).thenReturn(new String[] {"ticker,asc", "id,desc"});
    assertThat(interceptor.preHandle(request, response, new Object())).isTrue();
  }

  @Test
  void rejectsInvalidPageAndSizeValues() {
    assertPaginationError("page", new String[] {"-1"});
    assertPaginationError("page", new String[] {"x"});
    assertPaginationError("page", new String[] {"1", "2"});
    assertPaginationError("size", new String[] {"0"});
    assertPaginationError("size", new String[] {"101"});
    assertPaginationError("size", new String[] {"x"});
  }

  @Test
  void rejectsInvalidSortSyntax() {
    assertSortError(new String[] {",asc"});
    assertSortError(new String[] {"ticker,asc,extra"});
    assertSortError(new String[] {"ticker,sideways"});
  }

  private void assertPaginationError(String name, String[] values) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameterValues(name)).thenReturn(values);
    ApiException error =
        assertThrows(
            ApiException.class, () -> interceptor.preHandle(request, response, new Object()));
    assertThat(error.getCode()).isEqualTo("INVALID_PAGINATION");
  }

  private void assertSortError(String[] values) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameterValues("sort")).thenReturn(values);
    ApiException error =
        assertThrows(
            ApiException.class, () -> interceptor.preHandle(request, response, new Object()));
    assertThat(error.getCode()).isEqualTo("INVALID_SORT");
  }
}
