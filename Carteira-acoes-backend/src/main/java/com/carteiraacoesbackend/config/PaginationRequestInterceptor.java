package com.carteiraacoesbackend.config;

import com.carteiraacoesbackend.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PaginationRequestInterceptor implements HandlerInterceptor {
  static final int MAX_PAGE_SIZE = 100;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    validateInteger(request, "page", 0, Integer.MAX_VALUE);
    validateInteger(request, "size", 1, MAX_PAGE_SIZE);
    validateSortSyntax(request);
    return true;
  }

  private void validateInteger(HttpServletRequest request, String name, int minimum, int maximum) {
    String[] values = request.getParameterValues(name);
    if (values == null) {
      return;
    }
    if (values.length != 1) {
      invalidPagination();
    }
    try {
      int value = Integer.parseInt(values[0]);
      if (value < minimum || value > maximum) {
        invalidPagination();
      }
    } catch (NumberFormatException exception) {
      invalidPagination();
    }
  }

  private void validateSortSyntax(HttpServletRequest request) {
    String[] values = request.getParameterValues("sort");
    if (values == null) {
      return;
    }
    for (String value : values) {
      String[] parts = value.split(",", -1);
      if (parts.length == 0 || parts.length > 2 || parts[0].isBlank()) {
        invalidSort();
      }
      if (parts.length == 2) {
        String direction = parts[1].trim().toLowerCase(Locale.ROOT);
        if (!direction.equals("asc") && !direction.equals("desc")) {
          invalidSort();
        }
      }
    }
  }

  private void invalidPagination() {
    throw ApiException.badRequest(
        "INVALID_PAGINATION",
        "Paginação inválida: page deve ser maior ou igual a 0 e size deve estar entre 1 e 100.");
  }

  private void invalidSort() {
    throw ApiException.badRequest(
        "INVALID_SORT", "Ordenação inválida: use sort=campo,asc ou sort=campo,desc.");
  }
}
