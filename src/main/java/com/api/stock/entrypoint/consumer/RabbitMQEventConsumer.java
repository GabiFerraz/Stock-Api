package com.api.stock.entrypoint.consumer;

import com.api.stock.config.RabbitMQConfig;
import com.api.stock.core.gateway.EventPublisher;
import com.api.stock.core.usecase.StockService;
import com.api.stock.event.ReleaseStockEvent;
import com.api.stock.event.ReserveStockEvent;
import com.api.stock.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventConsumer {

  private final StockService stockService;
  private final EventPublisher eventPublisher;

  @RabbitListener(queues = RabbitMQConfig.RESERVE_STOCK_QUEUE)
  public void consumeReserveStockEvent(ReserveStockEvent event) {
    boolean success =
        stockService.reserveStock(event.productSku(), event.quantity(), event.orderId());

    eventPublisher.publish(new StockReservedEvent(event.orderId(), success));
  }

  @RabbitListener(queues = RabbitMQConfig.RELEASE_STOCK_QUEUE)
  public void consumeReleaseStockEvent(ReleaseStockEvent event) {
    stockService.releaseStock(event.productSku(), event.quantity());
  }
}
