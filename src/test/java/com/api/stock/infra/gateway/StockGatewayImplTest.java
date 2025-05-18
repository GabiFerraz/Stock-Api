package com.api.stock.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.stock.core.domain.Stock;
import com.api.stock.infra.gateway.exception.GatewayException;
import com.api.stock.infra.persistence.entity.StockEntity;
import com.api.stock.infra.persistence.repository.StockRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StockGatewayImplTest {

  private final StockRepository stockRepository = mock(StockRepository.class);
  private final StockGatewayImpl stockGateway = new StockGatewayImpl(this.stockRepository);

  @Test
  void shouldSaveStockSuccessfully() {
    final var entity =
        StockEntity.builder().productSku("BOLA-123-ABC").availableQuantity(10).build();
    final var entityResponse =
        StockEntity.builder().id(1).productSku("BOLA-123-ABC").availableQuantity(10).build();
    final var stockDomain = new Stock(1, "BOLA-123-ABC", 10);
    final ArgumentCaptor<StockEntity> entityCaptor = ArgumentCaptor.forClass(StockEntity.class);

    when(this.stockRepository.save(entityCaptor.capture())).thenReturn(entityResponse);

    final var response = this.stockGateway.save(stockDomain);

    assertThat(response.getId()).isEqualTo(entityResponse.getId());
    assertThat(response.getProductSku()).isEqualTo(entityResponse.getProductSku());
    assertThat(response.getAvailableQuantity()).isEqualTo(entityResponse.getAvailableQuantity());

    final var entityCaptured = entityCaptor.getValue();
    verify(this.stockRepository).save(entityCaptured);

    assertThat(entityCaptured.getId()).isNull();
    assertThat(entityCaptured.getProductSku()).isEqualTo(entity.getProductSku());
    assertThat(entityCaptured.getAvailableQuantity()).isEqualTo(entity.getAvailableQuantity());
  }

  @Test
  void shouldThrowExceptionWhenOccursErrorSavingStock() {
    final var stockDomain = new Stock(1, "BOLA-123-ABC", 10);

    when(this.stockRepository.save(any())).thenThrow(IllegalArgumentException.class);

    assertThatThrownBy(() -> this.stockGateway.save(stockDomain))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error saving stock for sku=[BOLA-123-ABC].");

    verify(this.stockRepository).save(any());
  }

  @Test
  void shouldFindStockSuccessfully() {
    final var productSku = "BOLA-123-ABC";
    final var entity =
        StockEntity.builder().id(1).productSku(productSku).availableQuantity(10).build();

    when(this.stockRepository.findByProductSku(productSku)).thenReturn(Optional.of(entity));

    final var response = this.stockGateway.findByProductSku(productSku);

    assertThat(response).isPresent();
    assertThat(response.get()).usingRecursiveComparison().isEqualTo(entity);

    verify(this.stockRepository).findByProductSku(productSku);
  }

  @Test
  void shouldReturnEmptyWhenStockNotFound() {
    final var productSku = "BOLA-123-ABC";

    when(this.stockRepository.findByProductSku(productSku)).thenReturn(Optional.empty());

    final var response = this.stockGateway.findByProductSku(productSku);

    assertThat(response).isEmpty();

    verify(this.stockRepository).findByProductSku(productSku);
  }

  @Test
  void shouldThrowExceptionWhenOccursErrorFindingStock() {
    final var productSku = "BOLA-123-ABC";

    when(this.stockRepository.findByProductSku(productSku))
        .thenThrow(IllegalArgumentException.class);

    assertThatThrownBy(() -> this.stockGateway.findByProductSku(productSku))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Stock for sku=[BOLA-123-ABC] not found.");

    verify(this.stockRepository).findByProductSku(productSku);
  }

  @Test
  void shouldUpdateStockSuccessfully() {
    final var entityFound =
        StockEntity.builder().id(1).productSku("BOLA-123-ABC").availableQuantity(10).build();
    final var entityUpdated =
        StockEntity.builder().id(1).productSku("BOLA-123-ABC").availableQuantity(20).build();
    final var stockUpdated = new Stock(1, "BOLA-123-ABC", 20);
    final ArgumentCaptor<StockEntity> entityCaptor = ArgumentCaptor.forClass(StockEntity.class);

    doReturn(Optional.of(entityFound)).when(this.stockRepository).findByProductSku("BOLA-123-ABC");
    when(this.stockRepository.save(entityCaptor.capture())).thenReturn(entityUpdated);

    final var response = this.stockGateway.update(stockUpdated);

    assertThat(response.getId()).isEqualTo(entityUpdated.getId());
    assertThat(response.getProductSku()).isEqualTo(entityUpdated.getProductSku());
    assertThat(response.getAvailableQuantity()).isEqualTo(entityUpdated.getAvailableQuantity());

    final var entityCaptured = entityCaptor.getValue();
    verify(this.stockRepository).save(entityCaptured);

    assertThat(entityCaptured.getId()).isEqualTo(entityFound.getId());
    assertThat(entityCaptured.getProductSku()).isEqualTo(entityFound.getProductSku());
    assertThat(entityCaptured.getAvailableQuantity())
        .isEqualTo(entityUpdated.getAvailableQuantity());
  }

  @Test
  void shouldThrowExceptionWhenUpdateStockAndStockNotFound() {
    final var stockUpdated = new Stock(1, "BOLA-123-ABC", 20);

    when(this.stockRepository.findByProductSku(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.stockGateway.update(stockUpdated))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Stock for sku=[BOLA-123-ABC] not found.");

    verify(this.stockRepository).findByProductSku(any());
  }

  @Test
  void shouldThrowExceptionWhenOccursErrorUpdatingStock() {
    final var entityFound =
        StockEntity.builder().id(1).productSku("BOLA-123-ABC").availableQuantity(10).build();
    final var stockUpdated = new Stock(1, "BOLA-123-ABC", 20);

    doReturn(Optional.of(entityFound)).when(this.stockRepository).findByProductSku(any());
    when(this.stockRepository.save(any())).thenThrow(IllegalArgumentException.class);

    assertThatThrownBy(() -> this.stockGateway.update(stockUpdated))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error updating stock for sku=[BOLA-123-ABC].");

    verify(this.stockRepository).save(any());
  }

  @Test
  void shouldDeleteStockSuccessfully() {
    final var productSku = "BOLA-123-ABC";

    this.stockGateway.deleteByProductSku(productSku);

    verify(this.stockRepository).deleteByProductSku(productSku);
  }

  @Test
  void shouldThrowExceptionWhenOccursErrorDeletingStock() {
    final var productSku = "BOLA-123-ABC";

    doThrow(IllegalArgumentException.class)
        .when(this.stockRepository)
        .deleteByProductSku(productSku);

    assertThatThrownBy(() -> this.stockGateway.deleteByProductSku(productSku))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error deleting stock for sku=[BOLA-123-ABC].");
  }
}
