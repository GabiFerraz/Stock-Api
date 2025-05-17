package com.api.stock.core.domain;

import static java.lang.String.format;

import com.api.stock.core.domain.exception.DomainException;
import com.api.stock.core.domain.valueobject.ValidationDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Stock {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain stock";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";
  private static final String NEGATIVE_MESSAGE_ERROR = "Field=[%s] should be greater than zero";

  private Integer id;
  private String productSku;
  private Integer availableQuantity;

  public Stock() {}

  public Stock(final Integer id, final String productSku, final Integer availableQuantity) {

    validateDomain(productSku, availableQuantity);

    this.id = id;
    this.productSku = productSku;
    this.availableQuantity = availableQuantity;
  }

  public static Stock createStock(final String productSku, final Integer availableQuantity) {
    validateDomain(productSku, availableQuantity);

    return new Stock(null, productSku, availableQuantity);
  }

  public Integer getId() {
    return id;
  }

  public String getProductSku() {
    return productSku;
  }

  public Integer getAvailableQuantity() {
    return availableQuantity;
  }

  public void setAvailableQuantity(final Integer availableQuantity) {
    this.availableQuantity = availableQuantity;
  }

  public boolean reserve(int quantity) {
    if (quantity <= 0 || quantity > availableQuantity) {
      return false;
    }

    availableQuantity -= quantity;
    return true;
  }

  public void release(int quantity) {
    if (quantity > 0) {
      availableQuantity += quantity;
    }
  }

  private static void validateDomain(final String productSku, final Integer availableQuantity) {
    final List<ValidationDomain<?>> rules =
        List.of(
            new ValidationDomain<>(
                productSku,
                format(BLANK_MESSAGE_ERROR, "product_sku"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                availableQuantity,
                String.format(BLANK_MESSAGE_ERROR, "available_quantity"),
                List.of(Objects::isNull)),
            new ValidationDomain<>(
                availableQuantity,
                String.format(NEGATIVE_MESSAGE_ERROR, "available_quantity"),
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
