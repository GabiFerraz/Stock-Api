package com.api.stock.entrypoint.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.api.stock.core.gateway.EventPublisher;
import com.api.stock.core.usecase.StockService;
import com.api.stock.event.ReleaseStockEvent;
import com.api.stock.event.ReserveStockEvent;
import com.api.stock.event.StockReservedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RabbitMQEventConsumerTest {

  private final StockService stockService = mock(StockService.class);
  private final EventPublisher eventPublisher = mock(EventPublisher.class);
  private final RabbitMQEventConsumer eventConsumer =
      new RabbitMQEventConsumer(stockService, eventPublisher);

  @Test
  void shouldReserveStockSuccessfullyAndPublishEvent() {
    final var event = new ReserveStockEvent("order-123", "sku-123", 10);

    when(stockService.reserveStock(any(String.class), anyInt(), any(String.class)))
        .thenReturn(true);

    eventConsumer.consumeReserveStockEvent(event);

    final ArgumentCaptor<String> skuCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<Integer> quantityCaptor = ArgumentCaptor.forClass(Integer.class);
    final ArgumentCaptor<String> orderIdCaptor = ArgumentCaptor.forClass(String.class);

    verify(stockService)
        .reserveStock(skuCaptor.capture(), quantityCaptor.capture(), orderIdCaptor.capture());

    assertThat(skuCaptor.getValue()).isEqualTo(event.productSku());
    assertThat(quantityCaptor.getValue()).isEqualTo(event.quantity());
    assertThat(orderIdCaptor.getValue()).isEqualTo(event.orderId());

    final ArgumentCaptor<StockReservedEvent> eventCaptor =
        ArgumentCaptor.forClass(StockReservedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    assertThat(eventCaptor.getValue().orderId()).isEqualTo(event.orderId());
    assertThat(eventCaptor.getValue().success()).isTrue();
  }

  @Test
  void shouldHandleFailedStockReservationAndPublishEvent() {
    final var event = new ReserveStockEvent("order-123", "sku-123", 10);

    when(stockService.reserveStock(any(String.class), anyInt(), any(String.class)))
        .thenReturn(false);

    eventConsumer.consumeReserveStockEvent(event);

    final ArgumentCaptor<String> skuCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<Integer> quantityCaptor = ArgumentCaptor.forClass(Integer.class);
    final ArgumentCaptor<String> orderIdCaptor = ArgumentCaptor.forClass(String.class);

    verify(stockService)
        .reserveStock(skuCaptor.capture(), quantityCaptor.capture(), orderIdCaptor.capture());

    assertThat(skuCaptor.getValue()).isEqualTo(event.productSku());
    assertThat(quantityCaptor.getValue()).isEqualTo(event.quantity());
    assertThat(orderIdCaptor.getValue()).isEqualTo(event.orderId());

    final ArgumentCaptor<StockReservedEvent> eventCaptor =
        ArgumentCaptor.forClass(StockReservedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    assertThat(eventCaptor.getValue().orderId()).isEqualTo(event.orderId());
    assertThat(eventCaptor.getValue().success()).isFalse();
  }

  @Test
  void shouldReleaseStockSuccessfully() {
    final var event = new ReleaseStockEvent("sku-123", 10);

    eventConsumer.consumeReleaseStockEvent(event);

    final ArgumentCaptor<String> skuCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<Integer> quantityCaptor = ArgumentCaptor.forClass(Integer.class);

    verify(stockService).releaseStock(skuCaptor.capture(), quantityCaptor.capture());

    assertThat(skuCaptor.getValue()).isEqualTo(event.productSku());
    assertThat(quantityCaptor.getValue()).isEqualTo(event.quantity());
  }
}
