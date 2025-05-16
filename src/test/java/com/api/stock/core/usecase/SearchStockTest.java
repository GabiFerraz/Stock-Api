package com.api.stock.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.gateway.StockGateway;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchStockTest {

  private final StockGateway stockGateway = mock(StockGateway.class);
  private final SearchStock searchStock = new SearchStock(this.stockGateway);

  @Test
  void shouldSearchStockSuccessfully() {
    final var productSku = "BOLA-123-ABC";
    final var stock = new Stock(1, "BOLA-123-ABC", 10);

    when(this.stockGateway.findByProductSku(productSku)).thenReturn(Optional.of(stock));

    final var response = this.searchStock.execute(productSku);

    assertThat(response).isPresent();
    assertThat(response.get()).usingRecursiveComparison().isEqualTo(stock);

    verify(this.stockGateway).findByProductSku(productSku);
  }

  @Test
  void shouldReturnEmptyWhenStockNotFound() {
    final var productSku = "BOLA-123-ABC";

    when(this.stockGateway.findByProductSku(productSku)).thenReturn(Optional.empty());

    final var response = this.searchStock.execute(productSku);

    assertThat(response).isEmpty();

    verify(this.stockGateway).findByProductSku(productSku);
  }
}
