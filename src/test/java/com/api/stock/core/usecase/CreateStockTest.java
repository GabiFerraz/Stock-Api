package com.api.stock.core.usecase;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.dto.StockDto;
import com.api.stock.core.gateway.ProductApiGateway;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.core.gateway.response.ProductDetailsResponse;
import com.api.stock.core.usecase.exception.ProductNotFoundException;
import com.api.stock.core.usecase.exception.StockAlreadyExistsException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateStockTest {

  private final StockGateway stockGateway = mock(StockGateway.class);
  private final ProductApiGateway productApiGateway = mock(ProductApiGateway.class);
  private final CreateStock createStock =
      new CreateStock(this.stockGateway, this.productApiGateway);

  @Test
  void shouldCreateStockSuccessfully() {
    final var request = new StockDto("BOLA-123-ABC", 10);
    final var product =
        ProductDetailsResponse.builder().name("Bola de futebol").sku("BOLA-123-ABC").build();
    final ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
    final var stock = new Stock(1, "BOLA-123-ABC", 10);

    when(this.stockGateway.findByProductSku(request.productSku())).thenReturn(empty());
    when(this.productApiGateway.getProductDetails(request.productSku())).thenReturn(product);
    when(this.stockGateway.save(stockCaptor.capture())).thenReturn(stock);

    final var response = this.createStock.execute(request);

    assertThat(response).usingRecursiveComparison().isEqualTo(stock);

    verify(this.stockGateway).findByProductSku(request.productSku());
    verify(this.productApiGateway).getProductDetails(request.productSku());

    final var stockCaptured = stockCaptor.getValue();
    verify(this.stockGateway).save(stockCaptured);

    assertThat(stockCaptured.getProductSku()).isEqualTo(request.productSku());
    assertThat(stockCaptured.getAvailableQuantity()).isEqualTo(request.quantity());
  }

  @Test
  void shouldNotCreateStockWhenStockAlreadyExists() {
    final var request = new StockDto("BOLA-123-ABC", 10);
    final var stock = new Stock(1, "BOLA-123-ABC", 10);

    when(this.stockGateway.findByProductSku(request.productSku())).thenReturn(Optional.of(stock));

    assertThatThrownBy(() -> this.createStock.execute(request))
        .isInstanceOf(StockAlreadyExistsException.class)
        .hasMessage("Stock for sku=[BOLA-123-ABC] already exists.");

    verify(this.stockGateway).findByProductSku(request.productSku());
    verifyNoInteractions(this.productApiGateway);
    verifyNoMoreInteractions(this.stockGateway);
  }

  @Test
  void shouldNotCreateStockWhenProductNotFound() {
    final var request = new StockDto("BOLA-123-ABC", 10);

    when(this.stockGateway.findByProductSku(request.productSku())).thenReturn(empty());
    when(this.productApiGateway.getProductDetails(request.productSku())).thenReturn(null);

    assertThatThrownBy(() -> this.createStock.execute(request))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("Product with sku=[BOLA-123-ABC] not found in Product-API.");

    verify(this.stockGateway).findByProductSku(request.productSku());
    verify(this.productApiGateway).getProductDetails(request.productSku());
    verifyNoMoreInteractions(this.stockGateway);
  }
}
