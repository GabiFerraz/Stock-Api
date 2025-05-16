package com.api.stock.core.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.core.usecase.exception.StockNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeleteStockTest {

  private final StockGateway stockGateway = mock(StockGateway.class);
  private final DeleteStock deleteStock = new DeleteStock(this.stockGateway);

  @Test
  void shouldDeleteStockSuccessfully() {
    final var productSku = "BOLA-123-ABC";
    final var stock = new Stock(1, "BOLA-123-ABC", 10);

    when(this.stockGateway.findByProductSku(productSku)).thenReturn(Optional.of(stock));
    doNothing().when(this.stockGateway).deleteByProductSku(productSku);

    this.deleteStock.execute(productSku);

    verify(this.stockGateway).findByProductSku(productSku);
    verify(this.stockGateway).deleteByProductSku(productSku);
  }

  @Test
  void shouldNotDeleteStockWhenStockNotFound() {
    final var productSku = "BOLA-123-ABC";

    when(this.stockGateway.findByProductSku(productSku)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.deleteStock.execute(productSku))
        .isInstanceOf(StockNotFoundException.class)
        .hasMessage("Stock for sku=[" + productSku + "] not found.");

    verify(this.stockGateway).findByProductSku(productSku);
    verifyNoMoreInteractions(this.stockGateway);
  }
}
