package com.api.stock.infra.gateway;

import static java.lang.String.format;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.infra.gateway.exception.GatewayException;
import com.api.stock.infra.persistence.entity.StockEntity;
import com.api.stock.infra.persistence.repository.StockRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockGatewayImpl implements StockGateway {

  private static final String SAVE_ERROR_MESSAGE = "Error saving stock for sku=[%s].";
  private static final String FIND_ERROR_MESSAGE = "Stock for sku=[%s] not found.";
  private static final String UPDATE_ERROR_MESSAGE = "Error updating stock for sku=[%s].";
  private static final String DELETE_ERROR_MESSAGE = "Error deleting stock for sku=[%s].";

  private final StockRepository stockRepository;

  @Override
  public Stock save(final Stock stock) {
    try {
      final var entity =
          StockEntity.builder()
              .productSku(stock.getProductSku())
              .availableQuantity(stock.getAvailableQuantity())
              .build();

      final var saved = this.stockRepository.save(entity);

      return this.toResponse(saved);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(SAVE_ERROR_MESSAGE, stock.getProductSku()));
    }
  }

  @Override
  public Optional<Stock> findByProductSku(final String productSku) {
    try {
      final var entity = this.stockRepository.findByProductSku(productSku);

      return entity.map(this::toResponse);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(FIND_ERROR_MESSAGE, productSku));
    }
  }

  @Override
  public Stock update(final Stock stock) {
    try {
      final var entity =
          this.stockRepository
              .findByProductSku(stock.getProductSku())
              .orElseThrow(
                  () -> new GatewayException(format(FIND_ERROR_MESSAGE, stock.getProductSku())));

      entity.setAvailableQuantity(stock.getAvailableQuantity());

      final var saved = this.stockRepository.save(entity);

      return this.toResponse(saved);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(UPDATE_ERROR_MESSAGE, stock.getProductSku()));
    }
  }

  @Override
  public void deleteByProductSku(final String productSku) {
    try {
      this.stockRepository.deleteByProductSku(productSku);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(DELETE_ERROR_MESSAGE, productSku));
    }
  }

  private Stock toResponse(final StockEntity entity) {
    return new Stock(entity.getId(), entity.getProductSku(), entity.getAvailableQuantity());
  }
}
