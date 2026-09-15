package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.exceptions.ApiException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class PageableSupport {
  private PageableSupport() {}

  static Pageable translate(Pageable pageable, Map<String, String> publicFields, Sort defaultSort) {
    Sort translated =
        pageable.getSort().isSorted()
            ? translateSort(pageable.getSort(), publicFields)
            : defaultSort;
    if (pageable.isUnpaged()) {
      return Pageable.unpaged(translated);
    }
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), translated);
  }

  private static Sort translateSort(Sort sort, Map<String, String> publicFields) {
    List<Sort.Order> orders =
        sort.stream().map(order -> translateOrder(order, publicFields)).toList();
    return Sort.by(orders);
  }

  private static Sort.Order translateOrder(Sort.Order order, Map<String, String> publicFields) {
    String property = publicFields.get(order.getProperty());
    if (property == null) {
      throw ApiException.badRequest(
          "VALIDATION_ERROR",
          "Campo de ordenação inválido. Campos permitidos: "
              + String.join(", ", new TreeSet<>(publicFields.keySet()))
              + ".");
    }
    Sort.Order translated = new Sort.Order(order.getDirection(), property, order.getNullHandling());
    return order.isIgnoreCase() ? translated.ignoreCase() : translated;
  }
}
