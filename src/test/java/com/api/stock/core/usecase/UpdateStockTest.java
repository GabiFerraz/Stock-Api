package com.api.stock.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.core.usecase.exception.StockNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateStockTest {

  private final StockGateway stockGateway = mock(StockGateway.class);
  private final UpdateStock updateStock = new UpdateStock(this.stockGateway);

  @Test
  void shouldUpdateStockSuccessfully() {
    final var productSku = "BOLA-123-ABC";
    final var quantity = 10;
    final var stock = new Stock(1, "BOLA-123-ABC", 10);
    final ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
    final var stockUpdated = new Stock(1, "BOLA-123-ABC", 20);

    when(this.stockGateway.findByProductSku(productSku)).thenReturn(Optional.of(stock));
    when(this.stockGateway.update(stockCaptor.capture())).thenReturn(stockUpdated);

    final var response = this.updateStock.execute(productSku, quantity);

    assertThat(response).usingRecursiveComparison().isEqualTo(stockUpdated);

    verify(this.stockGateway).findByProductSku(productSku);

    final var stockCaptured = stockCaptor.getValue();
    verify(this.stockGateway).update(stockCaptured);

    assertThat(stockCaptured.getProductSku()).isEqualTo(productSku);
    assertThat(stockCaptured.getAvailableQuantity()).isEqualTo(quantity);
  }

  @Test
  void shouldThrowExceptionWhenStockNotFound() {
    final var productSku = "BOLA-123-ABC";
    final var quantity = 10;

    when(this.stockGateway.findByProductSku(productSku)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.updateStock.execute(productSku, quantity))
        .isInstanceOf(StockNotFoundException.class)
        .hasMessage("Stock for sku=[" + productSku + "] not found.");

    verify(this.stockGateway).findByProductSku(productSku);
    verifyNoMoreInteractions(this.stockGateway);
  }
}
