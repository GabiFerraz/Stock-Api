package com.api.stock.core.usecase;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.core.usecase.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateStock {

  private final StockGateway stockGateway;

  public Stock execute(final String productSku, final int quantity) {
    final var existingStock =
        this.stockGateway
            .findByProductSku(productSku)
            .orElseThrow(() -> new StockNotFoundException(productSku));

    existingStock.setAvailableQuantity(quantity);

    return this.stockGateway.update(existingStock);
  }
}
