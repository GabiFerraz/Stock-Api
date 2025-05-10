package com.api.stock.core.usecase;

import com.api.stock.core.gateway.StockGateway;
import com.api.stock.core.usecase.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteStock {

  private final StockGateway stockGateway;

  @Transactional
  public void execute(final String productSku) {
    this.stockGateway
        .findByProductSku(productSku)
        .orElseThrow(() -> new StockNotFoundException(productSku));
    this.stockGateway.deleteByProductSku(productSku);
  }
}
