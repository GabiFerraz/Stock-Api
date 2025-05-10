package com.api.stock.core.usecase;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.gateway.StockGateway;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchStock {

  private final StockGateway stockGateway;

  public Optional<Stock> execute(final String productSku) {
    return this.stockGateway.findByProductSku(productSku);
  }
}
