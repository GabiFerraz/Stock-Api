package com.api.stock.core.usecase;

import com.api.stock.core.gateway.EventPublisher;
import com.api.stock.core.gateway.StockGateway;
import com.api.stock.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockService {

  private final StockGateway stockGateway;
  private final EventPublisher eventPublisher;

  public boolean reserveStock(String sku, int quantity, String orderId) {
    final var stock = stockGateway.findByProductSku(sku);

    if (stock.isEmpty()) {
      return false;
    }

    final var stockFound = stock.get();
    boolean reserved = stockFound.reserve(quantity);

    if (reserved) {
      this.stockGateway.update(stockFound);
    }

    eventPublisher.publish(new StockReservedEvent(orderId, reserved));

    return reserved;
  }

  public void releaseStock(String sku, int quantity) {
    final var stock = stockGateway.findByProductSku(sku);

    if (stock.isPresent()) {
      final var stockFound = stock.get();
      stockFound.release(quantity);

      this.stockGateway.update(stockFound);
    }
  }
}
