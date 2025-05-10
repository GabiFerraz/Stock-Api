package com.api.stock.core.gateway;

import com.api.stock.core.domain.Stock;
import java.util.Optional;

public interface StockGateway {

  Stock save(final Stock stock);

  Optional<Stock> findByProductSku(final String productSku);

  Stock update(final Stock stock);

  void deleteByProductSku(final String productSku);
}
