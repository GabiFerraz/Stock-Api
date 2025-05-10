package com.api.stock.core.usecase;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.dto.StockDto;
import com.api.stock.core.gateway.ProductApiGateway;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.core.usecase.exception.ProductNotFoundException;
import com.api.stock.core.usecase.exception.StockAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateStock {

  private final ProductApiGateway productApiGateway;
  private final StockGateway stockGateway;

  public Stock execute(final StockDto request) {
    final var productDetails = this.productApiGateway.getProductDetails(request.productSku());

    if (productDetails == null) {
      throw new ProductNotFoundException(request.productSku());
    }

    final var stock = this.stockGateway.findByProductSku(request.productSku());

    if (stock.isPresent()) {
      throw new StockAlreadyExistsException(request.productSku());
    }

    final var buildDomain = Stock.createStock(request.productSku(), request.quantity());

    return this.stockGateway.save(buildDomain);
  }
}
