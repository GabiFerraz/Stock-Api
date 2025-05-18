package com.api.stock.core.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.api.stock.core.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StockTest {

  @Test
  void shouldCreateStockSuccessfully() {
    final var stock = Stock.createStock("BOLA-123-ABC", 10);

    assert stock.getProductSku().equals("BOLA-123-ABC");
    assert stock.getAvailableQuantity().equals(10);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void shouldNotCreateStockWithInvalidProductSku(final String productSku) {
    assertThatThrownBy(() -> Stock.createStock(productSku, 10))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[product_sku] should not be empty or null by domain stock");
  }

  @ParameterizedTest
  @ValueSource(ints = {-1})
  void shouldNotCreateStockWithInvalidQuantity(final int quantity) {
    assertThatThrownBy(() -> Stock.createStock("BOLA-123-ABC", quantity))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[available_quantity] should not be negative by domain stock");
  }
}
