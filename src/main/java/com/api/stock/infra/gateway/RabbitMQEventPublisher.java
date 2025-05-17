package com.api.stock.infra.gateway;

import com.api.stock.config.RabbitMQConfig;
import com.api.stock.core.gateway.EventPublisher;
import com.api.stock.infra.gateway.exception.GatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEventPublisher implements EventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Object event) {
    try {
      rabbitTemplate.convertAndSend(
          RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.STOCK_RESERVED_QUEUE, event);
    } catch (Exception e) {
      log.error("Failed to publish event: {}", event, e);
      throw new GatewayException("Failed to publish event: " + event);
    }
  }
}
