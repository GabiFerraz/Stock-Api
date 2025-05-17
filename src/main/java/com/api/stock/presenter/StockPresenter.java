package com.api.stock.presenter;

import com.api.stock.core.domain.Stock;
import com.api.stock.presenter.response.StockPresenterResponse;
import org.springframework.stereotype.Component;

@Component
public class StockPresenter {

  public StockPresenterResponse parseToResponse(final Stock stock) {
    return StockPresenterResponse.builder()
        .id(stock.getId())
        .productSku(stock.getProductSku())
        .quantity(stock.getAvailableQuantity())
        .build();
  }
}
