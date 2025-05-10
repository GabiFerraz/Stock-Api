package com.api.stock.core.domain;

import static java.lang.String.format;

import com.api.stock.core.domain.exception.DomainException;
import com.api.stock.core.domain.valueobject.ValidationDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Stock {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain client";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";
  private static final String NEGATIVE_MESSAGE_ERROR = "Field=[%s] should be greater than zero";

  private Integer id;
  private String productSku;
  private Integer quantity;

  public Stock() {}

  public Stock(final Integer id, final String productSku, final Integer quantity) {

    validateDomain(productSku, quantity);

    this.id = id;
    this.productSku = productSku;
    this.quantity = quantity;
  }

  public static Stock createStock(final String productSku, final Integer quantity) {
    validateDomain(productSku, quantity);

    return new Stock(null, productSku, quantity);
  }

  public Integer getId() {
    return id;
  }

  public String getProductSku() {
    return productSku;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(final Integer quantity) {
    this.quantity = quantity;
  }

  private static void validateDomain(final String productSku, final Integer quantity) {
    final List<ValidationDomain<?>> rules =
        List.of(
            new ValidationDomain<>(
                productSku,
                format(BLANK_MESSAGE_ERROR, "name"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                quantity, String.format(BLANK_MESSAGE_ERROR, "quantity"), List.of(Objects::isNull)),
            new ValidationDomain<>(
                quantity,
                String.format(NEGATIVE_MESSAGE_ERROR, "quantity"),
                List.of(q -> q != null && q <= 0)));

    final var errors = validate(rules);

    if (!errors.isEmpty()) {
      throw new DomainException(errors);
    }
  }

  private static List<String> validate(final List<ValidationDomain<?>> validations) {
    return validations.stream()
        .filter(Stock::isInvalid)
        .map(it -> format("%s %s", it.message(), DOMAIN_MESSAGE_ERROR))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static <T> boolean isInvalid(final ValidationDomain<T> domain) {
    return domain.predicates().stream().anyMatch(predicate -> predicate.test(domain.field()));
  }
}
